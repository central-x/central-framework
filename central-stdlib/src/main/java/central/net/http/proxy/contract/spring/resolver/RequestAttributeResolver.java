package central.net.http.proxy.contract.spring.resolver;

import central.lang.Attribute;
import central.net.http.HttpRequest;
import central.net.http.proxy.contract.spring.SpringResolver;
import central.lang.Assertx;
import central.util.Objectx;
import central.util.Stringx;
import org.springframework.web.bind.annotation.RequestAttribute;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * 处理请求属性
 *
 * @author Alan Yeh
 * @see RequestAttribute
 * @since 2022/07/18
 */
public class RequestAttributeResolver implements SpringResolver {
    @Override
    public boolean support(Parameter parameter) {
        return parameter.isAnnotationPresent(RequestAttribute.class);
    }

    @Override
    public boolean resolve(HttpRequest request, Method method, Parameter parameter, Object arg) {
        var annotation = parameter.getAnnotation(RequestAttribute.class);

        String name = parameter.getName();
        Object value = arg;

        if (Stringx.isNotBlank(annotation.name()) || Stringx.isNotBlank(annotation.value())) {
            // 修改自定义属性名
            name = Objectx.get(annotation.name(), annotation.value());
        }

        // 必填校验
        Assertx.mustTrue(!annotation.required() || value != null, "Required parameter '{}' is missing", parameter.getName());

        request.setAttribute(new Attribute<>(name), value);
        return true;
    }
}
