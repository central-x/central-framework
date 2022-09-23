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

import central.util.Guidx;
import lombok.experimental.Delegate;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
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

    @Delegate
    private BlockingQueue<E> getQueue() {
        return this.queue;
    }

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
}
