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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Set 工具
 *
 * @author Alan Yeh
 * @since 2022/07/12
 */
@UtilityClass
public class Setx {

    /**
     * 获取 Set 中的任一元素
     *
     * @param set 集合
     */
    public static <T> T getAny(@Nullable Set<T> set) {
        if (set == null || set.isEmpty()) {
            return null;
        } else {
            return set.iterator().next();
        }
    }

    /**
     * 将可空集合变成不可空集合
     *
     * @param set 集合
     * @param <T> 元素类型
     */
    public static <T> Set<T> nullSafe(@Nullable Set<T> set) {
        return set == null ? newHashSet() : set;
    }

    /**
     * 判断集合是否为空
     *
     * @param set 集合
     * @param <T> 元素类型
     */
    public static <T> boolean isNullOrEmpty(@Nullable Set<T> set) {
        return set == null || set.isEmpty();
    }

    /**
     * 判断集合是否不为空
     *
     * @param set 集合
     * @param <T> 元素类型
     */
    public static <T> boolean isNotEmpty(@Nullable Set<T> set) {
        return set != null && !set.isEmpty();
    }

    /**
     * 转成 Stream
     *
     * @param set 集合
     * @param <T> 元素类型
     */
    public static <T> Stream<T> asStream(@Nullable Set<T> set) {
        return isNullOrEmpty(set) ? Stream.empty() : set.stream();
    }

    /**
     * 快速创建一个不可变集合
     *
     * @param elements 元素
     * @param <T>      元素类型
     */
    @SafeVarargs
    public static <T> Set<T> of(T... elements) {
        return Set.of(elements);
    }

    /**
     * 快速创建 HashSet
     *
     * @param elements 元素
     * @param <T>      元素类型
     */
    @SafeVarargs
    public static <T> Set<T> newHashSet(T... elements) {
        return new HashSet<>(Arrays.asList(elements));
    }

    /**
     * 判断两个集合里的元素是否相等
     *
     * @param source 源集合
     * @param target 目标集合
     * @param <T>    元素类型
     */
    public static <T> boolean equals(Set<T> source, Set<T> target) {
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

            return source.containsAll(target) && target.containsAll(source);
        }
    }
}
