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

package central.net.http.proxy;

import central.lang.Arrayx;
import central.lang.Stringx;
import central.lang.reflect.TypeRef;
import central.net.http.*;
import central.net.http.body.extractor.FileExtractor;
import central.net.http.body.extractor.JsonExtractor;
import central.net.http.body.extractor.StringExtractor;
import central.util.Convertx;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

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

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.isDefault()) {
            // interface 的 default 方法
            return InvocationHandler.invokeDefault(proxy, method, args);
        } else if (Objects.equals("toString", method.getName())) {
            // 输出 client 的相关信息
            return Stringx.format("{}@({})", this.proxyType.getSimpleName(), this.client.getBaseUrl());
        } else {
            HttpRequest request = null;
            if (Arrayx.isNotEmpty(args) && args.length == 1 && args[0] instanceof HttpRequest req) {
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
            if (!response.isSuccess()) {
                try (response) {
                    throw HttpException.of(request, response);
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

                // 没有响应体
                if (response.getHeaders().getContentLength() == 0) {
                    return null;
                }

                // 检查一些基础数据结构
                if (String.class.isAssignableFrom(method.getReturnType())) {
                    // 直接返回字符串
                    return response.getBody().extract(StringExtractor.of());
                }
                if (Boolean.class == method.getReturnType() || boolean.class == method.getReturnType()) {
                    return Convertx.Default().convert(response.getBody().extract(StringExtractor.of()), Boolean.class);
                }
                if (Integer.class == method.getReturnType() || int.class == method.getReturnType()) {
                    return Convertx.Default().convert(response.getBody().extract(StringExtractor.of()), Integer.class);
                }
                if (Long.class == method.getReturnType() || long.class == method.getReturnType()) {
                    return Convertx.Default().convert(response.getBody().extract(StringExtractor.of()), Long.class);
                }
                if (Short.class == method.getReturnType() || short.class == method.getReturnType()) {
                    return Convertx.Default().convert(response.getBody().extract(StringExtractor.of()), Long.class);
                }
                if (Double.class == method.getReturnType() || double.class == method.getReturnType()) {
                    return Convertx.Default().convert(response.getBody().extract(StringExtractor.of()), Double.class);
                }
                if (Float.class == method.getReturnType() || float.class == method.getReturnType()) {
                    return Convertx.Default().convert(response.getBody().extract(StringExtractor.of()), Float.class);
                }

                if (File.class.isAssignableFrom(method.getReturnType())) {
                    // 返回文件类型
                    // 解析文件名
                    return response.getBody().extract(FileExtractor.of(this.client.getTmp()));
                }

                // 使用 JSON 解析结果
                if (MediaType.APPLICATION_JSON.isCompatibleWith(response.getHeaders().getContentType())) {
                    return response.getBody().extract(JsonExtractor.of(TypeRef.of(method.getGenericReturnType())));
                }

                throw new IllegalStateException(Stringx.format("{} {}#{}({}): 无法处理返回值类型[{}]",
                        method.getReturnType().getSimpleName(), method.getDeclaringClass().getSimpleName(), method.getName(), Arrays.stream(method.getParameterTypes()).map(Class::getSimpleName).collect(Collectors.joining(", ")),
                        method.getName()));
            }
        }
    }
}
