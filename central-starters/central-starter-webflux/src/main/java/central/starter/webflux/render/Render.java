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

package central.starter.webflux.render;

import central.io.IOStreamx;
import lombok.Getter;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;

/**
 * Http
 *
 * @author Alan Yeh
 * @since 2022/10/09
 */
public abstract class Render<T extends Render<?>> {
    @Getter
    protected ServerHttpRequest request;

    @Getter
    protected ServerHttpResponse response;

    public Render(ServerWebExchange exchange) {
        this.request = exchange.getRequest();
        this.response = exchange.getResponse();
    }

    public Render(ServerHttpRequest request, ServerHttpResponse response) {
        this.request = request;
        this.response = response;
    }

    /**
     * 设置响应头
     *
     * @param name  名
     * @param value 值
     */
    public T setHeader(String name, String value) {
        this.response.getHeaders().set(name, value);
        return (T) this;
    }

    /**
     * 添加 Cookie
     *
     * @param cookie cookie
     */
    public T addCookie(ResponseCookie cookie) {
        response.addCookie(cookie);
        return (T) this;
    }

    /**
     * 添加 Cookie
     *
     * @param name  名
     * @param value 值
     */
    public T addCookie(String name, String value) {
        response.addCookie(ResponseCookie.from(name, value).build());
        return (T) this;
    }

    /**
     * 设置状态码
     *
     * @param status 状态码
     */
    public T setStatus(HttpStatus status) {
        response.setStatusCode(status);
        return (T) this;
    }

    /**
     * 写响应
     */
    public abstract Mono<Void> render();

    /**
     * 写字符串到响应体
     *
     * @param body    字符串
     * @param charset 字符串编码
     */
    protected Mono<Void> writeString(String body, Charset charset) {
        return writeBytes(body.getBytes(charset));
    }

    /**
     * 写字节码到响应体
     *
     * @param bytes 字节码
     */
    protected Mono<Void> writeBytes(byte[] bytes) {
        return response.writeWith(DataBufferUtils.readInputStream(() -> new ByteArrayInputStream(bytes), new DefaultDataBufferFactory(), IOStreamx.BUFFER_SIZE))
                .then(Mono.defer(response::setComplete));
    }
}
