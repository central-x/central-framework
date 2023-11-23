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

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Collection 工具
 *
 * @author Alan Yeh
 * @since 2022/07/12
 */
@UtilityClass
public class Collectionx {
    /**
     * 判断集合是否为空
     *
     * @param collection 集合
     * @param <T>        元素类型
     */
    public static <T> boolean isNullOrEmpty(@Nullable Collection<T> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * 判断集合是否不为空
     *
     * @param collection 集合
     * @param <T>        元素类型
     */
    public static <T> boolean isNotEmpty(@Nullable Collection<T> collection) {
        return collection != null && !collection.isEmpty();
    }

    /**
     * 获取第一个元素
     *
     * @param collection 集合
     * @param <T>        元素类型
     */
    public static <T> @Nonnull Optional<T> getFirst(@Nullable Collection<T> collection) {
        return Optional.ofNullable(getFirstOrNull(collection));
    }

    /**
     * 获取第一个元素
     *
     * @param collection 集合
     * @param <T>        元素类型
     */
    public static <T> @Nullable T getFirstOrNull(@Nullable Collection<T> collection) {
        return isNullOrEmpty(collection) ? null : collection.iterator().next();
    }

    /**
     * 创建一个空 Map
     */
    public static <K, V> Map<K, V> emptyMap() {
        return (Map<K, V>) EMPTY_MAP;
    }

    /**
     * 创建一个空 Set
     */
    public static <V> Set<V> emptySet() {
        return (Set<V>) EMPTY_SET;
    }

    /**
     * 创建一个空 List
     */
    public static <V> List<V> emptyList() {
        return (List<V>) EMPTY_LIST;
    }

    @SuppressWarnings("rawtypes")
    private static final Map EMPTY_MAP = new EmptyMap();

    @SuppressWarnings("rawtypes")
    private static final Set EMPTY_SET = new EmptySet();

    @SuppressWarnings("rawtypes")
    private static final List EMPTY_LIST = new EmptyList();

    private static class EmptyMap<K, V> implements Map<K, V> {

        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public boolean containsKey(Object key) {
            return false;
        }

        @Override
        public boolean containsValue(Object value) {
            return false;
        }

        @Override
        public V get(Object key) {
            return null;
        }

        @Override
        public V put(K key, V value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public V remove(Object key) {
            return null;
        }

        @Override
        public void putAll(@NotNull Map<? extends K, ? extends V> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
        }

        @NotNull
        @Override
        public Set<K> keySet() {
            return Collections.emptySet();
        }

        @NotNull
        @Override
        public Collection<V> values() {
            return Collections.emptyList();
        }

        @NotNull
        @Override
        public Set<Entry<K, V>> entrySet() {
            return Collections.emptySet();
        }
    }

    private static class EmptySet<V> implements Set<V> {

        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public boolean contains(Object o) {
            return false;
        }

        @NotNull
        @Override
        public Iterator<V> iterator() {
            return new Iterator<V>() {
                @Override
                public boolean hasNext() {
                    return false;
                }

                @Override
                public V next() {
                    return null;
                }
            };
        }

        @NotNull
        @Override
        public Object[] toArray() {
            return new Object[0];
        }

        @NotNull
        @Override
        public <T> T[] toArray(@NotNull T[] a) {
            return a;
        }

        @Override
        public boolean add(V v) {
            return false;
        }

        @Override
        public boolean remove(Object o) {
            return false;
        }

        @Override
        public boolean containsAll(@NotNull Collection<?> c) {
            return false;
        }

        @Override
        public boolean addAll(@NotNull Collection<? extends V> c) {
            return false;
        }

        @Override
        public boolean retainAll(@NotNull Collection<?> c) {
            return false;
        }

        @Override
        public boolean removeAll(@NotNull Collection<?> c) {
            return false;
        }

        @Override
        public void clear() {

        }
    }

    private static class EmptyList<V> implements List<V> {

        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public boolean contains(Object o) {
            return false;
        }

        @NotNull
        @Override
        public Iterator<V> iterator() {
            return new Iterator<V>() {
                @Override
                public boolean hasNext() {
                    return false;
                }

                @Override
                public V next() {
                    return null;
                }
            };
        }

        @NotNull
        @Override
        public Object[] toArray() {
            return new Object[0];
        }

        @NotNull
        @Override
        public <T> T[] toArray(@NotNull T[] a) {
            return a;
        }

        @Override
        public boolean add(V v) {
            return false;
        }

        @Override
        public boolean remove(Object o) {
            return false;
        }

        @Override
        public boolean containsAll(@NotNull Collection<?> c) {
            return false;
        }

        @Override
        public boolean addAll(@NotNull Collection<? extends V> c) {
            return false;
        }

        @Override
        public boolean addAll(int index, @NotNull Collection<? extends V> c) {
            return false;
        }

        @Override
        public boolean removeAll(@NotNull Collection<?> c) {
            return false;
        }

        @Override
        public boolean retainAll(@NotNull Collection<?> c) {
            return false;
        }

        @Override
        public void clear() {

        }

        @Override
        public V get(int index) {
            return null;
        }

        @Override
        public V set(int index, V element) {
            return null;
        }

        @Override
        public void add(int index, V element) {

        }

        @Override
        public V remove(int index) {
            return null;
        }

        @Override
        public int indexOf(Object o) {
            return 0;
        }

        @Override
        public int lastIndexOf(Object o) {
            return 0;
        }

        private final ListIterator<V> emptyListIterator = new ListIterator<V>() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public V next() {
                return null;
            }

            @Override
            public boolean hasPrevious() {
                return false;
            }

            @Override
            public V previous() {
                return null;
            }

            @Override
            public int nextIndex() {
                return 0;
            }

            @Override
            public int previousIndex() {
                return 0;
            }

            @Override
            public void remove() {

            }

            @Override
            public void set(V v) {

            }

            @Override
            public void add(V v) {

            }
        };

        @NotNull
        @Override
        public ListIterator<V> listIterator() {
            return this.emptyListIterator;
        }

        @NotNull
        @Override
        public ListIterator<V> listIterator(int index) {
            return this.emptyListIterator;
        }

        @NotNull
        @Override
        public List<V> subList(int fromIndex, int toIndex) {
            throw new IndexOutOfBoundsException(fromIndex);
        }
    }
}
