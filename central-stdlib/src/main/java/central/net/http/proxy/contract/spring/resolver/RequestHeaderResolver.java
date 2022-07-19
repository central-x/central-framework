package central.net.http.proxy.contract.spring.resolver;

import central.net.http.HttpRequest;
import central.net.http.body.HttpConverters;
import central.net.http.proxy.contract.spring.SpringResolver;
import central.util.Assertx;
import central.util.Objectx;
import central.util.Stringx;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ValueConstants;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 处理请求头
 *
 * @author Alan Yeh
 * @see RequestHeader
 * @since 2022/07/18
 */
public class RequestHeaderResolver implements SpringResolver {
    @Override
    public boolean support(Parameter parameter) {
        return parameter.isAnnotationPresent(RequestHeader.class);
    }

    @Override
    public boolean resolve(HttpRequest request, Method method, Parameter parameter, Object arg) {
        var annotation = parameter.getAnnotation(RequestHeader.class);

        String name = parameter.getName();
        Object value = arg;

        if (value instanceof Map<?, ?> headers) {
            // 使用 Map 传递 Header
            for (var header : headers.entrySet()) {
                Object headerValue = header.getValue();
                if (headerValue instanceof Collection<?> collection) {
                    // 兼容 MultiValueMap
                    for (Object it : collection) {
                        Assertx.mustTrue(HttpConverters.Default().support(it), Stringx.format("Unsupported value type '{}", it.getClass().getName()));

                        request.addHeader(header.getKey().toString(), HttpConverters.Default().convert(it));
                    }
                } else {
                    Assertx.mustTrue(HttpConverters.Default().support(headerValue), Stringx.format("Unsupported value type '{}", headerValue.getClass().getName()));
                    request.addHeader(header.getKey().toString(), HttpConverters.Default().convert(headerValue));
                }
            }
            // 已处理
            return true;
        }

        // 自定义 Header 名
        if (Stringx.isNotBlank(annotation.name()) || Stringx.isNotBlank(annotation.value())) {
            name = Objectx.get(annotation.name(), annotation.value());
        }

        if (value instanceof List<?> headers) {
            // 使用 List 传递 Header
            for (var header : headers) {
                Assertx.mustTrue(HttpConverters.Default().support(header), Stringx.format("Unsupported value type '{}", header.getClass().getName()));
                request.addHeader(name, HttpConverters.Default().convert(header));
            }
            return true;
        }

        // 使用普通对象传递 Header
        // 处理默认值
        if (arg == null && Stringx.isNotBlank(annotation.defaultValue()) && !Objects.equals(ValueConstants.DEFAULT_NONE, annotation.defaultValue())) {
            value = annotation.defaultValue();
        }

        // 必填校验
        Assertx.mustTrue(!annotation.required() || (value != null && Stringx.isNotBlank(value.toString())), Stringx.format("Required parameter '{}' is missing", parameter.getName()));

        Assertx.mustTrue(HttpConverters.Default().support(value), "Unsupported value type '{}", parameter.getType().getName());

        request.addHeader(name, HttpConverters.Default().convert(value));
        return true;
    }
}
