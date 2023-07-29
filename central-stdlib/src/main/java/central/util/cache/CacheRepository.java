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
import java.util.*;

/**
 * 缓存仓库
 *
 * @author Alan Yeh
 * @since 2023/06/10
 */
public interface CacheRepository {
    /**
     * 判断指定键的缓存是否存在
     *
     * @param key 缓存键
     */
    boolean hasKey(@Nonnull String key);

    /**
     * 删除指定键的数据
     *
     * @param key 缓存键
     * @return 如果指定键的缓存数据在，则返回 true，否则返回 false
     */
    boolean delete(@Nonnull String key);

    /**
     * 删除指定键集合的数据
     *
     * @param keys 缓存键集合
     * @return 被删除的缓存数量
     */
    long delete(@Nonnull Collection<String> keys);

    /**
     * 返回指定键的数据类型
     *
     * @param key 缓存键
     * @return 缓存值类型
     */
    @Nonnull DataType type(@Nonnull String key);

    /**
     * 获取所有的 Key
     */
    @Nonnull Set<String> keys();

    /**
     * 设置指定键的缓存的有效期
     *
     * @param key     缓存键
     * @param timeout 有效期
     * @return 如果指定键的缓存存在，则返回 true；否则返回 false
     */
    boolean expire(@Nonnull String key, @Nonnull Duration timeout);

    /**
     * 设置指定键的缓存在指定时间过期
     *
     * @param key  缓存键
     * @param date 过期时间
     * @return 如果指定键的缓存存在，则返回 true；否则返回 false
     */
    boolean expireAt(@Nonnull String key, @Nonnull Date date);

    /**
     * 移除指定键的过期时间
     *
     * @param key 缓存键
     * @return 如果指定键的缓存存在，则返回 true；否则返回 false
     */
    boolean persist(@Nonnull String key);

    /**
     * 清空缓存
     */
    void clear();

    /**
     * 获取指定键的缓存的剩余有效期
     *
     * @param key 缓存键
     * @return 缓存的有效期。如果缓存没有失效时间，则返回 null
     */
    @Nullable Duration getExpire(@Nonnull String key);

    /**
     * 获取指定键的缓存，并转换为 String 类型
     *
     * @param key 缓存键
     * @return 如果缓存不存在时，将自动创建
     * @throws ClassCastException 如果缓存值不兼容 {@link DataType#STRING} 时将抛出此异常
     */
    @Nonnull CacheValue opsValue(@Nonnull String key) throws ClassCastException;

    /**
     * 获取指定键的缓存，并转换为 List 类型
     *
     * @param key 缓存键
     * @return 如果缓存不存在时，将自动创建
     * @throws ClassCastException 如果缓存值不兼容 {@link DataType#LIST} 时将抛出此异常
     */
    @Nonnull CacheList opsList(@Nonnull String key) throws ClassCastException;

    /**
     * 获取指定键的缓存，并转换为 Queue 类型
     *
     * @param key 缓存键
     * @return 如果缓存不存在时，将自动创建
     * @throws ClassCastException 如果缓存值不兼容 {@link DataType#QUEUE} 时将抛出此异常
     */
    @Nonnull CacheQueue opsQueue(@Nonnull String key) throws ClassCastException;

    /**
     * 获取指定键的缓存，并转换为 Set 类型
     *
     * @param key 缓存键
     * @return 如果缓存不存在时，将自动创建
     * @throws ClassCastException 如果缓存值不兼容 {@link DataType#SET} 时将抛出此异常
     */
    @Nonnull CacheSet opsSet(@Nonnull String key) throws ClassCastException;

    /**
     * 获取指定键的缓存，并转换为 ZSet 类型
     *
     * @param key 缓存键
     * @return 如果缓存不存在时，将自动创建
     * @throws ClassCastException 如果缓存值不兼容 {@link DataType#ZSET} 时将抛出此异常
     */
    @Nonnull CacheSet opsZSet(@Nonnull String key) throws ClassCastException;

    /**
     * 获取指定的键的缓存，并转换为 Map 类型
     *
     * @param key 缓存键
     * @return 如果缓存不存在时，将自动创建
     * @throws ClassCastException 如果缓存值不兼容 {@link DataType#MAP} 时将抛出此异常
     */
    @Nonnull CacheMap opsMap(@Nonnull String key) throws ClassCastException;
}
