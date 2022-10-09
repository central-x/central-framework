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

package central.util;

import central.lang.Assertx;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 上下文容器
 *
 * @author Alan Yeh
 * @since 2022/09/23
 */
public class Context {
    private final Map<String, Object> context = new ConcurrentHashMap<>();

    /**
     * 克隆上下文
     *
     * @param context 待克隆上下文
     */
    public static Context clone(Context context) {
        var clone = new Context();
        clone.context.putAll(context.context);
        return clone;
    }

    /**
     * 判断是否存在上下文
     *
     * @param key 键
     */
    public boolean contains(@Nonnull String key) {
        return this.context.containsKey(key);
    }

    /**
     * 获取上下文
     *
     * @param key 键
     * @return 值
     */
    public <T> @Nullable T get(@Nonnull String key) {
        return (T) context.get(key);
    }

    /**
     * 获取上下文
     *
     * @param key 键
     * @return 值
     */
    public <T> @Nonnull T require(@Nonnull String key) {
        return (T) Assertx.requireNotNull(context.get(key), NullPointerException::new, "找不到 {} 指定的 Key", key);
    }

    /**
     * 保存上下文键值对
     *
     * @param key   键
     * @param value 值
     */
    public void set(@Nonnull String key, Object value) {
        if (value == null) {
            this.context.remove(key);
        } else {
            this.context.put(key, value);
        }
    }

    /**
     * 移除上下文键值对
     *
     * @param key 键
     */
    public void remove(@Nonnull String key) {
        this.context.remove(key);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 判断是否存在上下文
     *
     * @param key 键
     */
    public <T> boolean contains(@Nonnull Class<T> key) {
        return this.context.containsKey(key.getCanonicalName());
    }

    /**
     * 保存上下文
     *
     * @param key   类型
     * @param value 值
     */
    public <T> void set(@Nonnull Class<T> key, T value) {
        if (value == null) {
            this.context.remove(key.getCanonicalName());
        } else {
            this.context.put(key.getCanonicalName(), value);
        }
    }

    /**
     * 保存值
     *
     * @param value 值
     */
    public void set(@Nonnull Object value) {
        this.context.put(value.getClass().getCanonicalName(), value);
    }

    /**
     * 根据类型获取上下文
     *
     * @param key 类型
     * @return 值
     */
    public <T> @Nullable T get(@Nonnull Class<T> key) {
        return (T) context.get(key.getCanonicalName());
    }

    /**
     * 根据类型获取上下文
     *
     * @param key 类型
     * @return 值
     */
    public <T> @Nonnull T require(@Nonnull Class<T> key) {
        return (T) Assertx.requireNotNull(context.get(key.getCanonicalName()), NullPointerException::new, "找不到 {} 指定的 Key", key.getName());
    }

    /**
     * 移除上下文键值对
     *
     * @param key 类型
     */
    public <T> void remove(@Nonnull Class<T> key) {
        this.context.remove(key.getCanonicalName());
    }
}
