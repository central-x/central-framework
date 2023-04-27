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

import lombok.Getter;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Timeout Element
 *
 * @author Alan Yeh
 * @since 2023/04/28
 */
@Getter
public class TimeoutElement<E> {

    /**
     * 创建时间
     */
    private final long timestamp;

    /**
     * 超时时间
     */
    private long timeout;

    /**
     * 元素
     */
    private final E element;

    public TimeoutElement(E element, Duration timeout) {
        this.timestamp = System.currentTimeMillis();
        this.element = element;
        this.timeout = this.timestamp + TimeUnit.MILLISECONDS.convert(timeout);
    }

    /**
     * 刷新过期时间
     *
     * @param timeout 过期时间
     */
    public void refresh(Duration timeout) {
        this.timeout = System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(timeout);
    }

    /**
     * 判断当前元素是否有效
     */
    public boolean isTimeout() {
        // 元素已超时
        return System.currentTimeMillis() - this.timestamp >= 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeoutElement<?> that = (TimeoutElement<?>) o;
        return Objects.equals(element, that.element);
    }

    @Override
    public int hashCode() {
        return Objects.hash(element);
    }
}
