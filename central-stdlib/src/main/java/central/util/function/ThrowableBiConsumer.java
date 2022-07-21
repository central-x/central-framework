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
 * ThrowableBiConsumer
 *
 * @author Alan Yeh
 * @since 2022/07/12
 */
@FunctionalInterface
public interface ThrowableBiConsumer<T, U, E extends Exception> {

    static <T, U, E extends Exception> ThrowableBiConsumer<T, U, E> of(ThrowableBiConsumer<T, U, E> consumer) {
        return consumer;
    }

    /**
     * 消费
     *
     * @param t 第一个参数
     * @param u 第二个参数
     * @throws E 执行过程中的异常
     */
    void accept(T t, U u) throws E;

    /**
     * 忽略异常
     */
    default BiConsumer<T, U> ignoreThrows() {
        return (T t, U u) -> {
            try {
                accept(t, u);
            } catch (Exception ignored) {
            }
        };
    }

    /**
     * 隐匿异常
     *
     * @return 被包装后的函数
     */
    default BiConsumer<T, U> sneakThrows() {
        final var that = this;
        return new BiConsumer<T, U>() {
            @Override
            @SneakyThrows
            public void accept(T t, U u) {
                that.accept(t, u);
            }
        };
    }

    /**
     * 处理异常，返回异常处理器的结果
     *
     * @param handler 异常处理器，第一个参数是异常
     * @return 被包装后的消费者
     */
    default BiConsumer<T, U> catchThrows(Consumer<E> handler) {
        return (T t, U u) -> {
            try {
                accept(t, u);
            } catch (Exception cause) {
                handler.accept((E) cause);
            }
        };
    }

    /**
     * 处理异常，返回异常处理器的结果
     *
     * @param handler 异常处理器，第一、二个是原参数，第三个是异常
     * @return 被包装后的消费者
     */
    default BiConsumer<T, U> catchThrows(TriConsumer<T, U, E> handler) {
        return (T t, U u) -> {
            try {
                accept(t, u);
            } catch (Exception cause) {
                handler.accept(t, u, (E) cause);
            }
        };
    }
}
