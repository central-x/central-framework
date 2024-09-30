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

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 代表多个元素
 *
 * @author Alan Yeh
 * @since 2024/09/30
 */
public class Tuple implements Serializable {
    @Serial
    private static final long serialVersionUID = -6929401603851646119L;

    private final List<Object> values = new ArrayList<>();

    /**
     * 创建元组
     */
    public Tuple(Object... values) {
        this.values.addAll(Arrays.asList(values));
    }

    /**
     * 创建元组
     */
    public static Tuple of(Object... values) {
        return new Tuple(values);
    }

    /**
     * 创建元组
     */
    public static Tuple of(List<Object> values) {
        return new Tuple(values.toArray(new Object[0]));
    }

    /**
     * 获取第一个元素
     */
    @SuppressWarnings("unchecked")
    public <T> T getFirst() {
        return (T) values.get(0);
    }

    /**
     * 设置第一个元素
     */
    public void setFirst(Object value) {
        values.set(0, value);
    }

    /**
     * 获取第二个元素
     */
    @SuppressWarnings("unchecked")
    public <T> T getSecond() {
        return (T) values.get(1);
    }

    /**
     * 设置第二个元素
     */
    public void setSecond(Object value) {
        values.set(1, value);
    }

    /**
     * 获取第三个元素
     */
    @SuppressWarnings("unchecked")
    public <T> T getThird() {
        return (T) values.get(2);
    }

    /**
     * 设置第三个元素
     */
    public void setThird(Object value) {
        values.set(2, value);
    }

    /**
     * 获取第四个元素
     */
    @SuppressWarnings("unchecked")
    public <T> T getFourth() {
        return (T) values.get(3);
    }

    /**
     * 设置第四个元素
     */
    public void setFourth(Object value) {
        values.set(3, value);
    }

    /**
     * 获取第五个元素
     */
    @SuppressWarnings("unchecked")
    public <T> T getFifth() {
        return (T) values.get(4);
    }

    /**
     * 设置第五个元素
     */
    public void setFifth(Object value) {
        values.set(4, value);
    }

    /**
     * 获取第六个元素
     */
    @SuppressWarnings("unchecked")
    public <T> T getSixth() {
        return (T) values.get(5);
    }

    /**
     * 设置第六个元素
     */
    public void setSixth(Object value) {
        values.set(5, value);
    }

    /**
     * 获取第七个元素
     */
    @SuppressWarnings("unchecked")
    public <T> T getSeventh() {
        return (T) values.get(6);
    }

    /**
     * 设置第七个元素
     */
    public void setSeventh(Object value) {
        values.set(6, value);
    }

    /**
     * 获取第八个元素
     */
    @SuppressWarnings("unchecked")
    public <T> T getEighth() {
        return (T) values.get(7);
    }

    /**
     * 设置第八个元素
     */
    public void setEighth(Object value) {
        values.set(7, value);
    }

    /**
     * 获取第九个元素
     */
    @SuppressWarnings("unchecked")
    public <T> T getNinth() {
        return (T) values.get(8);
    }

    /**
     * 设置第九个元素
     */
    public void setNinth(Object value) {
        values.set(8, value);
    }

    /**
     * 获取第十个元素
     */
    @SuppressWarnings("unchecked")
    public <T> T getTenth() {
        return (T) values.get(9);
    }

    /**
     * 设置第十个元素
     */
    public void setTenth(Object value) {
        values.set(9, value);
    }

    /**
     * 获取指定元素
     */
    @SuppressWarnings("unchecked")
    public <T> T get(int index) {
        return (T) values.get(index);
    }

    /**
     * 设置指定元素
     */
    public void set(int index, Object value) {
        values.set(index, value);
    }

    /**
     * 转化为 List
     */
    public List<Object> toList() {
        return new ArrayList<>(this.values);
    }

    @Override
    public String toString() {
        var builder = new StringBuilder();
        for (var value : values) {
            if (!builder.isEmpty()) {
                builder.append(", ");
            }
            builder.append(value);
        }
        return "(" + builder + ")";
    }
}
