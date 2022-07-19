package central.net.http.proxy;

import central.io.IOStreamx;
import central.lang.reflect.TypeReference;
import central.net.http.*;
import central.net.http.body.extractor.JsonExtractor;
import central.net.http.body.extractor.StringExtractor;
import central.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;

import java.io.File;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

/**
 * Http Proxy
 *
 * @author Alan Yeh
 * @since 2022/07/19
 */
@RequiredArgsConstructor
public class HttpProxy implements InvocationHandler {

    private final Class<?> proxyType;

    private final HttpClient client;

    private final Contract contract;

    protected MethodHandles.Lookup lookup;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.isDefault()) {
            // interface 的 default 方法
            var caller = method.getDeclaringClass();
            if (lookup == null) {
                var constructor = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class, int.class);
                constructor.setAccessible(true);
                lookup = constructor.newInstance(caller, MethodHandles.Lookup.PUBLIC | MethodHandles.Lookup.PRIVATE);
            }
            // 通过 lookup 直接调有和默认实现
            return lookup.unreflectSpecial(method, caller).bindTo(proxy).invokeWithArguments(args);
        } else if (Objects.equals("toString", method.getName())) {
            return Stringx.format("{}@({})", this.proxyType.getSimpleName(), this.client.getBaseUrl());
        } else {
            HttpRequest request = null;
            if (args != null && args.length == 1 && args[0] instanceof HttpRequest req) {
                // 代理方法想直接执行 HttpRequest
                request = req;
            }

            if (request == null) {
                // 使用契约解析请求
                request = contract.parse(proxy, method, args);
            }

            request.getUrl().setBaseUrl(this.client.getBaseUrl());
            request.setAttribute(HttpAttributes.PROXY_METHOD, method);

            // 执行请求
            var response = client.execute(request);

            // 解析结果

            // 如果开发者要求返回 HttpResponse
            // 注意，开发者需要手动关闭 HttpResponse
            if (HttpResponse.class.isAssignableFrom(method.getReturnType())) {
                return response;
            }

            // 开发者希望框架完成反序列化
            // Response 需要是成功状态才能反序列化
            if (!response.isSuccess()){
                try (response){
                    throw new HttpException(response.getRequest(), response);
                }
            }

            // 根据返回值类型，动态判断要返回什么
            if (InputStream.class.isAssignableFrom(method.getReturnType())) {
                return response.getBody().getInputStream();
            }

            try (response) {
                if (void.class == method.getReturnType()) {
                    // 如果方法不需要返回值
                    return null;
                }

                if (String.class.isAssignableFrom(method.getReturnType())) {
                    // 直接返回字符串
                    return response.getBody().extract(new StringExtractor());
                }

                if (File.class.isAssignableFrom(method.getReturnType())) {
                    // 返回文件类型
                    // 解析文件名
                    var filename = response.getHeaders().getContentDisposition().getFilename();

                    var tmp = new File("tmp", Objectx.get(filename, Guidx.nextID() + ".tmp"));

                    IOStreamx.copy(response.getBody().getInputStream(), Files.newOutputStream(tmp.toPath(), StandardOpenOption.WRITE));
                    return tmp;
                }

                // 使用 JSON 解析结果
                if (MediaType.APPLICATION_JSON.isCompatibleWith(response.getHeaders().getContentType())) {
                    return response.getBody().extract(new JsonExtractor<>(TypeReference.of(method.getGenericReturnType())));
                }

                throw new HttpException(request, response, "无法解析响应体");
            }
        }
    }
}
