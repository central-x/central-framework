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
