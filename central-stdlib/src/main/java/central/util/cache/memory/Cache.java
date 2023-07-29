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

package central.util.cache.memory;

import central.util.cache.DataType;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * 缓存
 *
 * @author Alan Yeh
 * @since 2023/07/06
 */
class Cache implements Delayed {

    /**
     * 创建时间（时间戳）
     */
    @Getter
    private final long timestamp = System.currentTimeMillis();

    /**
     * 键
     */
    @Getter
    private final String key;

    /**
     * 值
     */
    @Getter
    @Setter
    private Object value;

    /**
     * 值类型
     */
    @Getter
    private DataType type;

    /**
     * 记录该元素是否已无效
     */
    @Getter
    private boolean invalid = false;

    /**
     * 过期时间（时间戳）
     * 如果过期时间 < 0 表示永不过期
     */
    private long expire = -1;

    public Cache(String key, Object value, DataType type) {
        this.key = key;
        this.value = value;
        this.type = type;
    }

    /**
     * 判断该缓存是否永久有效的
     */
    public boolean isPermanent() {
        return this.expire < 0;
    }

    /**
     * 判断该缓存是否已过期
     */
    public boolean isExpired() {
        return this.expire > 0 && this.timestamp > this.expire;
    }

    /**
     * 设置当前缓存的过期时间
     */
    public void setExpire(Date expire) {
        this.expire = expire.getTime();
    }

    /**
     * 设置当前缓存为永久有效
     */
    public void persist() {
        this.expire = -1;
    }

    /**
     * 将缓存设为无效
     */
    public void invalid() {
        this.invalid = true;
    }

    @Override
    public long getDelay(@NotNull TimeUnit unit) {
        return unit.convert(this.expire - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(@NotNull Delayed o) {
        return (int) (this.getDelay(TimeUnit.MILLISECONDS) - o.getDelay(TimeUnit.MILLISECONDS));
    }
}