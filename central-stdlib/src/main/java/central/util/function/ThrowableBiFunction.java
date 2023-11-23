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
import jakarta.annotation.Nonnull;
import lombok.SneakyThrows;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Throwable BiFunction
 *
 * @author Alan Yeh
 * @since 2022/07/11
 */
@FunctionalInterface
public interface ThrowableBiFunction<T, U, R, E extends Exception> {
    static <T, U, R, E extends Exception> ThrowableBiFunction<T, U, R, E> of(ThrowableBiFunction<T, U, R, E> function) {
        return function;
    }

    /**
     * 使用给定的参数调用函数
     *
     * @param t 第一个参数
     * @param u 第二个参数
     * @return 参数返回值
     * @throws E 函数可能抛出的异常
     */
    R apply(T t, U u) throws E;

    /**
     * 将本函数的返回值作为下一个函数的参数
     *
     * @param after 下一个函数
     * @param <V>   下一个函数的返回值类型
     * @param <RE>  下一个函数可能抛出的异常
     * @return 被包装后的函数
     */
    default <V, RE extends Exception> ThrowableBiFunction<T, U, V, RE> andThen(@Nonnull ThrowableFunction<R, V, RE> after) {
        Assertx.mustNotNull(after, "Argument 'after' must not null");

        return (T t, U u) -> after.apply(sneakThrows().apply(t, u));
    }

    /**
     * 将本函数的返回值作为下一个函数的参数
     *
     * @param after 下一个参数
     * @param <RE>  下一个函数可能抛出的异常
     * @return 被包装后的函数
     */
    default <RE extends Exception> ThrowableBiConsumer<T, U, RE> andThen(@Nonnull ThrowableConsumer<R, RE> after) {
        Assertx.mustNotNull(after, "Argument 'after' must not null");

        return (T t, U u) -> after.accept(sneakThrows().apply(t, u));
    }

    /**
     * 忽略异常
     *
     * @return 新函数，当出现异常时，将返回 null
     */
    default BiFunction<T, U, R> ignoreThrows() {
        return (T t, U u) -> {
            try {
                return apply(t, u);
            } catch (Exception ex) {
                return null;
            }
        };
    }

    /**
     * 隐匿异常
     *
     * @return 被包装后的函数
     */
    default BiFunction<T, U, R> sneakThrows() {
        final var that = this;
        return new BiFunction<T, U, R>() {
            @Override
            @SneakyThrows
            public R apply(T t, U u) {
                return that.apply(t, u);
            }
        };
    }

    /**
     * 处理异常，返回异常处理器的结果
     *
     * @param handler 异常处理器，第一个参数是异常
     * @return 被包装后的函数
     */
    default BiFunction<T, U, R> catchThrows(Function<E, R> handler) {
        return (T t, U u) -> {
            try {
                return apply(t, u);
            } catch (Exception ex) {
                return handler.apply((E) ex);
            }
        };
    }

    /**
     * 处理异常，返回异常处理器的结果
     *
     * @param handler 异常处理器，第一、二个参数是原参数，第三个参数是异常
     * @return 被包装后的函数
     */
    default BiFunction<T, U, R> catchThrows(TriFunction<T, U, E, R> handler) {
        return (T t, U u) -> {
            try {
                return apply(t, u);
            } catch (Exception cause) {
                return handler.apply(t, u, (E) cause);
            }
        };
    }
}
