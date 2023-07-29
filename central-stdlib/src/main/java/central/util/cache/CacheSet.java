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

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * 缓存集合
 *
 * @author Alan Yeh
 * @since 2023/06/10
 */
public interface CacheSet {

    /**
     * 返回集合中所有的元素
     */
    @Nullable Set<String> values();

    /**
     * 向集合里添加元素
     *
     * @param values 待添加元素
     * @return 被添加的元素数量
     */
    long add(@Nonnull String... values);

    /**
     * 向集合里添加元素
     *
     * @param values 待添加元素
     * @return 被添加的元素数量
     */
    long add(@Nonnull Collection<String> values);

    /**
     * 移除集合里的指定元素
     *
     * @param values 待移除元素
     * @return 被移除的元素数量
     */
    long remove(@Nonnull String... values);

    /**
     * 移除集合里的指定元素
     *
     * @param values 待移除元素
     * @return 被移除的元素数量
     */
    long remove(@Nonnull Collection<String> values);

    /**
     * 随机移除集合中的任一元素
     *
     * @return 被移除的元素
     */
    @Nullable String pop();

    /**
     * 随机移除集合中指定个数的元素
     *
     * @param count 移除个数
     * @return 被移除的元素
     */
    @Nonnull List<String> pop(long count);

    /**
     * 返回当前集合的元素数量
     *
     * @return 元素数量
     */
    long size();

    /**
     * 判断当前集合是否包含指定元素
     *
     * @param value 元素
     * @return 是否包含
     */
    boolean contains(@Nonnull String value);
}
