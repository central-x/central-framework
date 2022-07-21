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

import lombok.Getter;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * 延迟队列元素
 *
 * @author Alan Yeh
 * @since 2022/07/14
 */
public class DelayedElement<E> implements Delayed {
    /**
     * 创建时间
     */
    @Getter
    private final long timestamp;
    /**
     * 延迟时间
     */
    @Getter
    private final long delay;
    /**
     * 再次延迟次数
     */
    @Getter
    private final int times;
    /**
     * 元素
     */
    @Getter
    private final E element;

    public DelayedElement(E element){
        this(element, 0, 0);
    }

    public DelayedElement(E element, long delay){
        this(element, delay, 0);
    }

    public DelayedElement(E element, long delay, int times) {
        this.element = element;
        this.timestamp = System.currentTimeMillis();
        this.delay = delay;
        this.times = times;
    }

    private DelayedElement(E element,long timestamp, long delay, int times ){
        this.element = element;
        this.timestamp = timestamp;
        this.delay = delay;
        this.times = times;
    }

    /**
     * 再次延长
     */
    public DelayedElement<E> delay(long delay){
        return new DelayedElement<>(this.getElement(), this.getTimestamp(), this.getDelay() + delay, this.getTimes() + 1);
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert((this.timestamp + this.delay) - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        return (int) (this.getDelay(TimeUnit.MILLISECONDS) - o.getDelay(TimeUnit.MILLISECONDS));
    }
}
