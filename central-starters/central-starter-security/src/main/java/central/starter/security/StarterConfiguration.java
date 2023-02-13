/*
 * MIT License
 *
 * Copyright (c) 2022-present Alan Yeh <alan@yeh.cn>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package central.starter.security;

import central.starter.security.exception.UnauthenticatedExceptionHandler;
import central.starter.security.exception.UnauthorizedExceptionHandler;
import central.starter.security.shiro.SecurityRealm;
import central.starter.security.shiro.ShiroFilter;
import central.starter.security.shiro.resolver.ClassAnnotationResolver;
import central.starter.security.shiro.resolver.MethodAnnotationResolver;
import jakarta.servlet.Filter;
import org.apache.shiro.aop.AnnotationResolver;
import org.apache.shiro.authz.aop.*;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.spring.security.interceptor.AopAllianceAnnotationsAuthorizingMethodInterceptor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 安全认证配置
 *
 * @author Alan Yeh
 * @since 2023/02/13
 */
@Configuration
@ConditionalOnProperty(name = "central.security.enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(SecurityProperties.class)
@Import({ShiroFilter.class, SecurityRealm.class, UnauthenticatedExceptionHandler.class, UnauthorizedExceptionHandler.class})
public class StarterConfiguration {

    @Bean
    public SecurityManager securityManager(SecurityRealm realm) {
        // 禁用会话调度器
        var manager = new DefaultSessionManager();
        manager.setSessionValidationSchedulerEnabled(false);

        // 关闭 Shiro 自带的 Session，因为微服务是无状态的，每个请求的会话都是重新生成的
        var subjectDao = new DefaultSubjectDAO();
        var storageEvaluator = new DefaultSessionStorageEvaluator();
        storageEvaluator.setSessionStorageEnabled(false);
        subjectDao.setSessionStorageEvaluator(storageEvaluator);

        // 禁用 RememberMe
        var rememberMeManager = new CookieRememberMeManager();
        rememberMeManager.getCookie().setHttpOnly(true);
        rememberMeManager.getCookie().setMaxAge(0);

        var securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(realm);
        securityManager.setSubjectDAO(subjectDao);
        securityManager.setSessionManager(manager);
        securityManager.setRememberMeManager(rememberMeManager);

        return securityManager;
    }

    /**
     * 配置全局拦载器
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager, ShiroFilter shiroFilter) {
        var filterFactoryBean = new ShiroFilterFactoryBean();

        var filterMap = new HashMap<String, Filter>();
        filterMap.put("jwt", shiroFilter);
        filterFactoryBean.setFilters(filterMap);

        var filterChain = new HashMap<String, String>();
        filterChain.put("/**", "jwt");

        filterFactoryBean.setSecurityManager(securityManager);
        filterFactoryBean.setFilterChainDefinitionMap(filterChain);
        return filterFactoryBean;
    }

    // 使用注解需要的配置
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);

        // 这一段代码的逻辑用于优化 Shiro 的权限校验逻辑
        // Shiro 原来的逻辑是如果方法和类上面有相同的注解，那么就忽略类上面的注解。这个逻辑我认为是不正确的，应该是说，类上面的注解，
        // 对这个类下面所有的方法，都起作用才对。

        // 因此，在不大改 Shiro 的代码的前提下，对切面的处理逻辑做出以下调整
        // 如果同一个注解，如 @RequirePermissions 同时出现在 类上和方法上面，那么 Shiro 会先校验方法上面的权限注解，再校验类上面的权限注解。

        // 自定义注解处理逻辑
        AnnotationResolver methodResolver = new MethodAnnotationResolver();
        AnnotationResolver classResolver = new ClassAnnotationResolver();

        // 参考 org.apache.shiro.spring.security.interceptor.AopAllianceAnnotationsAuthorizingMethodInterceptor 的构造函数逻辑
        List<AuthorizingAnnotationMethodInterceptor> interceptors = new ArrayList<>(10);

        interceptors.add(new RoleAnnotationMethodInterceptor(methodResolver));
        interceptors.add(new RoleAnnotationMethodInterceptor(classResolver));
        interceptors.add(new PermissionAnnotationMethodInterceptor(methodResolver));
        interceptors.add(new PermissionAnnotationMethodInterceptor(classResolver));
        interceptors.add(new AuthenticatedAnnotationMethodInterceptor(methodResolver));
        interceptors.add(new AuthenticatedAnnotationMethodInterceptor(classResolver));
        interceptors.add(new UserAnnotationMethodInterceptor(methodResolver));
        interceptors.add(new UserAnnotationMethodInterceptor(classResolver));
        interceptors.add(new GuestAnnotationMethodInterceptor(methodResolver));
        interceptors.add(new GuestAnnotationMethodInterceptor(classResolver));

        AopAllianceAnnotationsAuthorizingMethodInterceptor interceptor = new AopAllianceAnnotationsAuthorizingMethodInterceptor();
        interceptor.setMethodInterceptors(interceptors);

        advisor.setAdvice(interceptor);
        return advisor;
    }
}
