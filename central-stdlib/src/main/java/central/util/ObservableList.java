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

import java.util.*;

/**
 * Observable List
 * <p>
 * 当里面的元数发生变动时，会向观察者发出通知
 *
 * @author Alan Yeh
 * @since 2022/11/11
 */
public class ObservableList<E> extends Observable<ObservableList<E>> implements List<E> {

    /**
     * 元素被添加事件
     */
    @Getter
    @RequiredArgsConstructor
    public static class ElementAdded<E> implements ObserveEvent<ObservableList<E>> {
        private final ObservableList<E> observable;

        private final List<? extends E> elements;

        protected static <E> ElementAdded<E> of(ObservableList<E> observable, List<? extends E> elements) {
            return new ElementAdded<>(observable, elements);
        }

        protected static <E> ElementAdded<E> of(ObservableList<E> observable, E element) {
            return new ElementAdded<>(observable, Collections.singletonList(element));
        }
    }

    /**
     * 元素被移除事件
     */
    @Getter
    @RequiredArgsConstructor
    public static class ElementRemoved<E> implements ObserveEvent<ObservableList<E>> {
        private final ObservableList<E> observable;

        private final List<Object> elements;

        protected static <E> ElementRemoved<E> of(ObservableList<E> observable, List<Object> elements) {
            return new ElementRemoved<>(observable, elements);
        }

        protected static <E> ElementRemoved<E> of(ObservableList<E> observable, Object element) {
            return new ElementRemoved<>(observable, Collections.singletonList(element));
        }
    }

    private final List<E> data;

    public ObservableList(List<E> data) {
        this.data = data;
    }

    public ObservableList() {
        this(new ArrayList<>());
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
    public boolean contains(Object o) {
        return this.data.contains(o);
    }

    @NotNull
    @Override
    public Iterator<E> iterator() {
        final var iterator = this.data.iterator();
        return new Iterator<E>() {
            private E element = null;

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public E next() {
                this.element = iterator.next();
                return this.element;
            }

            @Override
            public void remove() {
                try {
                    iterator.remove();
                } finally {
                    notifyObservers(ElementRemoved.of(ObservableList.this, this.element));
                    this.element = null;
                }
            }
        };
    }

    @NotNull
    @Override
    public Object[] toArray() {
        return this.data.toArray();
    }

    @NotNull
    @Override
    public <T> T[] toArray(@NotNull T[] a) {
        return this.data.toArray(a);
    }

    @Override
    public boolean add(E e) {
        try {
            return this.data.add(e);
        } finally {
            this.notifyObservers(ElementAdded.of(this, e));
        }
    }

    @Override
    public boolean remove(Object o) {
        var removed = this.data.remove(o);
        if (removed) {
            this.notifyObservers(ElementRemoved.of(this, o));
        }
        return removed;
    }

    @Override
    @SuppressWarnings("SlowListContainsAll")
    public boolean containsAll(@NotNull Collection<?> c) {
        return this.data.containsAll(c);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends E> c) {
        try {
            return this.data.addAll(c);
        } finally {
            this.notifyObservers(ElementAdded.of(this, new ArrayList<>(c)));
        }
    }

    @Override
    public boolean addAll(int index, @NotNull Collection<? extends E> c) {
        try {
            return this.data.addAll(index, c);
        } finally {
            this.notifyObservers(ElementAdded.of(this, new ArrayList<>(c)));
        }
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        try {
            return this.data.removeAll(c);
        } finally {
            this.notifyObservers(ElementRemoved.of(this, new ArrayList<>(c)));
        }
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        var removed = new ArrayList<Object>(this.data);

        var retained = this.data.retainAll(c);
        if (retained) {
            removed.removeAll(this.data);
            this.notifyObservers(ElementRemoved.of(this, removed));
        }
        return retained;
    }

    @Override
    public void clear() {
        var removed = new ArrayList<Object>(this.data);
        this.data.clear();
        if (Listx.isNotEmpty(removed)){
            this.notifyObservers(ElementRemoved.of(this, removed));
        }
    }

    @Override
    public E get(int index) {
        return this.data.get(index);
    }

    @Override
    public E set(int index, E element) {
        try {
            var removed = this.data.set(index, element);
            if (removed != null) {
                this.notifyObservers(ElementRemoved.of(this, removed));
            }
            return removed;
        } finally {
            this.notifyObservers(ElementAdded.of(this, element));
        }
    }

    @Override
    public void add(int index, E element) {
        this.data.add(index, element);
        this.notifyObservers(ElementAdded.of(this, element));
    }

    @Override
    public E remove(int index) {
        var removed = this.data.remove(index);
        if (removed != null) {
            this.notifyObservers(ElementRemoved.of(this, removed));
        }
        return removed;
    }

    @Override
    public int indexOf(Object o) {
        return this.data.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return this.data.lastIndexOf(o);
    }

    @NotNull
    @Override
    public ListIterator<E> listIterator() {
        return this.listIterator(0);
    }

    @NotNull
    @Override
    public ListIterator<E> listIterator(int index) {
        final var iterator = this.data.listIterator(index);
        return new ListIterator<E>() {
            private E element = null;

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public E next() {
                element = iterator.next();
                return element;
            }

            @Override
            public boolean hasPrevious() {
                return iterator.hasPrevious();
            }

            @Override
            public E previous() {
                return iterator.previous();
            }

            @Override
            public int nextIndex() {
                return iterator.nextIndex();
            }

            @Override
            public int previousIndex() {
                return iterator.previousIndex();
            }

            @Override
            public void remove() {
                iterator.remove();
                ObservableList.this.notifyObservers(ElementRemoved.of(ObservableList.this, element));
            }

            @Override
            public void set(E e) {
                iterator.set(e);
                ObservableList.this.notifyObservers(ElementAdded.of(ObservableList.this, e));
                ObservableList.this.notifyObservers(ElementRemoved.of(ObservableList.this, element));
            }

            @Override
            public void add(E e) {
                iterator.add(e);
                ObservableList.this.notifyObservers(ElementAdded.of(ObservableList.this, e));
            }
        };
    }

    @NotNull
    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return this.data.subList(fromIndex, toIndex);
    }
}
