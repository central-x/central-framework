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
import central.lang.Arrayx;
import central.lang.Assertx;
import central.util.Objectx;
import central.lang.Stringx;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ValueConstants;

import java.beans.Introspector;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 处理 Query 参数
 *
 * @author Alan Yeh
 * @see RequestParam
 * @since 2022/07/18
 */
public class RequestParamResolver implements SpringResolver {
    @Override
    public boolean support(Parameter parameter) {
        return parameter.isAnnotationPresent(RequestParam.class);
    }

    @Override
    @SneakyThrows
    public boolean resolve(HttpRequest request, Method method, Parameter parameter, Object arg) {
        var annotation = parameter.getAnnotation(RequestParam.class);

        String name = parameter.getName();
        Object value = arg;

        if (value instanceof Map<?, ?> query) {
            // 使用 Map 传递 Query
            for (var it : query.entrySet()) {
                if (it.getValue() instanceof List<?> list) {
                    // 兼容 MultiValueMap
                    for (var val : list) {
                        Assertx.mustTrue(HttpConverters.Default().support(val), "Unsupported value type '{}' in parameter '{}'", val.getClass().getName(), parameter.getName());
                        request.getUrl().addQuery(it.getKey().toString(), HttpConverters.Default().convert(val));
                    }
                } else {
                    Assertx.mustTrue(HttpConverters.Default().support(it.getValue()), "Unsupported value type '{}' in parameter '{}'", it.getValue().getClass().getName(), parameter.getName());
                    request.getUrl().addQuery(it.getKey().toString(), HttpConverters.Default().convert(it.getValue()));
                }
            }
            return true;
        }

        // 自定义参数名
        if (Stringx.isNotBlank(annotation.name()) || Stringx.isNotBlank(annotation.value())) {
            name = Objectx.get(annotation.name(), annotation.value());
        }

        if (value instanceof List<?> list) {
            // 使用 List 传递 Query
            for (var val : list) {
                Assertx.mustTrue(HttpConverters.Default().support(val), "Unsupported value type '{}' in parameter '{}'", val.getClass().getName(), parameter.getName());
                request.getUrl().addQuery(name, HttpConverters.Default().convert(val));
            }
            return true;
        }

        // 处理默认值
        if (arg == null && Stringx.isNotBlank(annotation.defaultValue()) && !Objects.equals(ValueConstants.DEFAULT_NONE, annotation.defaultValue())) {
            value = annotation.defaultValue();
        }

        // 处理必填
        Assertx.mustTrue(!annotation.required() || (value != null && Stringx.isNotBlank(value.toString())), "Required parameter '{}' is missing", parameter.getName());

        if (value == null) {
            request.getUrl().addQuery(name, "");
        } else if (HttpConverters.Default().support(value)) {
            request.getUrl().addQuery(name, HttpConverters.Default().convert(value));
        } else {
            // 如果不支持的数据类型，那么这个参数应该是个对象
            // 取出所有这个对象的字段信息作为参数
            var bean = Introspector.getBeanInfo(value.getClass());
            var properties = bean.getPropertyDescriptors();

            Assertx.mustTrue(Arrayx.isNotEmpty(properties), "Fail to get properties from value type '{}'", value.getClass().getName());

            for (var property : properties) {
                if (property.getWriteMethod() == null || property.getReadMethod() == null) {
                    continue;
                }

                var propertyValue = property.getReadMethod().invoke(value);

                if (propertyValue == null) {
                    continue;
                }

                if (propertyValue instanceof List<?> list) {
                    // 使用 List 传递 Query
                    for (var val : list) {
                        Assertx.mustTrue(HttpConverters.Default().support(val), "Unsupported value type '{}' in parameter '{}.{}'", val.getClass().getName(), parameter.getName(), property.getName());
                        request.getUrl().addQuery(property.getName(), HttpConverters.Default().convert(val));
                    }
                } else if (propertyValue.getClass().isArray()) {
                    // 使用 Array 传递 Query
                    var array = (Object[]) propertyValue;
                    for (var val : array) {
                        Assertx.mustTrue(HttpConverters.Default().support(val), "Unsupported value type '{}' in parameter '{}.{}'", val.getClass().getName(), parameter.getName(), property.getName());
                        request.getUrl().addQuery(property.getName(), HttpConverters.Default().convert(val));
                    }
                } else {
                    // TODO 支持多层级对象传递 Query
                    Assertx.mustTrue(HttpConverters.Default().support(propertyValue), "Unsupported value type '{}' in parameter '{}'", propertyValue.getClass().getName(), parameter.getName());
                    request.getUrl().addQuery(property.getName(), HttpConverters.Default().convert(propertyValue));
                }
            }
        }
        return true;
    }
}
