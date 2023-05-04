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

import central.bean.Nullable;
import lombok.Getter;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Expirable Value
 *
 * @author Alan Yeh
 * @since 2023/04/28
 */
public class ExpirableValue<V> {

    /**
     * 创建时间
     */
    @Getter
    private final long timestamp;

    /**
     * 过期时间点
     * <p>
     * 如果过期时间为 -1 时，值永不过期
     */
    @Getter
    private long expireTime;

    /**
     * 值
     */
    private final V value;

    public ExpirableValue(V value, Duration expires) {
        this.timestamp = System.currentTimeMillis();
        this.value = value;
        this.expireTime = this.timestamp + TimeUnit.MILLISECONDS.convert(expires);
    }

    public ExpirableValue(V value) {
        this.timestamp = System.currentTimeMillis();
        this.value = value;
        this.expireTime = -1;
    }

    public static <E> @Nonnull ExpirableValue<E> of(E value, Duration expires) {
        return new ExpirableValue<>(value, expires);
    }

    public static <E> @Nonnull ExpirableValue<E> of(E value) {
        return new ExpirableValue<>(value);
    }

    /**
     * 刷新过期时间
     *
     * @param expires 过期时间
     */
    public void expires(Duration expires) {
        this.expireTime = System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(expires);
    }

    /**
     * 设置为永不过期
     */
    public void permanent() {
        this.expireTime = -1;
    }

    /**
     * 判断当前元素是否已过期
     */
    public boolean isExpired() {
        if (this.expireTime < 0) {
            return false;
        } else {
            return System.currentTimeMillis() - this.timestamp >= 0;
        }
    }

    /**
     * 判断当前元素是否是永久有效的
     */
    public boolean isPermanent() {
        return this.expireTime < 0;
    }

    /**
     * 获取有效值
     * <p>
     * 如果当前元素已过期，则返回空
     */
    public @Nullable Optional<V> getValue() {
        if (this.isExpired()) {
            return Optional.empty();
        } else {
            return Optional.of(this.value);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExpirableValue<?> that = (ExpirableValue<?>) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
