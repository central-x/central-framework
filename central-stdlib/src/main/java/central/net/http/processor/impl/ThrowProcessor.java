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

package central.net.http.processor.impl;

import central.io.IOStreamx;
import central.lang.reflect.TypeReference;
import central.net.http.HttpException;
import central.net.http.HttpRequest;
import central.net.http.HttpResponse;
import central.net.http.body.Body;
import central.net.http.processor.HttpProcessor;
import central.pattern.chain.ProcessChain;
import central.util.Jsonx;
import central.util.Objectx;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;
import java.rmi.RemoteException;

/**
 * 抛异常处理器
 *
 * @author Alan Yeh
 * @since 2022/11/03
 */
public class ThrowProcessor implements HttpProcessor {
    @Override
    public HttpResponse<? extends Body> process(HttpRequest target, ProcessChain<HttpRequest, HttpResponse<? extends Body>> chain) throws Throwable {
        try {
            return chain.process(target);
        } catch (Throwable throwable) {
            if (throwable instanceof HttpException ex) {
                var contentEncoding = StandardCharsets.UTF_8;
                var contentType = ex.getResponse().getHeaders().getContentType();
                if (contentType != null) {
                    contentEncoding = Objectx.getOrDefault(contentType.getCharset(), contentEncoding);
                }
                var body = IOStreamx.readText(ex.getResponse().getBody().getInputStream(), contentEncoding);
                if (MediaType.APPLICATION_JSON.isCompatibleWith(contentType)) {
                    var message = Jsonx.Default().deserialize(body, TypeReference.ofMap(String.class, Objectx.class));
                    body = message.get("message").toString();
                }

                var serials = HttpStatus.Series.resolve(ex.getResponse().getStatus().value());
                if (serials != null) {
                    switch (serials) {
                        case CLIENT_ERROR -> throw new IllegalArgumentException(body);
                        case SERVER_ERROR -> throw new RemoteException(body);
                    }
                }
            }
            throw throwable;
        }
    }
}
