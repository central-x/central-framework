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

package central.net.http.proxy.contract.spring.resolver;

import central.net.http.HttpRequest;
import central.net.http.body.Body;
import central.net.http.body.InputStreamBody;
import central.net.http.body.request.*;
import central.net.http.proxy.contract.spring.SpringResolver;
import central.lang.Assertx;
import central.lang.Stringx;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;

/**
 * 处理请求体
 *
 * @author Alan Yeh
 * @see RequestBody
 * @since 2022/07/18
 */
public class RequestBodyResolver implements SpringResolver {
    @Override
    public boolean support(Parameter parameter) {
        return parameter.isAnnotationPresent(RequestBody.class);
    }

    @Override
    public boolean resolve(HttpRequest request, Method method, Parameter parameter, Object arg) {
        var annotation = parameter.getAnnotation(RequestBody.class);

        // 如果 request.getBody() 不为空，说明已经处理过 Body，因此 Body 发生冲突
        Assertx.mustNull(request.getBody(), IllegalStateException::new, "Conflict body");

        // 必填校验
        Assertx.mustTrue(!annotation.required() || arg != null, "Required parameter '{}' is missing", parameter.getName());

        // 没有消息体，没办法处理
        if (arg == null) {
            return false;
        }

        // 如果没有 Content-Type，则根据参数进行处理
        if (request.getHeaders().getContentType() == null) {
            // 如果这个参数的类型就是 Body，那么就直接使用这个 Body
            if (arg instanceof Body body) {
                request.setBody(body);
                return true;
            }

            // 处理几个特殊的数据类型
            if (arg instanceof File file) {
                request.setBody(new FileBody(file));
            } else if (arg instanceof InputStream stream) {
                request.setBody(new InputStreamBody(stream));
            } else if (arg instanceof byte[] bytes) {
                request.setBody(new ByteArrayBody(bytes));
            } else if (arg instanceof String string) {
                request.setBody(new StringBody(string));
            } else {
                throw new IllegalArgumentException(Stringx.format("Unsupported parameter type '{}' for @RequestBody", parameter.getType().getName()));
            }

            return true;
        } else if (MediaType.APPLICATION_JSON.isCompatibleWith(request.getHeaders().getContentType())) {
            // 如果希望传输 application/json
            if (arg instanceof Body body) {
                request.setBody(body);
            } else if (arg instanceof Map<?, ?> map) {
                request.setBody(new JsonBody(map));
            } else if (arg instanceof String string) {
                request.setBody(new JsonBody(string));
            } else {
                request.setBody(new JsonBody(arg));
            }

            return true;
        } else if (MediaType.APPLICATION_FORM_URLENCODED.isCompatibleWith(request.getHeaders().getContentType())) {
            // 如果希望传输 application/x-www-form-urlencoded
            if (arg instanceof UrlEncodedBody body) {
                request.setBody(body);
            } else {
                request.setBody(new UrlEncodedBody(arg));
            }

            return true;
        } else if (MediaType.MULTIPART_FORM_DATA.isCompatibleWith(request.getHeaders().getContentType())) {
            // 如果希望传输 multipart/form-data
            if (arg instanceof MultipartFormBody body) {
                request.setBody(body);
            } else {
                request.setBody(new MultipartFormBody(arg));
            }

            return true;
        } else if (MediaType.APPLICATION_OCTET_STREAM.isCompatibleWith(request.getHeaders().getContentType())) {
            // 如果希望直接传输 application/octet-stream
            if (arg instanceof File file) {
                request.setBody(new FileBody(file));
                return true;
            } else if (arg instanceof String string) {
                request.setBody(new StringBody(string));
                return true;
            } else if (arg instanceof byte[] bytes) {
                request.setBody(new ByteArrayBody(bytes));
                return true;
            } else if (arg instanceof InputStream stream) {
                request.setBody(new InputStreamBody(stream));
                return true;
            }
        }

        throw new IllegalArgumentException(Stringx.format("Unsupported parameter type '{}' for @RequestBody", parameter.getType().getName()));
    }
}
