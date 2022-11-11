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
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Observable Map
 * <p>
 * 当里面的元素发生变动时，会向观察者发出通知
 * <p>
 * 注意:
 * <p>
 * {@link  ConcurrentHashMap } 或类似这些的数据容器，由于重写了 {@link Map} 里面的 default 方法，因此可能导致通知失效
 *
 * @author Alan Yeh
 * @since 2022/11/11
 */
public class ObservableMap<K, V> extends Observable<ObservableMap<K, V>> implements Map<K, V> {

    /**
     * 元素被添加事件
     *
     * @param <K> 键类型
     * @param <V> 值类型
     */
    @Getter
    @RequiredArgsConstructor
    public static class EntryAdded<K, V> implements ObserveEvent<ObservableMap<K, V>> {

        private final ObservableMap<K, V> observable;

        private final List<Map.Entry<K, V>> entries;

        public static <K, V> EntryAdded<K, V> of(ObservableMap<K, V> observable, Collection<Map.Entry<K, V>> entries) {
            return new EntryAdded<>(observable, new ArrayList<>(entries));
        }

        public static <K, V> EntryAdded<K, V> of(ObservableMap<K, V> observable, Map.Entry<K, V> entry) {
            return new EntryAdded<>(observable, Collections.singletonList(entry));
        }
    }

    /**
     * 元素被删除事件
     *
     * @param <K> 键类型
     * @param <V> 值类型
     */
    @Getter
    @RequiredArgsConstructor
    public static class EntryRemoved<K, V> implements ObserveEvent<ObservableMap<K, V>> {
        private final ObservableMap<K, V> observable;

        private final List<Map.Entry<Object, Object>> entries;

        public static <K, V> EntryRemoved<K, V> of(ObservableMap<K, V> observable, Collection<Map.Entry<Object, Object>> entries) {
            return new EntryRemoved<>(observable, new ArrayList<>(entries));
        }

        public static <K, V> EntryRemoved<K, V> of(ObservableMap<K, V> observable, Map.Entry<Object, Object> entry) {
            return new EntryRemoved<>(observable, Collections.singletonList(entry));
        }
    }


    private final Map<K, V> data;

    public ObservableMap(Map<K, V> data) {
        this.data = data;
    }

    public ObservableMap() {
        this(new HashMap<>());
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
        var removed = this.data.put(key, value);
        this.notifyObservers(EntryAdded.of(this, Map.entry(key, value)));
        if (removed != null && removed != value) {
            this.notifyObservers(EntryRemoved.of(this, Map.entry(key, removed)));
        }
        return removed;
    }

    @Override
    public V remove(Object key) {
        var removed = this.data.remove(key);
        if (removed != null) {
            this.notifyObservers(EntryRemoved.of(this, Map.entry(key, removed)));
        }
        return removed;
    }

    @Override
    public void putAll(@NotNull Map<? extends K, ? extends V> m) {
        for (var entry : m.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        var removed = new HashMap<Object, Object>(this.data);
        this.data.clear();
        if (Mapx.isNotEmpty(removed)) {
            this.notifyObservers(EntryRemoved.of(this, removed.entrySet()));
        }
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
