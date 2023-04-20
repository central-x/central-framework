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

package central.starter.webmvc.servlet;

import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;

import java.net.URI;

/**
 * WebMvc Request
 * <p>
 * 支持常见的操作
 *
 * @author Alan Yeh
 * @since 2023/03/08
 */
public interface WebMvcRequest extends AttributedHttpServletRequest, TenantedHttpServletRequest {

    static WebMvcRequest of(HttpServletRequest request) {
        return DefaultWebMvcRequest.of(request);
    }

    /**
     * 获取当前请求地址
     */
    @Nonnull
    URI getUri();

    /**
     * 获取 Cookie 值
     */
    String getCookie(String name);

    /**
     * 是否支持指定响应体
     *
     * @param contentType 响应体类型
     */
    boolean isAcceptContentType(MediaType contentType);
}
