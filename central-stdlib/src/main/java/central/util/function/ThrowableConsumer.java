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

package central.util.function;

import lombok.SneakyThrows;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Throwable Consumer
 *
 * @author Alan Yeh
 * @since 2022/07/11
 */
@FunctionalInterface
public interface ThrowableConsumer<T, E extends Exception> {

    static <T, E extends Exception> ThrowableConsumer<T, E> of(ThrowableConsumer<T, E> consumer) {
        return consumer;
    }

    /**
     * 消费
     *
     * @param t 入参
     */
    void accept(T t) throws E;

    /**
     * 忽略异常
     */
    default Consumer<T> ignoreThrows() {
        final var that = this;
        return (T t) -> {
            try {
                that.accept(t);
            } catch (Exception ignored) {
            }
        };
    }

    /**
     * 隐匿异常
     *
     * @return 被包装后的函数
     */
    default Consumer<T> sneakThrows() {
        final var that = this;
        return new Consumer<T>() {
            @Override
            @SneakyThrows
            public void accept(T t) {
                that.accept(t);
            }
        };
    }

    /**
     * 处理异常，返回异常处理器的结果
     *
     * @param handler 异常处理器
     * @return 被包装后的函数
     */
    default Consumer<T> catchThrows(Consumer<E> handler) {
        return (T t) -> {
            try {
                accept(t);
            } catch (Exception cause) {
                handler.accept((E) cause);
            }
        };
    }

    /**
     * 处理异常，返回异常处理器的结果
     *
     * @param handler 异常处理器
     * @return 被包装后的函数
     */
    default Consumer<T> catchThrows(BiConsumer<T, E> handler) {
        return (T t) -> {
            try {
                accept(t);
            } catch (Exception cause) {
                handler.accept(t, (E) cause);
            }
        };
    }
}
