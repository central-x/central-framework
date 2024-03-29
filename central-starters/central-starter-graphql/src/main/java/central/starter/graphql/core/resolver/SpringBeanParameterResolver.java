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

package central.starter.graphql.core.resolver;

import central.lang.reflect.invoke.ParameterResolver;
import central.lang.Stringx;
import central.util.Context;
import graphql.GraphQLException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Objects;

/**
 * 处理 Spring Bean 参数注入
 *
 * @author Alan Yeh
 * @see Autowired
 * @see Qualifier
 * @since 2022/09/09
 */
public class SpringBeanParameterResolver implements ParameterResolver {
    @Override
    public boolean support(@NotNull Class<?> clazz, @NotNull Method method, @NotNull Parameter parameter) {
        if (Objects.equals(ApplicationContext.class, parameter.getType()) ||
                Objects.equals(Environment.class, parameter.getType())) {
            return true;
        }
        return parameter.isAnnotationPresent(Autowired.class) || parameter.isAnnotationPresent(Qualifier.class);
    }

    @Nullable
    @Override
    public Object resolve(@NotNull Class<?> clazz, @NotNull Method method, @NotNull Parameter parameter, @NotNull Context context) {
        ApplicationContext applicationContext = context.get(ApplicationContext.class);

        // 如果该注解不存在，或该注解的 required = true 时，bean 必须有值
        Autowired autowired = parameter.getAnnotation(Autowired.class);
        // 如果出现此注解，则根据 beanName 取，否则根据 beanType 取
        Qualifier qualifier = parameter.getAnnotation(Qualifier.class);

        String name = null;
        if (qualifier != null) {
            name = qualifier.value();
        }

        Object bean = null;

        if (applicationContext != null) {
            try {
                if (Objects.equals(ApplicationContext.class, parameter.getType())) {
                    bean = applicationContext;
                } else if (Objects.equals(Environment.class, parameter.getType())) {
                    bean = applicationContext.getEnvironment();
                } else if (Stringx.isNotBlank(name)) {
                    bean = applicationContext.getBean(name);
                } else {
                    bean = applicationContext.getBean(parameter.getType());
                }
            } catch (Exception ignored) {
            }
        }

        if (bean == null && (autowired == null || autowired.required())) {
            if (Stringx.isNotBlank(name)) {
                throw new GraphQLException(Stringx.format("执行方法[{}.{}]错误: 无法找到参数指定的 Bean(name = {})", method.getDeclaringClass().getSimpleName(), method.getName(), qualifier.value()));
            } else {
                throw new GraphQLException(Stringx.format("执行方法[{}.{}]错误: 无法找到参数指定的 Bean(class = {})", method.getDeclaringClass().getSimpleName(), method.getName(), parameter.getType().getCanonicalName()));
            }
        }

        return bean;
    }
}
