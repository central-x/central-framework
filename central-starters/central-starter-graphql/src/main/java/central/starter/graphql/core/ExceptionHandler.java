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

import jakarta.annotation.Nonnull;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Method;

/**
 * GraphQL 异常处理器
 * <p>
 * 用于将 GraphQL 执行过程中发生的异常转化为 ResponseStatusException，处理后的异常将会被直接抛出
 *
 * @author Alan Yeh
 * @see ResponseStatusException
 * @since 2024/06/13
 */
public interface ExceptionHandler {
    /**
     * 是否可以处理该异常
     *
     * @param throwable 待处理的异常
     */
    boolean support(Throwable throwable);

    /**
     * 处理异常
     *
     * @param throwable 待处理的异常
     * @return 处理后的异常
     */
    @Nonnull
    ResponseStatusException handle(@Nonnull Method method, @Nonnull Throwable throwable);
}
