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

package central.starter.webmvc.render;

import central.lang.Stringx;
import central.util.Jsonx;
import central.util.Objectx;
import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * Error Render
 *
 * @author Alan Yeh
 * @since 2022/07/16
 */
public class ErrorRender extends Render<ErrorRender> {
    public ErrorRender(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response) {
        super(request, response);
    }

    /**
     * 异常
     */
    @Getter
    @Setter
    private Throwable throwable;

    /**
     * 调试模式
     */
    @Getter
    @Setter
    private boolean debug = false;

    /**
     * 设置错误信息
     *
     * @param message 错误信息
     */
    public ErrorRender setError(String message) {
        return this.setError(HttpStatus.INTERNAL_SERVER_ERROR, new RuntimeException(message));
    }

    /**
     * 设置错误信息和状态码
     *
     * @param status  状态码
     * @param message 错误信息
     */
    public ErrorRender setError(HttpStatusCode status, String message) {
        return this.setError(status, new RuntimeException(message));
    }

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
     * 执行渲染
     *
     * @param message 错误信息
     */
    public void render(String message) throws IOException {
        this.setError(message).render();
    }

    /**
     * 执行渲染
     *
     * @param status  状态码
     * @param message 错误信息
     */
    public void render(HttpStatusCode status, String message) throws IOException {
        this.setError(status, message).render();
    }

    /**
     * 执行渲染
     *
     * @param status    状态码
     * @param throwable 异常
     */
    public void render(HttpStatus status, Throwable throwable) throws IOException {
        this.setError(status, throwable).render();
    }

    /**
     * 执行渲染
     *
     * @param throwable 异常
     */
    public void render(Throwable throwable) throws IOException {
        this.setError(throwable).render();
    }

    @Override
    public void render() throws IOException {
        this.getResponse().setHeader(HttpHeaders.PRAGMA, "no-cache");
        this.getResponse().setHeader(HttpHeaders.CACHE_CONTROL, "no-cache");
        this.getResponse().setDateHeader(HttpHeaders.EXPIRES, 0);

        var accepts = MediaType.parseMediaTypes(Objectx.getOrDefault(this.getRequest().getHeader(HttpHeaders.ACCEPT), MediaType.ALL_VALUE));

        if (accepts.stream().anyMatch(MediaType.APPLICATION_JSON::includes)) {
            this.renderJson();
        } else {
            this.renderHtml();
        }
    }

    private static final String JSON_CONTENT_TYPE = new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8).toString();

    /**
     * 渲染 JSON
     */
    private void renderJson() throws IOException {
        this.getResponse().setContentType(JSON_CONTENT_TYPE);

        // 需要返回 JSON 格式的数据
        var json = new HashMap<String, Object>();
        json.put("message", throwable.getLocalizedMessage());
        json.put("timestamp", System.currentTimeMillis());

        if (this.debug) {
            // 输出异常信息
            var writer = new StringWriter();
            throwable.printStackTrace(new PrintWriter(writer));
            var stacks = Arrays.stream(writer.toString().split("[\n]")).map(it -> it.replaceFirst("[\t]", "   ")).toList();
            json.put("stacks", stacks);
        }
        var body = Jsonx.Default().serialize(json);

        try (var writer = this.getResponse().getWriter()) {
            writer.write(body);
        }
    }

    private static final String HTML_CONTENT_TYPE = new MediaType(MediaType.TEXT_HTML, StandardCharsets.UTF_8).toString();

    /**
     * 渲染 HTML
     */
    private void renderHtml() throws IOException {
        // 返回 HTML 异常信息
        this.getResponse().setContentType(HTML_CONTENT_TYPE);

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

        var status = Objectx.getOrDefault(HttpStatus.resolve(this.getResponse().getStatus()), HttpStatus.INTERNAL_SERVER_ERROR);
        var body = Stringx.format(content, status.getReasonPhrase(), status.getReasonPhrase(), status.value(), throwable.getLocalizedMessage(), stack, OffsetDateTime.now().toString());

        try (var writer = this.getResponse().getWriter()) {
            writer.write(body);
        }
    }
}
