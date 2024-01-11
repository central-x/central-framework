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

package central.net.http.executor.apache;

import central.net.http.HttpExecutor;
import central.net.http.HttpRequest;
import central.net.http.HttpResponse;
import central.net.http.ssl.X509TrustManagerImpl;
import central.util.Mapx;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.InputStreamEntity;
import org.apache.hc.core5.http.message.BasicClassicHttpRequest;
import org.apache.hc.core5.util.Timeout;
import org.springframework.http.HttpHeaders;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.time.Duration;

/**
 * Apache HttpClient
 *
 * @author Alan Yeh
 * @since 2024/01/11
 */
@RequiredArgsConstructor
public class ApacheHttpClientExecutor implements HttpExecutor {

    private final HttpClient client;

    @Override
    public String getName() {
        return "Apache HttpClient";
    }

    @Override
    public HttpResponse execute(HttpRequest request) throws Exception {
        try (request) {
            var classicRequest = new BasicClassicHttpRequest(request.getMethod().name(), request.getUrl().toURI());

            // 处理请求头
            request.getHeaders().forEach((name, values) -> values.forEach(value -> classicRequest.addHeader(name, value)));

            // 处理 Cookie
            if (Mapx.isNotEmpty(request.getCookies())) {
                classicRequest.setHeader(HttpHeaders.COOKIE, request.getCookieHeader());
            }

            // 请求 Body
            if (request.getBody() != null) {
                request.getBody().getHeaders().forEach((name, values) -> values.forEach(value -> classicRequest.setHeader(name, value)));
                classicRequest.removeHeaders(HttpHeaders.CONTENT_LENGTH);
                classicRequest.setEntity(new InputStreamEntity(request.getBody().getInputStream(), request.getBody().getContentLength(), ContentType.parse(request.getBody().getContentType().toString())));
            }

            return this.client.execute(classicRequest, response -> new ApacheHttpResponse(request, response));
        }
    }

    @SneakyThrows
    public static ApacheHttpClientExecutor Default() {
        var sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, new TrustManager[]{new X509TrustManagerImpl()}, null);
        var sslFactory = SSLConnectionSocketFactoryBuilder.create().setSslContext(sslContext).build();
        var connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                .setSSLSocketFactory(sslFactory)
                .build();

        var config = RequestConfig.custom()
                .setConnectionRequestTimeout(Timeout.ofSeconds(60))
                .setRedirectsEnabled(true)
                .build();
        HttpClient client = HttpClients.custom()
                .setDefaultRequestConfig(config)
                .setConnectionManager(connectionManager)
                .build();
        return new ApacheHttpClientExecutor(client);
    }


    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Duration connectTimeout = Duration.ofSeconds(60);

        /**
         * 连接超时时间（毫秒）
         */
        public Builder connectTimeout(long connectTimeout) {
            this.connectTimeout = Duration.ofMillis(connectTimeout);
            return this;
        }

        private boolean followRedirects = true;

        public Builder followRedirects(boolean followRedirects) {
            this.followRedirects = followRedirects;
            return this;
        }

        @SneakyThrows
        public ApacheHttpClientExecutor build() {
            var sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[]{new X509TrustManagerImpl()}, null);
            var sslFactory = SSLConnectionSocketFactoryBuilder.create().setSslContext(sslContext).build();
            var connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                    .setSSLSocketFactory(sslFactory)
                    .build();

            var config = RequestConfig.custom()
                    .setConnectionRequestTimeout(Timeout.of(this.connectTimeout))
                    .setRedirectsEnabled(this.followRedirects)
                    .build();
            HttpClient client = HttpClients.custom()
                    .setDefaultRequestConfig(config)
                    .setConnectionManager(connectionManager)
                    .build();
            return new ApacheHttpClientExecutor(client);
        }
    }
}
