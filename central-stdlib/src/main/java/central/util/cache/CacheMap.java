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
import java.util.Map;
import java.util.Set;

/**
 * 缓存键值对
 *
 * @author Alan Yeh
 * @since 2023/06/10
 */
public interface CacheMap {
    /**
     * 移除指定的键的值
     *
     * @param keys 待移除键
     * @return 被删除的元素数量
     */
    long delete(@Nonnull String... keys);

    /**
     * 判断是否包含指定键的元素
     *
     * @param key 待判断键
     * @return 是否包含
     */
    boolean hasKey(@Nonnull String key);

    /**
     * 获取指定键的元素
     *
     * @param key 待取元素键
     * @return 已取出的元素
     */
    @Nullable String get(@Nonnull String key);

    /**
     * 获取指定键的元素
     *
     * @param keys 待取元素键集合
     * @return 已取出的元素
     */
    @Nonnull List<String> get(@Nonnull String... keys);

    /**
     * 获取指定键的元素
     *
     * @param keys 待取元素键集合
     * @return 已取出的元素
     */
    @Nonnull List<String> get(@Nonnull Collection<String> keys);

    /**
     * 获取所有键
     */
    @Nonnull Set<String> keys();

    /**
     * 获取所有元素数量
     */
    long size();

    /**
     * 保存指定值
     *
     * @param key   键
     * @param value 值
     */
    void put(@Nonnull String key, @Nonnull String value);

    /**
     * 保存指定的键的值
     *
     * @param values 待保存的元素
     */
    void putAll(@Nonnull Map<String, String> values);

    /**
     * 如果当前 Map 不存在指定的键，则保存为新值
     *
     * @param map 待保存的元素
     * @return 是否保存民功
     */
    boolean putIfAbsent(@Nonnull Map<String, String> values);

    /**
     * 获取所有元素
     */
    @Nonnull List<String> values();

    /**
     * 获取整个 Map
     */
    @Nonnull Map<String, String> entries();
}
