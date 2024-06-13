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

package central.starter.graphql.core.exception;

import central.lang.Stringx;
import central.starter.graphql.core.ExceptionHandler;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Method;

/**
 * ResponseStatusException Handler
 *
 * @author Alan Yeh
 * @see ResponseStatusException
 * @since 2024/06/13
 */
public class ResponseStatusExceptionHandler implements ExceptionHandler {
    @Override
    public boolean support(Throwable throwable) {
        return throwable instanceof ResponseStatusException;
    }

    @NotNull
    @Override
    public ResponseStatusException handle(@NotNull Method method, @NotNull Throwable throwable) {
        ResponseStatusException exception = (ResponseStatusException) throwable;
        return new ResponseStatusException(exception.getStatusCode(), Stringx.format("执行 {}.{} 出现异常: {}", method.getDeclaringClass().getSimpleName(), method.getName(), throwable.getMessage()), exception);
    }
}
