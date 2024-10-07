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

package central.starter.webmvc.view;

import central.lang.Stringx;
import central.util.Jsonx;
import central.util.Mapx;
import central.util.Objectx;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.ErrorResponse;
import org.springframework.web.servlet.View;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Error View
 * <p>
 * 错误视图
 *
 * @author Alan Yeh
 * @since 2023/10/29
 */
public class ErrorView implements View {

    /**
     * 异常
     */
    @Getter
    @Setter
    private Throwable throwable;

    /**
     * 是否输出详细异常信息
     */
    @Getter
    @Setter
    private boolean print;

    public ErrorView(Throwable throwable) {
        this(throwable, false);
    }

    public ErrorView(Throwable throwable, boolean print) {
        this.throwable = throwable;
        this.print = print;
    }

    public ErrorView(String message) {
        this(new RuntimeException(message), false);
    }

    @Override
    public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setHeader(HttpHeaders.PRAGMA, "no-cache");
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-cache");
        response.setDateHeader(HttpHeaders.EXPIRES, 0);

        if (this.throwable instanceof ErrorResponse error) {
            var headers = error.getHeaders();
            if (Mapx.isNotEmpty(headers)) {
                headers.forEach((name, values) -> values.forEach(value -> response.setHeader(name, value)));
            }
        }

        // 渲染响应体
        var accepts = MediaType.parseMediaTypes(Objectx.getOrDefault(request.getHeader(HttpHeaders.ACCEPT), MediaType.ALL_VALUE));

        if (accepts.stream().anyMatch(MediaType.APPLICATION_JSON::includes)) {
            this.renderJson(request, response);
        } else {
            this.renderHtml(request, response);
        }
    }

    private static final String JSON_CONTENT_TYPE = new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8).toString();

    /**
     * 渲染 JSON
     */
    private void renderJson(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 客户端要求返回 JSON 格式
        response.setContentType(JSON_CONTENT_TYPE);
        var body = new HashMap<String, Object>();
        if (this.throwable instanceof ErrorResponse error) {
            body.put("message", error.updateAndGetBody(null, request.getLocale()).getDetail());
        } else {
            body.put("message", this.throwable.getLocalizedMessage());
        }
        body.put("timestamp", System.currentTimeMillis());

        if (this.print) {
            // 输出异常信息
            var writer = new StringWriter();
            this.throwable.printStackTrace(new PrintWriter(writer));
            var reason = Arrays.stream(writer.toString().split("[\n]")).map(it -> it.replaceFirst("[\t]", "   ")).toList();
            body.put("stacks", reason);
        }

        // 写入响应体
        try (var writer = response.getWriter()) {
            writer.write(Jsonx.Default().serialize(body));
        }
    }

    private static final String HTML_CONTENT_TYPE = new MediaType(MediaType.TEXT_HTML, StandardCharsets.UTF_8).toString();

    /**
     * 渲染 HTML
     */
    private void renderHtml(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 返回 HTML 异常信息
        response.setContentType(HTML_CONTENT_TYPE);

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

        if (this.print) {
            // 输出异常信息
            var writer = new StringWriter();
            this.throwable.printStackTrace(new PrintWriter(writer));
            stack = Arrays.stream(writer.toString().split("[\n]")).map(it -> it.replaceFirst("[\t]", "&nbsp;&nbsp;&nbsp;")).collect(Collectors.joining("<br/>\n"));
            stack = "\n    <p>\n" + stack + "\n    </p>";
        }

        try (var writer = response.getWriter()) {
            var status = Objectx.getOrDefault(HttpStatus.resolve(response.getStatus()), HttpStatus.INTERNAL_SERVER_ERROR);

            var message = this.throwable.getLocalizedMessage();
            if (this.throwable instanceof ErrorResponse error) {
                message = error.updateAndGetBody(null, request.getLocale()).getDetail();
            }

            writer.write(Stringx.format(content, status.getReasonPhrase(), status.getReasonPhrase(), status.value(), message, stack, OffsetDateTime.now().toString()));
        }
    }
}
