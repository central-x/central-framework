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

import jakarta.annotation.Nonnull;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.Objects;

/**
 * Http 响应
 *
 * @author Alan Yeh
 * @since 2022/07/16
 */
public abstract class Render<T extends Render<?>> {
    @Getter
    private final HttpServletRequest request;
    @Getter
    private final HttpServletResponse response;

    @Getter
    private HttpStatusCode status;

    /**
     * 设置请求上下文
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     */
    public Render(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response) {
        this.request = Objects.requireNonNull(request);
        this.response = Objects.requireNonNull(response);
    }

    /**
     * 设置响应头
     */
    @SuppressWarnings("unchecked")
    public T setHeader(String name, String value) {
        response.setHeader(name, value);
        return (T) this;
    }

    /**
     * 设置响应体类型
     */
    @SuppressWarnings("unchecked")
    public T setContentType(MediaType contentType) {
        response.setHeader(HttpHeaders.CONTENT_TYPE, contentType.toString());
        return (T) this;
    }

    /**
     * 添加 Cookie
     */
    @SuppressWarnings("unchecked")
    public T addCookie(Cookie cookie) {
        response.addCookie(cookie);
        return (T) this;
    }

    /**
     * 添加 Cookie
     */
    @SuppressWarnings("unchecked")
    public T addCookie(String name, String value) {
        response.addCookie(new Cookie(name, value));
        return (T) this;
    }

    /**
     * 设置状态码
     */
    @SuppressWarnings("unchecked")
    public T setStatus(HttpStatusCode status) {
        this.status = status;
        response.setStatus(status.value());
        return (T) this;
    }

    public abstract void render() throws IOException;
}
