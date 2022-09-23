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

import central.starter.graphql.GraphQLParameterResolver;
import central.util.Context;
import graphql.schema.DataFetchingEnvironment;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.dataloader.BatchLoaderEnvironment;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Servlet 相关参数
 *
 * @author Alan Yeh
 * @since 2022/09/09
 */
public class ServletParameterResolver implements GraphQLParameterResolver {
    @Override
    public boolean support(Parameter parameter) {
        return ServletRequest.class == parameter.getType() ||
                HttpServletRequest.class == parameter.getType() ||
                ServletResponse.class == parameter.getType() ||
                HttpServletResponse.class == parameter.getType() ||
                HttpMethod.class == parameter.getType() ||
                Locale.class == parameter.getType() ||
                TimeZone.class == parameter.getType() ||
                ZoneId.class == parameter.getType();
    }

    @Override
    public Object resolve(Method method, Parameter parameter, DataFetchingEnvironment environment) {
        return this.resolves(parameter, environment.getLocalContext());
    }

    @Override
    public Object resolve(Method method, Parameter parameter, List<String> keys, BatchLoaderEnvironment environment) {
        return this.resolves(parameter, environment.getContext());
    }

    private Object resolves(Parameter parameter, Context context) {
        if (context == null) {
            return null;
        }

        if (ServletResponse.class == parameter.getType()) {
            return context.get(ServletResponse.class);
        }

        if (HttpServletResponse.class == parameter.getType()) {
            return context.get(HttpServletResponse.class);
        }

        if (ServletRequest.class == parameter.getType()) {
            return context.get(ServletRequest.class);
        }

        if (HttpServletRequest.class == parameter.getType()) {
            return context.get(HttpServletRequest.class);
        }

        HttpServletRequest request = context.get(HttpServletRequest.class);
        // 获取请求相关信息
        if (request != null) {
            if (HttpMethod.class == parameter.getType()) {
                return HttpMethod.resolve(request.getMethod());
            }

            if (Locale.class == parameter.getType()) {
                return RequestContextUtils.getLocale(request);
            }

            if (TimeZone.class == parameter.getType()) {
                return RequestContextUtils.getTimeZone(request);
            }

            if (ZoneId.class == parameter.getType()) {
                TimeZone timeZone = RequestContextUtils.getTimeZone(request);
                return (timeZone != null ? timeZone.toZoneId() : ZoneId.systemDefault());
            }
        }

        return null;
    }
}
