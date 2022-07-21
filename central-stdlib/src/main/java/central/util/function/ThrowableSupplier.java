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

import central.lang.Assertx;
import lombok.SneakyThrows;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Throwable Supplier
 *
 * @author Alan Yeh
 * @since 2022/07/11
 */
@FunctionalInterface
public interface ThrowableSupplier<V, E extends Exception> {
    static <V, E extends Exception> ThrowableSupplier<V, E> of(ThrowableSupplier<V, E> supplier) {
        return supplier;
    }

    /**
     * 获取值
     */
    V get() throws E;

    /**
     * 将本函数的返回值作为下一个函数的参数
     *
     * @param after 下一个函数
     * @param <R>   下一个函数的返回值类型
     * @param <RE>  下一个函数可能抛出的异常
     * @return 被包装后的函数
     */
    default <R, RE extends Exception> ThrowableSupplier<R, RE> andThen(ThrowableFunction<? super V, ? extends R, RE> after) {
        Assertx.mustNotNull(after, "Argument 'after' must not null");

        return () -> after.apply(sneakThrows().get());
    }

    /**
     * 忽略异常
     * 当出现异常时，将返回 null
     */
    default Supplier<V> ignoreThrows() {
        final var that = this;
        return () -> {
            try {
                return that.get();
            } catch (Exception ignored) {
                return null;
            }
        };
    }

    /**
     * 隐匿异常
     *
     * @return 被包装后的函数
     */
    default Supplier<V> sneakThrows() {
        final var that = this;
        return new Supplier<V>() {
            @Override
            @SneakyThrows
            public V get() {
                return that.get();
            }
        };
    }

    /**
     * 处理异常，返回异常处理器的结果
     *
     * @param handler 异常处理器
     */
    default Supplier<V> catchThrows(Supplier<V> handler) {
        return () -> {
            try {
                return get();
            } catch (Exception ignored) {
                return handler.get();
            }
        };
    }

    /**
     * 处理异常，返回异常处理器的结果
     *
     * @param handler 异常处理器，第一个参数是异常
     */
    default Supplier<V> catchThrows(Function<E, V> handler) {
        return () -> {
            try {
                return get();
            } catch (Exception cause) {
                return handler.apply((E) cause);
            }
        };
    }
}
