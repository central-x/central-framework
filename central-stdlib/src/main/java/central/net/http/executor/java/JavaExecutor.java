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

package central.net.http.executor.java;

import central.net.http.HttpException;
import central.net.http.HttpExecutor;
import central.net.http.body.Body;
import central.net.http.ssl.X509TrustManagerImpl;
import central.lang.Stringx;
import central.util.function.ThrowableSupplier;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpHeaders;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Java 实现
 *
 * @author Alan Yeh
 * @since 2022/07/18
 */
@RequiredArgsConstructor
public class JavaExecutor implements HttpExecutor {

    private final HttpClient client;

    @Override
    public String getName() {
        return "java";
    }

    @Override
    public central.net.http.HttpResponse<? extends Body> execute(central.net.http.HttpRequest request) throws Exception {
        try (request) {
            var builder = HttpRequest.newBuilder()
                    .method(request.getMethod().name(), this.parseBody(request.getBody(), request.getHeaders()))
                    .uri(request.getUrl().toURI());

            // 处理请求头
            request.getHeaders().forEach((name, values) -> values.forEach(value -> builder.header(name, value)));

            // 处理 Cookie
            builder.header(HttpHeaders.COOKIE, request.getCookieHeader());

            // 执行请求
            try {
                var response = this.client.send(builder.build(), HttpResponse.BodyHandlers.ofInputStream());

                return new JavaResponse(request, response);
            } catch (SocketTimeoutException cause) {
                throw new HttpException(request, null, Stringx.format("网络超时: {} {}", request.getMethod(), request.getUrl()), cause);
            } catch (IOException cause) {
                throw new HttpException(request, null, Stringx.format("网络异常: {} {}", request.getMethod(), request.getUrl()), cause);
            }
        }
    }


    private HttpRequest.BodyPublisher parseBody(Body body, HttpHeaders headers) {
        if (body == null) {
            // 没有请求体
            return HttpRequest.BodyPublishers.noBody();
        } else {
            headers.putAll(body.getHeaders());
            headers.remove(HttpHeaders.CONTENT_LENGTH);
            return HttpRequest.BodyPublishers.ofInputStream(ThrowableSupplier.of(body::getInputStream).sneakThrows());
        }
    }

    /**
     * 默认的 HttpClient 配置
     */
    @SneakyThrows
    public static JavaExecutor Default() {
        SSLContext context = SSLContext.getInstance("SSL");
        context.init(null, new TrustManager[]{new X509TrustManagerImpl()}, null);

        HttpClient client = HttpClient.newBuilder()
                .sslContext(context)
                .connectTimeout(Duration.ofSeconds(60))
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build();

        return new JavaExecutor(client);
    }
}
