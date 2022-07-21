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

package central.net.http.executor.okhttp;

import central.net.http.HttpException;
import central.net.http.HttpExecutor;
import central.net.http.HttpRequest;
import central.net.http.HttpResponse;
import central.net.http.body.Body;
import central.net.http.executor.okhttp.body.EmptyBody;
import central.net.http.executor.okhttp.body.WrapperBody;
import central.net.http.ssl.HostnameVerifierImpl;
import central.net.http.ssl.X509TrustManagerImpl;
import central.util.Stringx;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.internal.http.HttpMethod;
import org.springframework.http.HttpHeaders;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

/**
 * OkHttp 实现
 *
 * @author Alan Yeh
 * @since 2022/07/15
 */
@RequiredArgsConstructor
public class OkHttpExecutor implements HttpExecutor {

    private final OkHttpClient client;

    @Override
    public String getName() {
        return "OkHttp";
    }

    @Override
    public HttpResponse<? extends Body> execute(HttpRequest request) throws Exception {
        try (request) {
            // 处理请求基本信息
            var builder = new Request.Builder()
                    .method(request.getMethod().name(), this.parseBody(request.getMethod().name(), request.getBody(), request.getHeaders()))
                    .url(request.getUrl().getValue());

            // 处理请求头
            request.getHeaders().forEach((key, values) -> values.forEach(it -> builder.addHeader(key, it)));

            // 处理 Cookie
            builder.header(HttpHeaders.COOKIE, request.getCookieHeader());

            // 执行请求
            try {
                var response = this.client.newCall(builder.build()).execute();

                return new OkHttpResponse(request, response);
            } catch (SocketTimeoutException cause) {
                throw new HttpException(request, null, Stringx.format("网络超时: {} {}", request.getMethod(), request.getUrl()), cause);
            } catch (IOException cause) {
                throw new HttpException(request, null, Stringx.format("网络异常: {} {}", request.getMethod(), request.getUrl()), cause);
            }
        }
    }

    private RequestBody parseBody(String method, Body body, HttpHeaders headers) {
        if (!HttpMethod.permitsRequestBody(method)) {
            // 不允许有请求体的方法
            return null;
        } else {
            if (body == null) {
                return new EmptyBody();
            } else {
                headers.putAll(body.getHeaders());
                return new WrapperBody(body);
            }
        }
    }

    /**
     * 默认的 OkHttpClient 配置
     */
    @SneakyThrows
    public static HttpExecutor Default() {
        SSLContext context = SSLContext.getInstance("SSL");
        context.init(null, new TrustManager[]{new X509TrustManagerImpl()}, null);

        OkHttpClient okhttp = new OkHttpClient.Builder()
                .sslSocketFactory(context.getSocketFactory(), new X509TrustManagerImpl())
                .hostnameVerifier(new HostnameVerifierImpl())
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .followRedirects(true)
                .followSslRedirects(true).build();

        return new OkHttpExecutor(okhttp);
    }
}
