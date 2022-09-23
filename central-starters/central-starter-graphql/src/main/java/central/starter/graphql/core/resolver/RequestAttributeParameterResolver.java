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
import graphql.schema.DataFetchingEnvironment;
import jakarta.servlet.http.HttpServletRequest;
import org.dataloader.BatchLoaderEnvironment;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.web.bind.annotation.RequestAttribute;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;

/**
 * 解析请求属性
 *
 * @author Alan Yeh
 * @see RequestAttribute
 * @since 2022/09/09
 */
public class RequestAttributeParameterResolver extends AnnotatedParameterResolver {
    public RequestAttributeParameterResolver() {
        super(RequestAttribute.class);
    }

    @Override
    public Object resolve(Method method, Parameter parameter, DataFetchingEnvironment environment) {
        return this.resolves(method, parameter, environment.getLocalContext());
    }

    @Override
    public Object resolve(Method method, Parameter parameter, List<String> keys, BatchLoaderEnvironment environment) {
        return this.resolves(method, parameter, environment.getContext());
    }

    private Object resolves(Method method, Parameter parameter, Context context) {
        RequestAttribute attr = parameter.getAnnotation(RequestAttribute.class);
        String name = parameter.getName();
        if (attr.value().length() > 0 || attr.name().length() > 0) {
            name = Objectx.get(attr.value(), attr.name());
        }

        if (context == null) {
            if (attr.required()) {
                throw new GraphQLException(Stringx.format("执行方法[{}.{}]错误: 无法找到参数[{}]指定的请求属性[{}]", method.getDeclaringClass().getSimpleName(), method.getName(), parameter.getName(), name));
            }

            return null;
        }

        HttpServletRequest request = context.get(HttpServletRequest.class);
        Object value = request.getAttribute(name);

        // 判断
        if (value == null && attr.required()) {
            throw new GraphQLException(Stringx.format("执行方法[{}.{}]错误: 无法找到参数[{}]指定的请求属性[{}]", method.getDeclaringClass().getSimpleName(), method.getName(), parameter.getName(), name));
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
