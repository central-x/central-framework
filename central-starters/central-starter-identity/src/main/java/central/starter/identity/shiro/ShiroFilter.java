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

package central.starter.identity.shiro;

import central.bean.Nullable;
import central.lang.Arrayx;
import central.lang.BooleanEnum;
import central.lang.Stringx;
import central.starter.identity.IdentityProperties;
import central.starter.webmvc.render.JsonRender;
import central.starter.webmvc.render.RedirectRender;
import central.util.Objectx;
import central.web.XForwardedHeaders;
import com.auth0.jwt.JWT;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Setter;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.net.URI;
import java.util.Objects;

/**
 * Shiro 拦载器
 * <p>
 * 此拦截器会将所有请求都拦截下来，然后判断请求的 Header 中是否包含 Authorization 信息，
 * 如果有，则自动登录，并在 Request 的 attributes 中附带帐户相关信息
 *
 * @author Alan Yeh
 * @since 2023/02/13
 */
public class ShiroFilter extends BasicHttpAuthenticationFilter {
    @Setter(onMethod_ = @Autowired)
    private IdentityProperties properties;

    /**
     * 判断用户是否想要登录
     * <p>
     * 主要判断头部里面是否包含 Authorization 字段
     */
    @Override
    protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
        if (request instanceof HttpServletRequest servletRequest) {
            request.setAttribute("accountId", ""); // 帐户主键
            request.setAttribute("admin", false); // 是否管理员（系统管理员，安全管理员、安全保密员）
            request.setAttribute("supervisor", false); // 是否超级管理员
            return Stringx.isNotBlank(this.findJwt(servletRequest));
        }
        return false;
    }

    /**
     * 判断请求是否是退出登录请求
     */
    protected boolean isLogoutAttempt(ServletRequest request, ServletResponse response) {
        if (request instanceof HttpServletRequest servletRequest) {
            if (!"GET".equalsIgnoreCase(servletRequest.getMethod())) {
                return false;
            }

            return servletRequest.getRequestURL().toString().endsWith(this.properties.getLogoutUrl());
        }
        return false;
    }

    private @Nullable String findJwt(HttpServletRequest request) {
        String jwt = null;

        // 看看 Cookie 中有没有
        var cookies = request.getCookies();
        if (Arrayx.isNotEmpty(cookies)) {
            for (var cookie : cookies) {
                if (Objects.equals(this.properties.getCookie(), cookie.getName())) {
                    jwt = cookie.getValue();
                    break;
                }
            }
        }
        if (Stringx.isNotBlank(jwt)) {
            return jwt;
        }

        // 再从 Header 中取
        jwt = request.getHeader(this.properties.getCookie());
        if (Stringx.isNotBlank(jwt)) {
            return jwt;
        }

        // 最后再从参数里取
        return request.getParameter(this.properties.getCookie());
    }

    /**
     * 执行登录操作
     */
    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
        var token = new JsonWebToken(this.findJwt((HttpServletRequest) request));

        // 提交给 realm 进行登录，如果错误会抛出异常并在 isAccessAllowed 方法中被捕获
        var subject = this.getSubject(request, response);
        subject.login(token);

        // 如果没有抛出异常，则代表登录成功，返回 true
        // 解析 JWT 里面的用户信息，将期设置到 RequestAttribute 里
        var jwt = JWT.decode(subject.getPrincipal().toString());
        request.setAttribute("accountId", jwt.getSubject());
        request.setAttribute("admin", jwt.getClaim("su").asBoolean());
        request.setAttribute("supervisor", BooleanEnum.resolve(jwt.getClaim("sa").asString()).getJValue());

        return true;
    }

    /**
     * 执行退出登录操作
     */
    protected void executeLogout(ServletRequest request, ServletResponse response) throws Exception {
        var servletRequest = (HttpServletRequest) request;
        var servletResponse = (HttpServletResponse) response;

        var subject = this.getSubject(request, response);
        subject.logout();

        // 注销后要清除 Cookie
        var path = Objectx.getOrDefault(servletRequest.getHeader(XForwardedHeaders.PATH), "/");
        var cookie = new Cookie(this.properties.getCookie(), "deleteMe");
        cookie.setPath(path);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        servletResponse.addCookie(cookie);

        var accepts = MediaType.parseMediaTypes(Objectx.getOrDefault(servletRequest.getHeader(HttpHeaders.ACCEPT), MediaType.ALL_VALUE));
        if (accepts.stream().anyMatch(MediaType.APPLICATION_JSON::includes)) {
            new JsonRender(servletRequest, servletResponse).set("message", "注销成功").render();
        } else {
            new RedirectRender(servletRequest, servletResponse).redirect(URI.create(Objectx.getOrDefault(((HttpServletRequest) request).getContextPath(), "/"))).render();
        }
    }

    /**
     * 开发者在 Controller 的方法中，可以通过 @RequestAttribute String accountId 来获取帐户唯一标识
     * <p>
     * 无论 JWT 是认证通过还是不通过，都应该返回 true，只有标了注解的方法，才会被切面拦截
     * 如果返回 false 的话，会导致请求被拉截，一些普通的不需要权限的请求，比如获取验证码这些接口也会被拦截
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        if (this.isLoginAttempt(request, response)) {
            try {
                this.executeLogin(request, response);
            } catch (Exception ignored) {
                // 登录失败也没关系，如果方法是受保护的，方法上的注解会保护并抛出 UnauthenticatedException
            }
        }

        if (this.isLogoutAttempt(request, response)) {
            try {
                executeLogout(request, response);
                return false;
            } catch (Exception ignored) {
                // 退出失败也没关系
            }
        }
        return true;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        if (this.isLogoutAttempt(request, response)) {
            return false;
        } else {
            return super.onAccessDenied(request, response);
        }
    }
}
