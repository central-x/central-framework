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

import lombok.experimental.UtilityClass;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Stream;

/**
 * List 工具
 *
 * @author Alan Yeh
 * @since 2022/07/12
 */
@UtilityClass
public class Listx {

    /**
     * 将可空集合变成不可空集合
     *
     * @param list 集合
     * @param <T>  元素类型
     */
    public static <T> List<T> nullSafe(@Nullable List<T> list) {
        return list == null ? newArrayList() : list;
    }

    /**
     * 判断集合是否为空
     *
     * @param list 集合
     * @param <T>  元素类型
     */
    public static <T> boolean isNullOrEmpty(@Nullable List<T> list) {
        return list == null || list.isEmpty();
    }

    /**
     * 判断集合是否不为空
     *
     * @param list 集合
     * @param <T>  元素类型
     */
    public static <T> boolean isNotEmpty(@Nullable List<T> list) {
        return list != null && !list.isEmpty();
    }

    /**
     * 转成 Stream
     *
     * @param list 集合
     * @param <T>  集合类型
     */
    public static <T> Stream<T> asStream(@Nullable List<T> list) {
        return isNullOrEmpty(list) ? Stream.empty() : list.stream();
    }

    /**
     * 获取第一个元素
     *
     * @param list 集合
     * @param <T>  集合类型
     */
    public static <T> T getFirst(@Nullable List<T> list) {
        return isNullOrEmpty(list) ? null : list.get(0);
    }

    /**
     * 获取最后一个元素
     *
     * @param list 集合
     * @param <T>  集合类型
     */
    public static <T> T getLast(@Nullable List<T> list) {
        return isNullOrEmpty(list) ? null : list.get(list.size() - 1);
    }

    /**
     * 获取指定下标的元素
     *
     * @param list  集合
     * @param index 下标
     * @param <T>   集合类型
     */
    public static <T> T get(@Nullable List<T> list, int index) {
        if (list == null || list.size() <= index) {
            return null;
        } else {
            return list.get(index);
        }
    }

    /**
     * 快速创建不可修改类型集合
     *
     * @param elements 元素
     * @param <T>      元素类型
     */
    @SafeVarargs
    public static <T> List<T> of(T... elements) {
        return List.of(elements);
    }

    /**
     * 快速创建 ArrayList
     *
     * @param elements 元素
     * @param <T>      元素类型
     */
    @SafeVarargs
    public static <T> List<T> newArrayList(T... elements) {
        return new ArrayList<>(Arrays.asList(elements));
    }

    /**
     * 快速创建 LinkedList
     *
     * @param elements 元素
     * @param <T>      元素类型
     */
    @SafeVarargs
    public static <T> List<T> newLinkedList(T... elements) {
        return new LinkedList<>(Arrays.asList(elements));
    }

    /**
     * 判断两个集合是否相等
     *
     * @param source 源集合
     * @param target 目标集合
     * @param <T>    元素类型
     */
    public static <T> boolean equals(List<T> source, List<T> target) {
        if (source == null && target == null) {
            // 两个集合都为空
            return true;
        } else if (source == null || target == null) {
            // 一个集合为空，另一个集合不为空
            return false;
        } else {
            // 两个集合都不为空
            if (source.size() != target.size()) {
                // 集合数量不一致
                return false;
            }
            for (int i = 0, length = source.size(); i < length; i++) {
                if (!Objects.equals(source.get(i), target.get(i))) {
                    return false;
                }
            }
            return true;
        }
    }
}
