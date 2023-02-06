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
import central.util.Context;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.time.ZoneId;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Servlet 相关参数
 *
 * @author Alan Yeh
 * @since 2022/09/09
 */
public class ServletParameterResolver implements ParameterResolver {
    @Override
    public boolean support(@NotNull Class<?> clazz, @NotNull Method method, @NotNull Parameter parameter) {
        return ServletRequest.class.equals(parameter.getType()) ||
                HttpServletRequest.class.equals(parameter.getType()) ||
                ServletResponse.class.equals(parameter.getType()) ||
                HttpServletResponse.class.equals(parameter.getType()) ||
                HttpMethod.class.equals(parameter.getType()) ||
                Locale.class.equals(parameter.getType()) ||
                TimeZone.class.equals(parameter.getType()) ||
                ZoneId.class.equals(parameter.getType());
    }

    @Nullable
    @Override
    public Object resolve(@NotNull Class<?> clazz, @NotNull Method method, @NotNull Parameter parameter, @NotNull Context context) {
        if (ServletResponse.class.equals(parameter.getType())) {
            return context.get(ServletResponse.class);
        }

        if (HttpServletResponse.class.equals(parameter.getType())) {
            return context.get(HttpServletResponse.class);
        }

        if (ServletRequest.class.equals(parameter.getType())) {
            return context.get(ServletRequest.class);
        }

        if (HttpServletRequest.class.equals(parameter.getType())) {
            return context.get(HttpServletRequest.class);
        }

        HttpServletRequest request = context.get(HttpServletRequest.class);
        // 获取请求相关信息
        if (request != null) {
            if (HttpMethod.class.equals(parameter.getType())) {
                return HttpMethod.valueOf(request.getMethod());
            }

            if (Locale.class.equals(parameter.getType())) {
                return RequestContextUtils.getLocale(request);
            }

            if (TimeZone.class.equals(parameter.getType())) {
                return RequestContextUtils.getTimeZone(request);
            }

            if (ZoneId.class.equals(parameter.getType())) {
                TimeZone timeZone = RequestContextUtils.getTimeZone(request);
                return (timeZone != null ? timeZone.toZoneId() : ZoneId.systemDefault());
            }
        }

        return null;
    }
}
