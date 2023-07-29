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

package central.util.cache;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.time.Duration;

/**
 * 缓存值
 *
 * @author Alan Yeh
 * @since 2023/06/10
 */
public interface CacheValue {
    /**
     * 设置值
     *
     * @param value 缓存值
     * @return 原来的缓存值
     */
    @Nullable String set(@Nonnull String value);

    /**
     * 设置值，并设置该值的有效期
     *
     * @param value   缓存值
     * @param timeout 有效期
     * @return 原来的缓存值
     */
    @Nullable String set(@Nonnull String value, @Nullable Duration timeout);

    /**
     * 如果值不存在时，设置值
     *
     * @param value 缓存值
     * @return 如果原来有值，则返回 false
     */
    boolean setIfAbsent(@Nonnull String value);

    /**
     * 如果值不存在时，设置值，并设置该值的有效期
     *
     * @param value   缓存值
     * @param timeout 缓存超时时间
     * @return 如果原来有值，则返回 false
     */
    boolean setIfAbsent(@Nonnull String value, @Nullable Duration timeout);

    /**
     * 如果值存在时，覆盖该值为新值
     *
     * @param value 缓存值
     * @return 如果原来有值，则返回 true
     */
    boolean setIfPresent(@Nonnull String value);

    /**
     * 如果值存在时，覆盖该值为新值，并设置该值的有效期
     *
     * @param value 缓存值
     * @return 如果原来有值，则返回 true
     */
    boolean setIfPresent(@Nonnull String value, @Nullable Duration timeout);

    /**
     * 获取缓存值
     */
    @Nullable String getValue();

    /**
     * 获取缓存值并删除
     */
    @Nullable String getAndDelete();

    /**
     * 自增数字
     *
     * @return 自增后数字
     * @throws NumberFormatException 原来的值不是数字时抛该异常
     */
    @Nonnull Long increment() throws NumberFormatException;

    /**
     * 自增数字
     *
     * @param delta 自增量
     * @return 自增后数字
     * @throws NumberFormatException 原来的值不是数字时抛该异常
     */
    @Nonnull Long increment(long delta) throws NumberFormatException;

    /**
     * 自减数字
     *
     * @return 自减后数字
     * @throws NumberFormatException 原来的值不是数字时抛该异常
     */
    @Nonnull Long decrement() throws NumberFormatException;

    /**
     * 自减数字
     *
     * @param delta 自减量
     * @return 自减后数字
     * @throws NumberFormatException 原来的值不是数字时抛该异常
     */
    @Nonnull Long decrement(long delta) throws NumberFormatException;
}
