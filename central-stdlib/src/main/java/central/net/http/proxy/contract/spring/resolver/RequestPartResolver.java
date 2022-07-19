package central.net.http.proxy.contract.spring.resolver;

import central.net.http.HttpRequest;
import central.net.http.body.HttpConverters;
import central.net.http.body.request.JsonBody;
import central.net.http.body.request.MultipartFormBody;
import central.net.http.body.request.MultipartFormPart;
import central.net.http.body.request.UrlEncodedBody;
import central.net.http.proxy.contract.spring.SpringResolver;
import central.util.Assertx;
import central.util.Objectx;
import central.util.Stringx;
import lombok.SneakyThrows;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestPart;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * 处理处求体信息
 *
 * @author Alan Yeh
 * @see RequestPart
 * @since 2022/07/18
 */
public class RequestPartResolver implements SpringResolver {
    @Override
    public boolean support(Parameter parameter) {
        return parameter.isAnnotationPresent(RequestPart.class);
    }

    @Override
    @SneakyThrows
    public boolean resolve(HttpRequest request, Method method, Parameter parameter, Object arg) {
        var annotation = parameter.getAnnotation(RequestPart.class);

        String name = parameter.getName();
        Object value = arg;

        // 自定义参数名
        if (Stringx.isNotBlank(annotation.name()) || Stringx.isNotBlank(annotation.value())) {
            name = Objectx.get(annotation.name(), annotation.value());
        }

        // 处理必填
        Assertx.mustTrue(!annotation.required() || value != null, "Required parameter '{}' is missing", parameter.getName());

        // 如查请求没有指定 ContentType，那么就默认使用 application/x-www-form-urlencoded 来传递参数
        var contentType = request.getHeaders().getContentType();
        if (contentType == null) {
            contentType = MediaType.APPLICATION_FORM_URLENCODED;
            request.getHeaders().setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        }

        // 处理 application/x-www-form-urlencoded 类型参数
        if (MediaType.APPLICATION_FORM_URLENCODED.isCompatibleWith(contentType)) {
            if (request.getBody() == null) {
                request.setBody(new UrlEncodedBody());
            }

            Assertx.mustTrue(request.getBody() instanceof UrlEncodedBody, "Method '{}#{}' has duplicated body parameters", method.getDeclaringClass().getName(), method.getName());

            var body = (UrlEncodedBody) request.getBody();

            Assertx.mustTrue(HttpConverters.Default().support(value), "Unsupported value type '{}' in parameter '{}'", value.getClass().getName(), parameter.getName());
            body.set(name, HttpConverters.Default().convert(value));
            return true;
        }

        // 处理 application/json 类型的参数
        if (MediaType.APPLICATION_JSON.isCompatibleWith(contentType)) {
            if (request.getBody() == null) {
                request.setBody(new JsonBody());
            }
            Assertx.mustTrue(request.getBody() instanceof JsonBody, "Method '{}#{}' has duplicated body parameters", method.getDeclaringClass().getName(), method.getName());

            var body = (JsonBody) request.getBody();
            body.set(name, value);
            return true;
        }

        // 处理 multipart/form-data 类型的数据
        if (MediaType.MULTIPART_FORM_DATA.isCompatibleWith(contentType)) {
            if (request.getBody() == null) {
                request.setBody(new MultipartFormBody());
            }

            Assertx.mustTrue(request.getBody() instanceof MultipartFormBody, "Method '{}#{}' has duplicated body parameters", method.getDeclaringClass().getName(), method.getName());

            var body = (MultipartFormBody) request.getBody();

            if (value instanceof File file) {
                body.add(MultipartFormPart.create(name, file.getName(), file));
            } else if (value instanceof InputStream stream) {
                body.add(MultipartFormPart.create(name, name, stream));
            } else {
                Assertx.mustTrue(HttpConverters.Default().support(value), "Unsupported value type '{}", value.getClass().getName());
                body.add(MultipartFormPart.create(name, HttpConverters.Default().convert(value)));
            }
            return true;
        }

        // RequestPart 暂不支持其它类型的 ContentType
        throw new IllegalStateException(Stringx.format("Unsupported Content-Type '{}' for @RequestPart", contentType));
    }
}
