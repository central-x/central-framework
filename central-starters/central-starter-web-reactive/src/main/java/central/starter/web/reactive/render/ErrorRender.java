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
import central.util.Listx;
import central.util.Objectx;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
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
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
     * 调试模式
     */
    private boolean debug = false;

    /**
     * 打开调试模式
     * <p>
     * 输出异常栈
     */
    public ErrorRender debug() {
        this.debug = true;
        return this;
    }

    /**
     * 设置是否为调试模式
     * <p>
     * 是否输出异常栈
     */
    public ErrorRender debug(boolean debug) {
        this.debug = debug;
        return this;
    }

    /**
     * 异常
     */
    private Throwable throwable;

    /**
     * 设置异常
     *
     * @param throwable 异常
     */
    public ErrorRender setError(Throwable throwable) {
        if (throwable instanceof ResponseStatusException exception) {
            return setError(exception.getStatusCode(), throwable);
        } else {
            return setError(HttpStatus.INTERNAL_SERVER_ERROR, throwable);
        }
    }

    /**
     * 设置状态码和异常
     *
     * @param status    状态码
     * @param throwable 异常
     */
    public ErrorRender setError(HttpStatusCode status, Throwable throwable) {
        this.setStatus(status);
        this.throwable = throwable;
        return this;
    }

    /**
     * 设置错误信息
     *
     * @param message 错误信息
     */
    public ErrorRender setError(String message) {
        return this.setError(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    /**
     * 设置错误信息和状态码
     *
     * @param status  状态码
     * @param message 错误信息
     */
    public ErrorRender setError(HttpStatus status, String message) {
        return this.setError(status, new RuntimeException(message));
    }

    /**
     * 执行渲染
     *
     * @param message 错误信息
     */
    public Mono<Void> render(String message) {
        return this.setError(message).render();
    }

    /**
     * 执行渲染
     *
     * @param status  状态码
     * @param message 错误信息
     */
    public Mono<Void> render(HttpStatus status, String message) {
        return this.setError(status, message).render();
    }

    /**
     * 返回异常
     *
     * @param throwable 异常
     */
    public Mono<Void> render(Throwable throwable) {
        return this.setError(throwable).render();
    }

    /**
     * 返回异常
     *
     * @param status    状态码
     * @param throwable 异常
     */
    public Mono<Void> render(HttpStatus status, Throwable throwable) {
        return this.setError(status, throwable).render();
    }

    @Override
    public Mono<Void> render() {
        response.getHeaders().setPragma("no-cache");
        response.getHeaders().setCacheControl("no-cache");
        response.getHeaders().setExpires(0);

        var accepts = this.getRequest().getHeaders().getAccept();

        if (Listx.isNullOrEmpty(accepts)) {
            accepts = List.of(MediaType.ALL);
        }

        if (accepts.stream().anyMatch(MediaType.APPLICATION_JSON::includes)) {
            return this.renderJson();
        } else {
            return this.renderHtml();
        }
    }

    private static final MediaType JSON_CONTENT_TYPE = new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8);

    /**
     * 渲染 JSON
     */
    private Mono<Void> renderJson() {
        // 返回 JSON
        response.getHeaders().setContentType(JSON_CONTENT_TYPE);

        var json = new HashMap<String, Object>();
        json.put("message", this.throwable.getLocalizedMessage());
        json.put("timestamp", System.currentTimeMillis());

        if (this.debug && this.throwable != null) {
            var writer = new StringWriter();
            throwable.printStackTrace(new PrintWriter(writer));
            var stacks = Arrays.stream(writer.toString().split("[\n]")).map(it -> it.replaceFirst("[\t]", "   ")).toList();
            json.put("stacks", stacks);
        }

        var body = Jsonx.Default().serialize(json);

        return this.writeString(body, StandardCharsets.UTF_8);
    }

    private static final MediaType HTML_CONTENT_TYPE = new MediaType(MediaType.TEXT_HTML, StandardCharsets.UTF_8);

    /**
     * 渲染 HTML
     */
    private Mono<Void> renderHtml() {
        this.getResponse().getHeaders().setContentType(HTML_CONTENT_TYPE);

        var content = """
                <!DOCTYPE html>
                <html lang="en">
                  <head>
                    <meta charset="UTF-8">
                    <title>{}</title>
                  </head>
                  <body>
                    <h2>{} ({})</h2>
                    <p>{}</p>{}
                    <p>
                      <div id='created'>{}</div>
                    </p>
                  </body>
                </html>
                """;
        String stack = "";

        if (this.debug) {
            // 输出异常信息
            var writer = new StringWriter();
            throwable.printStackTrace(new PrintWriter(writer));
            stack = Arrays.stream(writer.toString().split("[\n]")).map(it -> it.replaceFirst("[\t]", "&nbsp;&nbsp;&nbsp;")).collect(Collectors.joining("<br/>\n"));
            stack = "\n    <p>\n" + stack + "\n    </p>";
        }

        var status = Objectx.getOrDefault(HttpStatus.resolve(Objects.requireNonNull(this.getResponse().getStatusCode()).value()), HttpStatus.INTERNAL_SERVER_ERROR);
        var body = Stringx.format(content, status.getReasonPhrase(), status.getReasonPhrase(), status.value(), throwable.getLocalizedMessage(), stack, OffsetDateTime.now().toString());

        return this.writeString(body, StandardCharsets.UTF_8);
    }
}
