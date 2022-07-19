package central.util.concurrent;

import central.util.Guidx;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import javax.annotation.Nonnull;
import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * 可消费的队列
 * 用于快速创建队列消费线程
 *
 * @author Alan Yeh
 * @since 2022/07/14
 */
public class ConsumableQueue<E, Queue extends BlockingQueue<E>> implements BlockingQueue<E>, Closeable {

    private final Queue queue;

    private ExecutorService executor;
    private final Map<String, Future<?>> runners = new HashMap<>();

    public ConsumableQueue(Queue queue) {
        this.queue = queue;
        this.executor = Executors.newCachedThreadPool(new CustomizableThreadFactory(this.getClass().getSimpleName() + "@" + this.hashCode() + "-"));
    }

    public ConsumableQueue(Queue queue, ExecutorService executor) {
        this.queue = queue;
        this.executor = executor;
    }

    public ConsumableQueue(Queue queue, String threadPrefix) {
        this.queue = queue;
        this.executor = Executors.newCachedThreadPool(new CustomizableThreadFactory(threadPrefix + "-"));
    }

    @Override
    public void close() throws IOException {
        if (this.executor != null) {
            this.executor.shutdownNow();
            this.executor = null;
        }
    }

    /**
     * 添加消费者
     * 已添加的消费者会自动加入到异步线程池进行消费
     *
     * @param consumer 队列消费者
     * @return 消费者标识，移除消费者需要使用此标识
     */
    public String addConsumer(Consumer<Queue> consumer) {
        Future<?> future = this.executor.submit(() -> {
            if (consumer instanceof InitializingBean) {
                try {
                    ((InitializingBean) consumer).afterPropertiesSet();
                } catch (Exception ex) {
                    throw new BeanInitializationException("Consumer 初始化失败: " + ex.getLocalizedMessage(), ex);
                }
            }
            consumer.accept(this.queue);
        });

        String key = Guidx.nextID();
        this.runners.put(key, future);
        return key;
    }

    /**
     * 使用指定的标识添加消费者
     * 已添加的消费者会自动加入到异步线程池进行消费
     *
     * @param key      标费者标识
     * @param consumer 队列消费者
     */
    public void addConsumer(String key, Consumer<Queue> consumer) {
        Future<?> future = this.executor.submit(() -> {
            if (consumer instanceof InitializingBean) {
                try {
                    ((InitializingBean) consumer).afterPropertiesSet();
                } catch (Exception ex) {
                    throw new BeanInitializationException("Consumer 初始化失败: " + ex.getLocalizedMessage(), ex);
                }
            }
            consumer.accept(this.queue);
        });

        this.runners.put(key, future);
    }

    /**
     * 移除消费者
     *
     * @param key 消费者标识
     */
    public void removeConsumer(String key) {
        Future<?> future = this.runners.remove(key);
        future.cancel(true);
    }

    // =================================================================================================================
    @Override
    public boolean add(@Nonnull E element) {
        return this.queue.add(element);
    }

    @Override
    public boolean offer(@Nonnull E element) {
        return this.queue.offer(element);
    }

    @Override
    public E remove() {
        return this.queue.remove();
    }

    @Override
    public E poll() {
        return this.queue.poll();
    }

    @Override
    public E element() {
        return this.queue.element();
    }

    @Override
    public E peek() {
        return this.queue.peek();
    }

    @Override
    public void put(@Nonnull E element) throws InterruptedException {
        this.queue.put(element);
    }

    @Override
    public boolean offer(@Nonnull E element, long timeout, @Nonnull TimeUnit unit) throws InterruptedException {
        return this.queue.offer(element, timeout, unit);
    }

    @Override
    public E take() throws InterruptedException {
        return this.queue.take();
    }

    @Override
    public E poll(long timeout, @Nonnull TimeUnit unit) throws InterruptedException {
        return this.queue.poll(timeout, unit);
    }

    @Override
    public int remainingCapacity() {
        return this.queue.remainingCapacity();
    }

    @Override
    public boolean remove(Object o) {
        return this.queue.remove(o);
    }

    @Override
    public boolean containsAll(@Nonnull Collection<?> c) {
        return this.queue.containsAll(c);
    }

    @Override
    public boolean addAll(@Nonnull Collection<? extends E> c) {
        return this.queue.addAll(c);
    }

    @Override
    public boolean removeAll(@Nonnull Collection<?> c) {
        return this.queue.removeAll(c);
    }

    @Override
    public boolean retainAll(@Nonnull Collection<?> c) {
        return this.queue.retainAll(c);
    }

    @Override
    public void clear() {
        this.queue.clear();
    }

    @Override
    public int size() {
        return this.queue.size();
    }

    @Override
    public boolean isEmpty() {
        return this.queue.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return this.queue.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return this.queue.iterator();
    }

    @Override
    public Object[] toArray() {
        return this.queue.toArray();
    }

    @Override
    public <T> T[] toArray(@Nonnull T[] a) {
        return this.queue.toArray(a);
    }

    @Override
    public int drainTo(@Nonnull Collection<? super E> c) {
        return this.queue.drainTo(c);
    }

    @Override
    public int drainTo(@Nonnull Collection<? super E> c, int maxElements) {
        return this.queue.drainTo(c, maxElements);
    }
}
