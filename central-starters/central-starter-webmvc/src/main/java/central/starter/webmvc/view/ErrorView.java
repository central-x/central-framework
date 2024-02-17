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
import central.util.Listx;
import central.util.Objectx;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.View;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

/**
 * Error View
 * <p>
 * 错误视图
 *
 * @author Alan Yeh
 * @since 2023/10/29
 */
public class ErrorView implements View {

    @Getter
    @Setter
    private HttpStatusCode status;

    @Getter
    @Setter
    private String message;

    public ErrorView(Throwable throwable) {
        if (throwable instanceof ResponseStatusException error) {
            this.status = error.getStatusCode();
            this.message = error.getReason();
        } else {
            this.status = HttpStatus.INTERNAL_SERVER_ERROR;
            this.message = throwable.getMessage();
        }
    }

    public ErrorView(HttpStatusCode status, String message) {
        this.status = status;
        this.message = message;
    }

    @Override
    public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setHeader(HttpHeaders.PRAGMA, "no-cache");
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-cache");
        response.setDateHeader(HttpHeaders.EXPIRES, 0);

        if (isAcceptJson(MediaType.parseMediaTypes(request.getHeader(HttpHeaders.ACCEPT)))) {
            response.setContentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8).toString());
            try (var writer = response.getWriter()) {
                writer.write(Jsonx.Default().serialize(Map.of("message", this.message)));
            }
        } else {
            response.setContentType(new MediaType(MediaType.TEXT_HTML, StandardCharsets.UTF_8).toString());
            try (var writer = response.getWriter()) {
                var reason = Objectx.getOrDefault(HttpStatus.resolve(this.getStatus().value()), HttpStatus.INTERNAL_SERVER_ERROR).getReasonPhrase();
                var content = Stringx.format("<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\"><title>{}</title></head><body><h2>{} ({})</h2><p>{}</p><div id='created'>{}</div></body></html>", reason, reason, this.getStatus().value(), this.message, OffsetDateTime.now().toString());
                writer.write(content);
            }

        }
    }

    private boolean isAcceptJson(List<MediaType> types) {
        if (Listx.isNullOrEmpty(types)) {
            return false;
        }
        return types.size() == 1 && MediaType.APPLICATION_JSON.equalsTypeAndSubtype(Listx.getFirstOrNull(types));
    }
}
