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

package central.bean;

import central.util.Collectionx;
import central.util.Mapx;
import central.util.Objectx;
import central.lang.Stringx;
import lombok.SneakyThrows;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Coder value pairs
 *
 * @author Alan Yeh
 * @since 2022/07/05
 */
public class CodeValues<V extends Serializable> implements Collection<CodeValue<V>>, Serializable {
    @Serial
    private static final long serialVersionUID = 5058361389934247060L;

    private final List<CodeValue<V>> internal;

    public CodeValues() {
        this.internal = new ArrayList<>();
    }

    public CodeValues(Map<String, V> init) {
        this.internal = new ArrayList<>(init.size());
        this.internal.addAll(init.entrySet().stream().map(it -> new CodeValue<>(it.getKey(), it.getValue())).collect(Collectors.toSet()));
    }

    /**
     * 设置键值
     *
     * @param code  键
     * @param value 值
     */
    public void put(String code, V value) {
        var data = this.internal.stream().filter(it -> code.equals(it.getCode())).findFirst();
        if (data.isPresent()) {
            data.get().setValue(value);
        } else {
            this.internal.add(new CodeValue<>(code, value));
        }
    }

    /**
     * 如果值不存在，则设置此值
     * 如果值存在，则使用原来的值
     *
     * @param key   键
     * @param value 值
     */
    public V putIfAbsent(String key, V value) {
        var origin = this.internal.stream().filter(it -> it.getCode().equals(key)).findFirst();
        if (origin.isPresent()) {
            var originValue = origin.get().getValue();
            origin.get().setValue(value);
            return originValue;
        } else {
            this.internal.add(new CodeValue<>(key, value));
            return null;
        }
    }

    /**
     * 是否包含指定的键
     *
     * @param code 键
     */
    public boolean containsCode(String code) {
        if (Stringx.isNullOrBlank(code)) {
            return false;
        }

        for (var item : this.internal) {
            if (item.getCode().equals(code)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取值
     *
     * @param code 键
     */
    public V get(String code) {
        for (var item : this.internal) {
            if (item.getCode().equals(code)) {
                return item.getValue();
            }
        }
        return null;
    }

    /**
     * 获取值
     * 如果不存在指定的值，则从 mappingFunction 中计算获取
     *
     * @param code            键
     * @param mappingFunction 值计算器
     */
    public V computeIfAbsent(String code, Function<String, V> mappingFunction) {
        Objects.requireNonNull(mappingFunction);

        var origin = this.internal.stream().filter(it -> it.getCode().equals(code)).findFirst();
        if (origin.isPresent()) {
            return origin.get().getValue();
        }
        var value = mappingFunction.apply(code);
        this.internal.add(new CodeValue<>(code, value));
        return value;
    }


    /**
     * 获取值
     * 如果不存在指定值，则返回默认值
     *
     * @param key          键
     * @param defaultValue 默认值
     */
    public V getOrDefault(String key, V defaultValue) {
        return Objectx.getOrDefault(this.get(key), defaultValue);
    }

    /**
     * 循环
     */
    public void forEach(BiConsumer<String, V> action) {
        Objects.requireNonNull(action);
        this.forEach(item -> action.accept(item.getCode(), item.getValue()));
    }


    /**
     * 移除指定键
     *
     * @param code 键
     * @return 被移除的值
     */
    public V remove(String code) {
        var origin = this.internal.stream().filter(it -> it.getCode().equals(code)).findFirst();
        if (origin.isPresent()) {
            this.remove(origin.get());
            return origin.get().getValue();
        } else {
            return null;
        }
    }

    /**
     * 设置 map 中所有的键值
     *
     * @param map 键值对
     */
    public void putAll(Map<String, V> map) {
        if (Mapx.isNullOrEmpty(map)) {
            return;
        }
        for (Map.Entry<String, V> item : map.entrySet()) {
            this.add(new CodeValue<>(item.getKey(), item.getValue()));
        }
    }

    /**
     * 获取所有键
     */
    public Set<String> codeSet() {
        return this.internal.stream().map(CodeValue::getCode).collect(Collectors.toSet());
    }

    /**
     * 获取键值对
     */
    public Set<CodeValue<V>> entrySet() {
        return new HashSet<>(this.internal);
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
    public boolean contains(Object o) {
        return this.internal.contains(o);
    }

    @Override
    public Iterator<CodeValue<V>> iterator() {
        return this.internal.iterator();
    }

    @Override
    public Object[] toArray() {
        return this.internal.toArray();
    }

    @Override
    @SuppressWarnings("SuspiciousToArrayCall")
    public <T> T[] toArray(@Nonnull T[] a) {
        return this.internal.toArray(a);
    }

    @Override
    public boolean add(CodeValue<V> item) {
        if (item == null) {
            throw new IllegalArgumentException("参数不允许为空");
        }
        if (Stringx.isNullOrBlank(item.getCode())) {
            throw new IllegalArgumentException("不允许使用为 null 或空字符串作为 Key 值");
        }
        var origin = this.internal.stream().filter(it -> it.getCode().equals(item.getCode())).findFirst();
        if (origin.isPresent()) {
            origin.get().setValue(item.getValue());
        } else {
            this.internal.add(new CodeValue<>(item.getCode(), item.getValue()));
        }
        return true;
    }

    @Override
    public boolean remove(Object o) {
        return this.internal.remove(o);
    }

    @Override
    @SuppressWarnings("SlowListContainsAll")
    public boolean containsAll(@Nonnull Collection<?> collection) {
        return this.internal.containsAll(collection);
    }

    @Override
    public boolean addAll(@Nonnull Collection<? extends CodeValue<V>> collection) {
        if (Collectionx.isNullOrEmpty(collection)) {
            return true;
        }
        for (var item : collection) {
            this.add(item);
        }
        return true;
    }

    @Override
    public boolean removeAll(@Nonnull Collection<?> collection) {
        return this.internal.removeAll(collection);
    }

    @Override
    public boolean retainAll(@Nonnull Collection<?> collection) {
        return this.internal.retainAll(collection);
    }

    @Override
    public void clear() {
        this.internal.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CodeValues<?> that = (CodeValues<?>) o;
        return Objects.equals(internal, that.internal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(internal);
    }

    @Override
    @SneakyThrows(CloneNotSupportedException.class)
    protected Object clone() {
        var clone = (CodeValues<V>) super.clone();
        this.forEach(clone::put);
        return clone;
    }
}
