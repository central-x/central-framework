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

package central.net.http.proxy.contract.spring.resolver;

import central.net.http.HttpRequest;
import central.net.http.body.HttpConverters;
import central.net.http.proxy.contract.spring.SpringResolver;
import central.lang.Assertx;
import central.util.Objectx;
import central.lang.Stringx;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ValueConstants;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.Objects;

/**
 * 处理 Cookie
 *
 * @author Alan Yeh
 * @see CookieValue
 * @since 2022/07/18
 */
public class CookieValueResolver implements SpringResolver {
    @Override
    public boolean support(Parameter parameter) {
        return parameter.isAnnotationPresent(CookieValue.class);
    }

    @Override
    public boolean resolve(HttpRequest request, Method method, Parameter parameter, Object arg) {
        var annotation = parameter.getAnnotation(CookieValue.class);

        String name = parameter.getName();
        Object value = arg;

        if (value instanceof Map<?, ?> cookies) {
            // 使用 Map 传递 Cookie
            for (var cookie : cookies.entrySet()) {
                Object val = cookie.getValue();
                Assertx.mustTrue(HttpConverters.Default().support(val), "Unsupported value type '{}' in parameter '{}'", val.getClass().getName(), parameter.getName());
                request.setCookie(cookie.getKey().toString(), HttpConverters.Default().convert(val));
            }
            return true;
        }

        if (Stringx.isNotBlank(annotation.name()) || Stringx.isNotBlank(annotation.value())) {
            // 修改为指定的 Cookie 名
            name = Objectx.get(annotation.name(), annotation.value());
        }

        if (arg == null && Stringx.isNotBlank(annotation.defaultValue()) && Objects.equals(ValueConstants.DEFAULT_NONE, annotation.defaultValue())) {
            // 设置默认值
            value = annotation.defaultValue();
        }

        if (value != null) {
            Assertx.mustTrue(HttpConverters.Default().support(value), "Unsupported value type '{}' in parameter '{}'", value.getClass().getName(), parameter.getName());
            value = HttpConverters.Default().convert(value);
        }

        if (annotation.required() && (value == null || Stringx.isNullOrEmpty(value.toString()))) {
            throw new IllegalArgumentException(Stringx.format("Required parameter '{}' is missing", parameter.getName()));
        }

        request.setCookie(name, Objectx.toString(value));
        return true;
    }
}
