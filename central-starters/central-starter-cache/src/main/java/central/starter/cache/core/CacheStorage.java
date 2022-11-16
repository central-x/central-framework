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

package central.starter.cache.core;

import java.time.Duration;

/**
 * 缓存存储
 *
 * @author Alan Yeh
 * @since 2022/11/15
 */
public interface CacheStorage {
    /**
     * 判断缓存是否存在
     *
     * @param key 缓存键
     */
    boolean exists(String key);

    /**
     * 获取缓存
     *
     * @param key 缓存键
     * @param <T> 缓存类型
     */
    <T> T get(String key);

    /**
     * 保存/覆盖缓存
     *
     * @param key          缓存键
     * @param value        缓存值
     * @param expires      有效期
     * @param dependencies 依存依赖
     */
    void put(String key, Object value, Duration expires, String... dependencies);

    /**
     * 删除缓存
     *
     * @param keys 缓存键
     */
    void evict(String... keys);
}
