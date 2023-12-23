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

package central.net.http;

import central.io.IOStreamx;
import central.lang.Stringx;
import central.lang.reflect.TypeRef;
import central.util.Jsonx;
import central.util.Objectx;
import lombok.Getter;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.io.Serial;
import java.nio.charset.StandardCharsets;

/**
 * Http Exception
 *
 * @author Alan Yeh
 * @since 2022/07/14
 */
public class HttpException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 8580026645615849189L;

    @Getter
    private transient final HttpRequest request;

    @Getter
    private transient final HttpResponse response;

    public static HttpException of(HttpRequest request, HttpResponse response) {
        // 解析响应体
        var contentEncoding = StandardCharsets.UTF_8;
        var contentType = response.getHeaders().getContentType();
        if (contentType != null) {
            contentEncoding = Objectx.getOrDefault(contentType.getCharset(), contentEncoding);
        }

        var body = "";
        if (MediaType.APPLICATION_JSON.isCompatibleWith(contentType)) {
            try {
                // 尝试读取异常
                body = IOStreamx.readText(response.getBody().getInputStream(), contentEncoding);
            } catch (IOException ignored) {
            }

            if (Stringx.isNotBlank(body)){
                var message = Jsonx.Default().deserialize(body, TypeRef.ofMap(String.class, Object.class));
                body = message.get("message").toString();
            }
        } else if (MediaType.TEXT_PLAIN.isCompatibleWith(contentType)) {
            try {
                // 尝试读取异常
                body = IOStreamx.readText(response.getBody().getInputStream(), 50, contentEncoding);
            } catch (IOException ignored) {
            }
        }

        if (Stringx.isNotBlank(body)) {
            return new HttpException(request, response, body);
        } else {
            return new HttpException(request, response);
        }
    }

    public HttpException(String message, HttpRequest request, HttpResponse response) {
        super(message);
        this.request = request;
        this.response = response;
    }

    public HttpException(HttpRequest request, HttpResponse response) {
        super(Stringx.format("[{} {}] {} {}", response.getStatus().value(), response.getStatus().getReasonPhrase(), request.getMethod().name(), request.getUrl()));
        this.request = request;
        this.response = response;
    }

    private HttpException(HttpRequest request, HttpResponse response, String body) {
        super(Stringx.format("[{} {}] {} {}: {}", response.getStatus().value(), response.getStatus().getReasonPhrase(), request.getMethod().name(), request.getUrl(), body));
        this.request = request;
        this.response = response;
    }

    public HttpException(HttpRequest request, HttpResponse response, String message, Throwable cause) {
        super(message, cause);
        this.request = request;
        this.response = response;
    }
}
