package central.net.http.proxy.contract.spring.resolver;

import central.net.http.HttpRequest;
import central.net.http.body.HttpConverters;
import central.net.http.proxy.contract.spring.SpringResolver;
import central.lang.Assertx;
import central.util.Objectx;
import central.util.Stringx;
import org.springframework.web.bind.annotation.PathVariable;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * Path 参数
 *
 * @author Alan Yeh
 * @see PathVariable
 * @since 2022/07/18
 */
public class PathVariableResolver implements SpringResolver {
    @Override
    public boolean support(Parameter parameter) {
        return parameter.isAnnotationPresent(PathVariable.class);
    }

    @Override
    public boolean resolve(HttpRequest request, Method method, Parameter parameter, Object arg) {
        var annotation = parameter.getAnnotation(PathVariable.class);

        String name = parameter.getName();
        Object value = arg;

        if (Stringx.isNotBlank(annotation.name()) || Stringx.isNotBlank(annotation.value())) {
            // 修改为自定义参数名
            name = Objectx.get(annotation.name(), annotation.value());
        }

        // 判断是否必填
        Assertx.mustTrue(!annotation.required() || value != null, "Required parameter '{}' is missing", parameter.getName());

        if (value instanceof String v) {
            request.getUrl().setVariable(name, v);
        } else {
            Assertx.mustTrue(HttpConverters.Default().support(value), "Unsupported value type '{}' for parameter '{}'", value.getClass().getName(), parameter.getName());
            request.getUrl().setVariable(name, HttpConverters.Default().convert(value));
        }

        return true;
    }
}
