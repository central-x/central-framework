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

import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Stream;

/**
 * Map 工具
 *
 * @author Alan Yeh
 * @since 2022/07/12
 */
@UtilityClass
public class Mapx {

    /**
     * 将可空集合变成不可空集合
     *
     * @param map 集合
     * @param <K> 键类型
     * @param <V> 值类型
     */
    public static <K, V> Map<K, V> nullSafe(@Nullable Map<K, V> map) {
        return map == null ? newHashMap() : map;
    }

    /**
     * 判断集合是否为空
     *
     * @param map 集合
     * @param <K> 键类型
     * @param <V> 值类型
     */
    public static <K, V> boolean isNullOrEmpty(@Nullable Map<K, V> map) {
        return map == null || map.isEmpty();
    }

    /**
     * 判断集合是否不为空
     *
     * @param map 集合
     * @param <K> 键类型
     * @param <V> 值类型
     */
    public static <K, V> boolean isNotEmpty(@Nullable Map<K, V> map) {
        return map != null && !map.isEmpty();
    }

    /**
     * 转成 Stream
     *
     * @param map 集合
     * @param <K> 键类型
     * @param <V> 值类型
     */
    public static <K, V> Stream<Map.Entry<K, V>> asStream(@Nullable Map<K, V> map) {
        return isNullOrEmpty(map) ? Stream.empty() : map.entrySet().stream();
    }

    /**
     * 获取指定键的值
     *
     * @param map 集合
     * @param key 键
     * @param <K> 键类型
     * @param <V> 值类型
     */
    public static <K, V> @Nonnull Optional<V> get(@Nullable Map<K, V> map, K key) {
        return Optional.ofNullable(getOrNull(map, key));
    }

    /**
     * 获取指定键的值
     *
     * @param map 集合
     * @param key 键
     * @param <K> 键类型
     * @param <V> 值类型
     */
    public static <K, V> V getOrNull(@Nullable Map<K, V> map, K key) {
        return isNullOrEmpty(map) ? null : map.get(key);
    }

    /**
     * 快速创建 HashMap
     *
     * @param <K> 键类型
     * @param <V> 值类型
     */
    public static <K, V> Map<K, V> newHashMap() {
        return new HashMap<>();
    }

    /**
     * 快速创建键值对
     *
     * @param key   键
     * @param value 值
     * @param <K>   键类型
     * @param <V>   值类型
     */
    public static <K, V> Map<K, V> newHashMap(K key, V value) {
        var map = new HashMap<K, V>();
        map.put(key, value);
        return map;
    }

    /**
     * 快速创建 LinkedHashMap
     *
     * @param <K> 键类型
     * @param <V> 值类型
     */
    public static <K, V> Map<K, V> newLinkedHashMap() {
        return new LinkedHashMap<>();
    }

    /**
     * 快速创建 LinkedHashMap
     *
     * @param key   键
     * @param value 值
     * @param <K>   键类型
     * @param <V>   值类型
     */
    public static <K, V> Map<K, V> newLinkedHashMap(K key, V value) {
        var map = new HashMap<K, V>();
        map.put(key, value);
        return map;
    }

    /**
     * 将 Map 转换线程安全类
     *
     * @param unsafe 非线程安全集合
     * @return 线程安全集合
     */
    public static <K, V> Map<K, V> threadSafe(Map<K, V> unsafe) {
        return new ThreadSafeMap<>(unsafe);
    }

    @RequiredArgsConstructor
    private static class ThreadSafeMap<K, V> implements Map<K, V> {
        private final Map<K, V> internal;

        private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

        private final Lock readLock = lock.readLock();

        private final Lock writeLock = lock.writeLock();

        @Override
        public int size() {
            try {
                readLock.lock();
                return this.internal.size();
            } finally {
                readLock.unlock();
            }
        }

        @Override
        public boolean isEmpty() {
            try {
                readLock.lock();
                return this.internal.isEmpty();
            } finally {
                readLock.unlock();
            }
        }

        @Override
        public boolean containsKey(Object key) {
            try {
                readLock.lock();
                return this.internal.containsKey(key);
            } finally {
                readLock.unlock();
            }
        }

        @Override
        public boolean containsValue(Object value) {
            try {
                readLock.lock();
                return this.internal.containsValue(value);
            } finally {
                readLock.unlock();
            }
        }

        @Override
        public V get(Object key) {
            try {
                readLock.lock();
                return this.internal.get(key);
            } finally {
                readLock.unlock();
            }
        }

        @Override
        public V put(K key, V value) {
            try {
                writeLock.lock();
                return this.internal.put(key, value);
            } finally {
                writeLock.unlock();
            }
        }

        @Override
        public V remove(Object key) {
            try {
                writeLock.lock();
                return this.internal.remove(key);
            } finally {
                writeLock.unlock();
            }
        }

        @Override
        public void putAll(@Nonnull Map<? extends K, ? extends V> m) {
            try {
                writeLock.lock();
                this.internal.putAll(m);
            } finally {
                writeLock.unlock();
            }
        }

        @Override
        public void clear() {
            try {
                writeLock.lock();
                this.internal.clear();
            } finally {
                writeLock.unlock();
            }
        }

        @Override
        public @Nonnull Set<K> keySet() {
            try {
                readLock.lock();
                return this.internal.keySet();
            } finally {
                readLock.unlock();
            }
        }

        @Override
        public @Nonnull Collection<V> values() {
            try {
                readLock.lock();
                return this.internal.values();
            } finally {
                readLock.unlock();
            }
        }

        @Override
        public @Nonnull Set<Entry<K, V>> entrySet() {
            try {
                readLock.lock();
                return this.internal.entrySet();
            } finally {
                readLock.unlock();
            }
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof ThreadSafeMap<?, ?> safe) {
                return this.internal.equals(safe.internal);
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return this.internal.hashCode();
        }
    }

    /**
     * 将 Map 转换大小写不敏感的 Map
     *
     * @param map 原 Map
     * @param <V> 值类型
     */
    public static <V> Map<String, V> caseInsensitive(Map<String, V> map) {
        return new CaseInsensitiveMap<>(map);
    }

    @RequiredArgsConstructor
    private static class CaseInsensitiveMap<V> implements Map<String, V> {
        private final Map<String, V> internal;

        @Override
        public int size() {
            return this.internal.size();
        }

        @Override
        public boolean isEmpty() {
            return this.internal.isEmpty();
        }

        @Override
        public boolean containsKey(Object key) {
            if (key instanceof String s) {
                return this.internal.containsKey(s.toLowerCase());
            }
            return false;
        }

        @Override
        public boolean containsValue(Object value) {
            return this.internal.containsValue(value);
        }

        @Override
        public V get(Object key) {
            if (key instanceof String s) {
                return this.internal.get(s.toLowerCase());
            }
            return null;
        }

        @Override
        public V put(String key, V value) {
            return this.internal.put(key.toLowerCase(), value);
        }

        @Override
        public V remove(Object key) {
            if (key instanceof String s) {
                return this.internal.remove(s.toLowerCase());
            } else {
                return null;
            }
        }

        @Override
        public void putAll(Map<? extends String, ? extends V> m) {
            m.forEach(this::put);
        }

        @Override
        public void clear() {
            this.internal.clear();
        }

        @Override
        public @Nonnull Set<String> keySet() {
            return this.internal.keySet();
        }

        @Override
        public @Nonnull Collection<V> values() {
            return this.internal.values();
        }

        @Override
        public @Nonnull Set<Entry<String, V>> entrySet() {
            return this.internal.entrySet();
        }
    }
}
