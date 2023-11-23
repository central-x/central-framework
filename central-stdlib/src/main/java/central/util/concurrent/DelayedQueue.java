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

import jakarta.annotation.Nonnull;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

/**
 * 延迟队列
 *
 * @author Alan Yeh
 * @since 2022/07/14
 */
public class DelayedQueue<E extends Delayed> extends AbstractQueue<E> implements BlockingQueue<E> {

    private final transient ReentrantLock lock = new ReentrantLock();
    private final PriorityQueue<E> queue = new PriorityQueue<>();

    private Thread leader = null;

    private final Condition available = lock.newCondition();

    public DelayedQueue() {
    }

    public DelayedQueue(Collection<? extends E> c) {
        this.addAll(c);
    }

    @Override
    public boolean add(@Nonnull E element) {
        return this.offer(element);
    }

    @Override
    public boolean offer(@Nonnull E element) {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            this.queue.offer(element);
            if (this.queue.peek() == element) {
                leader = null;
                available.signal();
            }
            return true;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void put(@Nonnull E element) throws InterruptedException {
        offer(element);
    }

    @Override
    public boolean offer(@Nonnull E element, long timeout, @Nonnull TimeUnit unit) throws InterruptedException {
        return this.offer(element);
    }

    @Override
    public E poll() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            E first = this.queue.peek();
            if (first == null || first.getDelay(NANOSECONDS) > 0) {
                return null;
            } else {
                return queue.poll();
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public E take() throws InterruptedException {
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            for (; ; ) {
                E first = this.queue.peek();
                if (first == null) {
                    available.await();
                } else {
                    long delay = first.getDelay(NANOSECONDS);
                    if (delay <= 0) {
                        return this.queue.poll();
                    }

                    first = null; // don't retain ref while waiting
                    if (leader != null) {
                        available.await();
                    } else {
                        Thread thisThread = Thread.currentThread();
                        leader = thisThread;
                        try {
                            long ignored = available.awaitNanos(delay);
                        } finally {
                            if (leader == thisThread) {
                                leader = null;
                            }

                        }
                    }
                }
            }
        } finally {
            if (leader == null && this.queue.peek() != null) {
                available.signal();
            }
            lock.unlock();
        }
    }

    public Collection<E> take(int size) throws InterruptedException {
        List<E> takes = new ArrayList<>(size);
        while (takes.size() < size) {
            takes.add(this.take());
        }
        return takes;
    }

    @Override
    public E poll(long timeout, TimeUnit unit) throws InterruptedException {
        long nanos = unit.toNanos(timeout);
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            for (; ; ) {
                E first = this.queue.peek();
                if (first == null) {
                    if (nanos <= 0) {
                        return null;
                    } else {
                        nanos = available.awaitNanos(nanos);
                    }

                } else {
                    long delay = first.getDelay(NANOSECONDS);
                    if (delay <= 0) {
                        return this.queue.poll();
                    }
                    if (nanos <= 0) {
                        return null;
                    }
                    first = null; // don't retain ref while waiting
                    if (nanos < delay || leader != null) {
                        nanos = available.awaitNanos(nanos);
                    } else {
                        Thread thisThread = Thread.currentThread();
                        leader = thisThread;
                        try {
                            long timeLeft = available.awaitNanos(delay);
                            nanos -= delay - timeLeft;
                        } finally {
                            if (leader == thisThread) {
                                leader = null;
                            }
                        }
                    }
                }
            }
        } finally {
            if (leader == null && this.queue.peek() != null) {
                available.signal();
            }
            lock.unlock();
        }
    }

    public List<E> poll(int maxElements, long timeout, TimeUnit unit) throws InterruptedException {
        List<E> polls = new ArrayList<>(maxElements);
        long nanos = unit.toNanos(timeout);
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();

        try {
            while (polls.size() < maxElements) {
                E first = this.queue.peek();
                if (first == null) {
                    if (nanos <= 0) {
                        // 等待时间到了，就返回 polled 的数据
                        return polls;
                    } else {
                        // 等待开发者指定的时间
                        nanos = available.awaitNanos(nanos);
                    }
                } else {
                    long delay = first.getDelay(NANOSECONDS);
                    if (delay <= 0) {
                        // 该元素已经到期了，可以加入 polled 数组
                        polls.add(this.queue.poll());
                    }
                    if (nanos <= 0) {
                        // 等待时间到了，就返回 polled 的数据
                        return polls;
                    }

                    first = null; // don't retain ref while waiting


                    if (nanos < delay || leader != null) {
                        nanos = available.awaitNanos(nanos);
                    } else {
                        Thread thisThread = Thread.currentThread();
                        leader = thisThread;
                        try {
                            // 等待新元素加入或第一个元素到期
                            long timeLeft = available.awaitNanos(delay);
                            // 扣除已等待时间
                            nanos -= delay - timeLeft;
                        } finally {
                            if (leader == thisThread) {
                                leader = null;
                            }
                        }
                    }
                }
            }

            // 元素已满
            return polls;
        } finally {
            if (leader == null && this.queue.peek() != null) {
                available.signal();
            }
            lock.unlock();
        }
    }

    @Override
    public E peek() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return this.queue.peek();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int size() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return this.queue.size();
        } finally {
            lock.unlock();
        }
    }

    private E peekExpired() {
        // assert lock.isHeldByCurrentThread();
        E first = this.queue.peek();
        return (first == null || first.getDelay(NANOSECONDS) > 0) ?
                null : first;
    }

    @Override
    public int drainTo(@Nonnull Collection<? super E> c) {
        Objects.requireNonNull(c);
        if (c == this) {
            throw new IllegalArgumentException();
        }

        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            int n = 0;
            for (E e; (e = peekExpired()) != null; ) {
                c.add(e);       // In this order, in case add() throws.
                this.queue.poll();
                ++n;
            }
            return n;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int drainTo(@Nonnull Collection<? super E> c, int maxElements) {
        Objects.requireNonNull(c);
        if (c == this) {
            throw new IllegalArgumentException();
        }

        if (maxElements <= 0) {
            return 0;
        }

        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            int n = 0;
            for (E e; n < maxElements && (e = peekExpired()) != null; ) {
                c.add(e);       // In this order, in case add() throws.
                this.queue.poll();
                ++n;
            }
            return n;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void clear() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            this.queue.clear();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int remainingCapacity() {
        return Integer.MAX_VALUE;
    }

    @Override
    public Object[] toArray() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return this.queue.toArray();
        } finally {
            lock.unlock();
        }
    }

    @Override
    @SuppressWarnings("SuspiciousToArrayCall")
    public <T> T[] toArray(@Nonnull T[] a) {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return this.queue.toArray(a);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean remove(Object o) {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return this.queue.remove(o);
        } finally {
            lock.unlock();
        }
    }

    void removeEQ(Object o) {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            for (Iterator<E> it = this.queue.iterator(); it.hasNext(); ) {
                if (o == it.next()) {
                    it.remove();
                    break;
                }
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Iterator<E> iterator() {
        return new Itr(toArray());
    }

    private class Itr implements Iterator<E> {
        final Object[] array; // Array of all elements
        int cursor;           // index of next element to return
        int lastRet;          // index of last element, or -1 if no such

        Itr(Object[] array) {
            lastRet = -1;
            this.array = array;
        }

        @Override
        public boolean hasNext() {
            return cursor < array.length;
        }

        @Override
        public E next() {
            if (cursor >= array.length) {
                throw new NoSuchElementException();
            }

            lastRet = cursor;
            return (E) array[cursor++];
        }

        @Override
        public void remove() {
            if (lastRet < 0) {
                throw new IllegalStateException();
            }

            removeEQ(array[lastRet]);
            lastRet = -1;
        }
    }
}