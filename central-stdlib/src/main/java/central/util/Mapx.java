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

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Stream;

/**
 * Map 工具
 *
 * @author Alan Yeh
 * @since 2022/07/12
 */
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
     * 获取指定
     *
     * @param map 集合
     * @param key 键
     * @param <K> 键类型
     * @param <V> 值类型
     */
    public static <K, V> V get(@Nullable Map<K, V> map, K key) {
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
     * 将 Map 转换大小写不敏感的 Map
     *
     * @param map 原 Map
     * @param <V> 值类型
     */
    public static <V> Map<String, V> caseInsensitiveMap(Map<String, V> map) {
        return new CaseInsensitiveMap<>(map);
    }

    private static class CaseInsensitiveMap<V> implements Map<String, V> {
        private final Map<String, V> internal;

        public CaseInsensitiveMap(Map<String, V> source) {
            this.internal = source;
        }

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
        public Set<String> keySet() {
            return this.internal.keySet();
        }

        @Override
        public Collection<V> values() {
            return this.internal.values();
        }

        @Override
        public Set<Entry<String, V>> entrySet() {
            return this.internal.entrySet();
        }
    }
}
