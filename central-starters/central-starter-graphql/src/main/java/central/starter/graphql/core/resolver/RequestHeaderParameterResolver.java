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

import central.util.Context;
import central.util.Objectx;
import central.lang.Stringx;
import graphql.GraphQLException;
import jakarta.servlet.http.HttpServletRequest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ValueConstants;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * 处理请求头参数注入
 *
 * @author Alan Yeh
 * @see RequestHeader
 * @since 2022/09/09
 */
public class RequestHeaderParameterResolver extends SpringAnnotatedParameterResolver {
    public RequestHeaderParameterResolver() {
        super(RequestHeader.class);
    }

    @Nullable
    @Override
    public Object resolve(@NotNull Class<?> clazz, @NotNull Method method, @NotNull Parameter parameter, @NotNull Context context) {
        RequestHeader header = parameter.getAnnotation(RequestHeader.class);
        String name = parameter.getName();
        if (header.value().length() > 0 || header.name().length() > 0) {
            name = Objectx.getOrDefault(header.value(), header.name());
        }

        HttpServletRequest request = context.get(HttpServletRequest.class);
        if (request == null) {
            if (header.required()) {
                throw new GraphQLException(Stringx.format("执行方法[{}.{}]错误: 无法找到参数[{}]指定的请求头(name = {})", parameter.getName(), name));
            }
            return null;
        }

        Object value = request.getHeader(name);

        // 设置默认值
        if (value == null && !ValueConstants.DEFAULT_NONE.equals(header.defaultValue())) {
            value = header.defaultValue();
        }

        // 判断
        if (value == null && header.required()) {
            throw new GraphQLException(Stringx.format("执行方法[{}.{}]错误: 无法找到参数[{}]指定的请求头(name = {})", method.getDeclaringClass().getSimpleName(), method.getName(), parameter.getName(), name));
        }

        // 判断类型是否可转换
        if (value != null && !parameter.getType().isAssignableFrom(value.getClass())) {
            // 如果值的类型与参数类型不匹配，则需要对其进行类型转换
            FormattingConversionService converter = this.getConverter(context);

            if (converter == null || !converter.canConvert(value.getClass(), parameter.getType())) {
                throw new GraphQLException(Stringx.format("执行方法[{}.{}]错误: 请求属性[{}]类型错误，无法将{}转换成{}类型", method.getDeclaringClass().getSimpleName(), method.getName(), name, value.getClass().getCanonicalName(), parameter.getType().getCanonicalName()));
            }

            value = converter.convert(value, parameter.getType());
        }

        return value;
    }
}
