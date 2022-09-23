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
import central.lang.Stringx;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Treeable Entity
 * 可构建成树状的数据
 *
 * @author Alan Yeh
 * @since 2022/07/10
 */
public interface Treeable<T extends Treeable<T>> extends Identifiable {
    /**
     * 节点唯一标识
     */
    @Override
    @Nonnull
    String getId();

    /**
     * 父节点唯一标识
     * 如果父节点为空，则认为是根节点
     */
    @Nullable
    String getParentId();

    /**
     * 设置父节点唯一标识
     *
     * @param parentId 父节点唯一标识
     */
    void setParentId(@Nullable String parentId);

    /**
     * 获取子节点
     */
    List<T> getChildren();

    /**
     * 设置子节点
     *
     * @param children 子节点列表
     */
    void setChildren(@Nullable List<T> children);


    /**
     * 将数据构建成树
     *
     * @param data 数据
     * @param <T>  数据类型
     * @return 已构造成树的数据
     */
    static <T extends Treeable<T>> List<T> build(@Nullable Collection<T> data) {
        return Treeable.build(data, null);
    }

    /**
     * 将数据构建成树
     *
     * @param data       数据
     * @param comparator 每个层级的排序器
     * @param <T>        数据类型
     * @return 已构造成树的数据
     */
    static <T extends Treeable<T>> List<T> build(@Nullable Collection<T> data, @Nullable Comparator<T> comparator) {
        if (Collectionx.isNullOrEmpty(data)) {
            return Collections.emptyList();
        }

        // 如果只有一个元素，无法构建成树
        if (data.size() == 1) {
            return Collections.singletonList(Collectionx.getFirst(data));
        }

        // 所有节点的主键
        var ids = new HashSet<String>();

        var map = data.stream().peek(it -> {
            if (Stringx.isNullOrEmpty(it.getParentId())) {
                it.setParentId("_root");
            }
            ids.add(it.getId());
        }).collect(Collectors.groupingBy(Treeable::getParentId));

        // 构建成树
        Stream<T> stream = data.stream()
                .peek(it -> {
                    var children = map.get(it.getId());
                    if (Collectionx.isNotEmpty(children)) {
                        if (it.getChildren() == null) {
                            it.setChildren(new ArrayList<>());
                        }
                        it.getChildren().addAll(children);
                        if (comparator != null) {
                            it.getChildren().sort(comparator);
                        }
                    }
                }).filter(it -> "_root".equals(it.getParentId()) || !ids.contains(it.getParentId()))
                .peek(it -> {
                    if ("_root".equals(it.getParentId())) {
                        it.setParentId(null);
                    }
                });
        if (comparator != null) {
            stream = stream.sorted(comparator);
        }
        return stream.collect(Collectors.toList());
    }
}
