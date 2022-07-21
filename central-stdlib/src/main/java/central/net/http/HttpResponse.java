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

import central.net.http.body.Body;
import lombok.Getter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

/**
 * Http 响应
 *
 * @author Alan Yeh
 * @since 2022/07/14
 */
public abstract class HttpResponse<T extends Body> implements AutoCloseable {
    /**
     * 响应创建时间
     */
    @Getter
    private final long timestamp = System.currentTimeMillis();

    /**
     * 此响应的请求
     */
    @Getter
    private final HttpRequest request;

    public HttpResponse(HttpRequest request){
        this.request = request;
    }

    /**
     * 状态码
     */
    public abstract HttpStatus getStatus();

    /**
     * 状态码在 [200, 300) 之间为成功
     */
    public boolean isSuccess(){
        return this.getStatus().value() >= 200 && this.getStatus().value() < 300;
    }

    /**
     * 获取响应头
     */
    public abstract HttpHeaders getHeaders();

    /**
     * 获取响应体
     */
    public abstract T getBody();

    @Override
    public void close() throws Exception {
        if (this.getBody() != null){
            this.getBody().close();
        }
    }
}
