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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Closeable;
import java.io.IOException;
import java.time.Duration;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 元素过期 Map
 *
 * @author Alan Yeh
 * @since 2022/11/09
 */
public class ExpiredMap<K, V extends Expired> implements Map<K, V>, Closeable {
    private final Map<K, V> data = new ConcurrentHashMap<>();

    private final ConsumableQueue<DelayedElement<K>, DelayedQueue<DelayedElement<K>>> values = new ConsumableQueue<>(new DelayedQueue<>());

    public ExpiredMap() {
        values.addConsumer(queue -> {
            try {
                while (true) {
                    var delayed = queue.take();
                    data.compute(delayed.getElement(), (key, value) -> {
                        if (value == null) {
                            return null;
                        }
                        var expires = value.getExpire(TimeUnit.MILLISECONDS);
                        if (expires <= 0) {
                            return null;
                        } else {
                            // 重新添加到队列里，等待下次检查元素过期
                            queue.offer(new DelayedElement<>(key, Duration.ofMillis(expires)));
                            return value;
                        }
                    });
                }
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        });
    }

    @Override
    public void close() throws IOException {
        this.values.close();
    }

    @Override
    public int size() {
        return this.data.size();
    }

    @Override
    public boolean isEmpty() {
        return this.data.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.data.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.data.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return this.data.get(key);
    }

    @Nullable
    @Override
    public V put(K key, V value) {
        this.values.offer(new DelayedElement<>(key, Duration.ofMillis(value.getExpire(TimeUnit.MILLISECONDS))));
        return this.data.put(key, value);
    }

    @Override
    public V remove(Object key) {
        return this.data.remove(key);
    }

    @Override
    public void putAll(@NotNull Map<? extends K, ? extends V> m) {
        for (var entry : m.entrySet()) {
            this.values.offer(new DelayedElement<>(entry.getKey(), Duration.ofMillis(entry.getValue().getExpire(TimeUnit.MILLISECONDS))));
        }
        this.data.putAll(m);
    }

    @Override
    public void clear() {
        this.data.clear();
    }

    @NotNull
    @Override
    public Set<K> keySet() {
        return this.data.keySet();
    }

    @NotNull
    @Override
    public Collection<V> values() {
        return this.data.values();
    }

    @NotNull
    @Override
    public Set<Entry<K, V>> entrySet() {
        return this.data.entrySet();
    }
}
