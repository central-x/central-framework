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

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import org.springframework.http.HttpStatusCode;

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
    protected HttpServletRequest request;
    @Getter
    protected HttpServletResponse response;

    @Getter
    protected HttpStatusCode status;

    /**
     * 设置请求上下文
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     */
    public Render(HttpServletRequest request, HttpServletResponse response) {
        this.request = Objects.requireNonNull(request);
        this.response = Objects.requireNonNull(response);
    }

    /**
     * 设置响应头
     */
    public T setHeader(String name, String value) {
        response.setHeader(name, value);
        return (T) this;
    }

    /**
     * 添加 Cookie
     */
    public T addCookie(Cookie cookie) {
        response.addCookie(cookie);
        return (T) this;
    }

    /**
     * 添加 Cookie
     */
    public T addCookie(String name, String value) {
        response.addCookie(new Cookie(name, value));
        return (T) this;
    }

    /**
     * 设置状态码
     */
    public T setStatus(HttpStatusCode status) {
        this.status = status;
        response.setStatus(status.value());
        return (T) this;
    }

    public abstract void render() throws IOException;
}
