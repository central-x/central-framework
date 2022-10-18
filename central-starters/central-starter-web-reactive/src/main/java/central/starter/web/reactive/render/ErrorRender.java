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

package central.starter.web.reactive.render;

import central.lang.Stringx;
import central.util.Jsonx;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.HashMap;

/**
 * 异常响应
 *
 * @author Alan Yeh
 * @since 2022/10/09
 */
public class ErrorRender extends Render<ErrorRender> {
    public ErrorRender(ServerWebExchange exchange) {
        super(exchange);
    }

    public ErrorRender(ServerHttpRequest request, ServerHttpResponse response) {
        super(request, response);
    }

    public static ErrorRender of(ServerWebExchange exchange) {
        return new ErrorRender(exchange);
    }

    public static ErrorRender of(ServerHttpRequest request, ServerHttpResponse response) {
        return new ErrorRender(request, response);
    }

    /**
     * 是否 DEBUG 模块
     * 如果为 true，则在响应体中返回 stacks
     */
    private boolean debug = false;

    /**
     * 异常信息
     */
    private String message;

    /**
     * 异常
     */
    private Throwable throwable;

    public ErrorRender setDebug(boolean debug) {
        this.debug = debug;
        return this;
    }

    /**
     * 返回文本异常
     *
     * @param status  状态码
     * @param message 异常信息
     */
    public Mono<Void> render(HttpStatus status, String message) {
        this.setStatus(status);
        this.message = message;
        return this.render();
    }

    /**
     * 返回异常
     *
     * @param status    状态码
     * @param throwable 异常
     */
    public Mono<Void> render(HttpStatus status, Throwable throwable) {
        this.setStatus(status);
        this.message = throwable.getLocalizedMessage();
        this.throwable = throwable;
        return this.render();
    }

    /**
     * 返回异常
     *
     * @param throwable 异常
     */
    public Mono<Void> render(Throwable throwable) {
        if (throwable instanceof ResponseStatusException ex) {
            return render(HttpStatus.resolve(ex.getStatusCode().value()), ex);
        } else {
            return render(HttpStatus.INTERNAL_SERVER_ERROR, throwable);
        }
    }

    @Override
    public Mono<Void> render() {
        String body;
        if (isAcceptJson()) {
            // 返回 JSON
            response.getHeaders().setContentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8));

            var json = new HashMap<String, Object>();
            json.put("message", this.message);
            if (this.debug && this.throwable != null) {
                var writer = new StringWriter();
                this.throwable.printStackTrace(new PrintWriter(writer));
                var stacks = Arrays.stream(writer.toString().split("[\n]")).map(it -> it.replaceFirst("[\t]", "   ")).toList();
                json.put("stacks", stacks);
            }

            body = Jsonx.Default().serialize(json);

        } else {
            // 返回 html
            this.getResponse().getHeaders().setContentType(new MediaType(MediaType.TEXT_HTML, StandardCharsets.UTF_8));
            body = Stringx.format("<html><body><h2>{} ({})</h2><p>{}</p><div id='created'>{}</div></body></html>", HttpStatus.resolve(this.response.getStatusCode().value()).getReasonPhrase(), this.response.getStatusCode().value(), this.message, OffsetDateTime.now().toString());
        }

        return this.writeString(body, StandardCharsets.UTF_8);
    }

    private boolean isAcceptJson() {
        var accepts = this.getRequest().getHeaders().getAccept();

        for (var accept : accepts) {
            if ("*".equals(accept.getType()) && "*".equals(accept.getSubtype())) {
                // accept */*
                continue;
            }

            if (accept.isCompatibleWith(MediaType.APPLICATION_JSON)) {
                return true;
            }
        }
        return false;
    }
}
