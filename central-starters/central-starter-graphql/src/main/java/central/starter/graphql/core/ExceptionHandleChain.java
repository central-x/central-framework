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

package central.starter.graphql.core;

import central.starter.graphql.core.exception.FallbackHandler;
import central.starter.graphql.core.exception.ResponseStatusExceptionHandler;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 异常处理链
 *
 * @author Alan Yeh
 * @since 2024/06/13
 */
public class ExceptionHandleChain {
    // 用户自定义异常处理器
    private final List<ExceptionHandler> handlers;
    // 内置异常处理器
    private final List<ExceptionHandler> internalHandlers = List.of(new ResponseStatusExceptionHandler());
    // 默认异常处理器
    private final FallbackHandler fallbackHandler = new FallbackHandler();

    public ExceptionHandleChain(List<ExceptionHandler> handlers) {
        this.handlers = new ArrayList<>(handlers);
        AnnotationAwareOrderComparator.sort(this.handlers);
    }

    /**
     * 处理异常
     *
     * @param method    抛出异常的方法
     * @param throwable 待处理的异常
     * @return 已处理的异常
     */
    public ResponseStatusException handle(Method method, Throwable throwable) {
        for (ExceptionHandler handler : handlers) {
            if (handler.support(throwable)) {
                return handler.handle(method, throwable);
            }
        }

        for (ExceptionHandler handler : internalHandlers) {
            return handler.handle(method, throwable);
        }

        return fallbackHandler.handle(method, throwable);
    }
}
