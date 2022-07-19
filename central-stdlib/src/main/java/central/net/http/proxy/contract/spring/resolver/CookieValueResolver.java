package central.net.http.proxy.contract.spring.resolver;

import central.net.http.HttpRequest;
import central.net.http.body.HttpConverters;
import central.net.http.proxy.contract.spring.SpringResolver;
import central.util.Assertx;
import central.util.Objectx;
import central.util.Stringx;
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
