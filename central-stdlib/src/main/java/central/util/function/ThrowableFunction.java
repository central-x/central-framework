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
import central.lang.Exceptionx;
import lombok.SneakyThrows;

import javax.annotation.Nonnull;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Throwable Function
 *
 * @author Alan Yeh
 * @since 2022/07/11
 */
@FunctionalInterface
public interface ThrowableFunction<T, R, E extends Exception> {
    static <T, R, E extends Exception> ThrowableFunction<T, R, E> of(ThrowableFunction<T, R, E> function) {
        return function;
    }

    /**
     * 调用方法
     */
    R apply(T t) throws E;

    /**
     * 将本函数的返回值作为下一个函数的参数
     *
     * @param after 下一个函数
     * @param <V>   下一个函数的返回值类型
     * @param <RE>  下一个函数可能抛出的异常
     * @return 被包装后的函数
     */
    default <V, RE extends Exception> ThrowableFunction<T, V, RE> andThen(@Nonnull ThrowableFunction<? super R, ? extends V, RE> after) {
        Assertx.mustNotNull(after, "Argument 'after' must not null");

        return (T t) -> after.apply(sneakThrows().apply(t));
    }

    /**
     * 将本函数的返回值作为下一个函数的参数
     *
     * @param after 下一个函数
     * @param <RE>  下一个函数可能抛出的异常
     * @return 被包装后的函数
     */
    default <RE extends Exception> ThrowableConsumer<T, RE> andThen(ThrowableConsumer<? super R, RE> after) {
        Assertx.mustNotNull(after, "Argument 'after' must not null");

        return (T t) -> after.accept(sneakThrows().apply(t));
    }

    /**
     * 忽略异常
     *
     * @return 被包装后的函数
     */
    default Function<T, R> ignoreThrows() {
        return (T t) -> {
            try {
                return apply(t);
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
    default Function<T, R> sneakThrows() {
        final var that = this;
        return new Function<T, R>() {
            @Override
            @SneakyThrows
            public R apply(T t) {
                return that.apply(t);
            }
        };
    }

    /**
     * 处理异常，返回异常处理器的结果
     *
     * @param handler 异常处理器，第一个参数是异常信息
     * @return 被包装后的函数
     */
    default Function<T, R> catchThrows(Function<E, R> handler) {
        return (T t) -> {
            try {
                return apply(t);
            } catch (Exception cause) {
                return handler.apply((E) cause);
            }
        };
    }

    /**
     * 处理异常，返回异常处理器的结果
     *
     * @param handler 异常处理器，第一个参数是原调用参数，第二个参数是异常信息
     * @return 被包装后的函数
     */
    default Function<T, R> catchThrows(BiFunction<T, E, R> handler) {
        return (T t) -> {
            try {
                return apply(t);
            } catch (Exception cause) {
                return handler.apply(t, (E) cause);
            }
        };
    }
}
