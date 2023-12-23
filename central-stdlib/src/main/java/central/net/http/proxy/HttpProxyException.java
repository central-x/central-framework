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

package central.net.http.proxy;

import central.lang.Stringx;

import java.io.Serial;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * 代理异常
 *
 * @author Alan Yeh
 * @since 2023/12/23
 */
public class HttpProxyException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -2288694528830529474L;

    public HttpProxyException(Method method) {
        super(Stringx.format("{} {}#{}({})", method.getReturnType().getSimpleName(), method.getDeclaringClass().getSimpleName(), method.getName(), Arrays.stream(method.getParameterTypes()).map(Class::getSimpleName).collect(Collectors.joining(", "))));
    }

    public HttpProxyException(Method method, Throwable cause) {
        super(Stringx.format("{} {}#{}({})", method.getReturnType().getSimpleName(), method.getDeclaringClass().getSimpleName(), method.getName(), Arrays.stream(method.getParameterTypes()).map(Class::getSimpleName).collect(Collectors.joining(", "))), cause);
    }

    public HttpProxyException(Method method, String message) {
        super(Stringx.format("{} {}#{}({}): {}", method.getReturnType().getSimpleName(), method.getDeclaringClass().getSimpleName(), method.getName(), Arrays.stream(method.getParameterTypes()).map(Class::getSimpleName).collect(Collectors.joining(", ")), message));
    }

    public HttpProxyException(Method method, String message, Throwable cause) {
        super(Stringx.format("{} {}#{}({}): {}", method.getReturnType().getSimpleName(), method.getDeclaringClass().getSimpleName(), method.getName(), Arrays.stream(method.getParameterTypes()).map(Class::getSimpleName).collect(Collectors.joining(", ")), message), cause);
    }
}
