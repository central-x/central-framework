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

package central.util.concurrent;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * 过期无素
 *
 * @author Alan Yeh
 * @since 2022/11/09
 */
@EqualsAndHashCode
public class ExpiredElement<E> implements Expired {

    /**
     * 创建时间
     */
    @Getter
    private final long timestamp = System.currentTimeMillis();
    /**
     * 失效时间
     */
    @Getter
    private final Duration expires;
    /**
     * 元素
     */
    @Getter
    private final E element;

    public ExpiredElement(E element, Duration expires) {
        this.element = element;
        this.expires = expires;
    }

    @Override
    public long getExpire(TimeUnit unit) {
        return unit.convert((this.timestamp + this.expires.toMillis()) - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }
}
