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

package central.data;

import central.lang.Assertx;
import central.util.Objectx;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Nonnull;

/**
 * Sortable Entity
 * 可排序的实体
 *
 * @author Alan Yeh
 * @since 2022/07/10
 */
public interface Sortable<T extends Sortable<T>> extends Comparable<T> {
    /**
     * 获取排序号
     */
    Integer getSortNo();

    /**
     * 设置排序号
     *
     * @param sortNo 排序号
     */
    void setSortNo(Integer sortNo);

    @Override
    default int compareTo(@Nonnull T target) {
        Assertx.mustNotNull(target, "Argument 'target' must not null");
        // 相同对象才能排序
        Assertx.mustInstanceOf(this.getClass(), target, "Argument 'argument' must instance of " + this.getClass().getName());

        Integer thisSortNo = Objectx.get(this.getSortNo(), -1);
        Integer targetSortNo = Objectx.get(target.getSortNo(), -1);
        return thisSortNo.compareTo(targetSortNo);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class Holder<T extends Sortable<T>> implements Sortable<Holder<T>> {
        private T data;

        private Integer sortNo;
    }
}
