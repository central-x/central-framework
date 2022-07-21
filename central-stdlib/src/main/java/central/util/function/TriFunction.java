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

import java.util.Objects;
import java.util.function.Function;

/**
 * 三员参数函数
 *
 * @author Alan Yeh
 * @since 2022/07/12
 */
@FunctionalInterface
public interface TriFunction<T, U, K, R> {

    /**
     * 使用指定参数执行函数
     *
     * @param t 第一个参数
     * @param u 第二个参数
     * @param k 第三个参数
     * @return 涵数执行结果
     */
    R apply(T t, U u, K k);

    /**
     * 将本函数的执行结果作为下一个函数的入参
     *
     * @param after 下一个函数
     * @param <V>   下一个函数返回值类型
     * @return 被包装后的函数
     */
    default <V> TriFunction<T, U, K, V> andThen(Function<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        return (T t, U u, K k) -> after.apply(apply(t, u, k));
    }
}
