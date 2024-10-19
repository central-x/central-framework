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

import central.lang.PublicApi;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 分页
 *
 * @author Alan Yeh
 * @since 2022/07/10
 */
@Data
@PublicApi
@NoArgsConstructor
public class Page<T extends Serializable> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1013358155713268204L;
    /**
     * 数据
     */
    private List<T> data = null;
    /**
     * 分页信息
     */
    private Pager pager = null;

    /**
     * 创建分页结果
     *
     * @param data      分页数据
     * @param pageIndex 分页下标
     * @param pageSize  分页大小
     * @param pageCount 分页总数
     * @param itemCount 数据总数
     */
    public Page(List<T> data, long pageIndex, long pageSize, long pageCount, long itemCount) {
        this.data = data;
        this.pager = new Pager(pageIndex, pageSize, pageCount, itemCount);
    }

    /**
     * 创建分页结果
     *
     * @param data  分页数据
     * @param pager 分页信息
     */
    public Page(List<T> data, Pager pager) {
        this.data = data;
        this.pager = pager;
    }

    /**
     * 创建空分页
     */
    public static <T extends Serializable> Page<T> ofEmpty() {
        return new Page<>(Collections.emptyList(), Pager.ofEmpty());
    }

    /**
     * 创建空分页
     *
     * @param pager 分页信息
     */
    public static <T extends Serializable> Page<T> ofEmpty(Pager pager) {
        return new Page<>(Collections.emptyList(), pager);
    }

    /**
     * 创建空分页
     *
     * @param pageIndex 分页下标
     * @param pageSize  分页大小
     */
    public static <T extends Serializable> Page<T> ofEmpty(long pageIndex, long pageSize) {
        return new Page<>(List.of(), Pager.ofEmpty(pageIndex, pageSize));
    }

    public static <T extends Serializable> Page<T> of(List<T> data, Pager pager) {
        return new Page<>(data, pager);
    }

    public static <T extends Serializable> Page<T> of(List<T> data, long pageIndex, long pageSize, long pageCount, long itemCount) {
        return new Page<>(data, pageIndex, pageSize, pageCount, itemCount);
    }

    /**
     * 分页信息
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Pager implements Serializable {
        @Serial
        private static final long serialVersionUID = 2733919861157759916L;
        /**
         * 分页下标（从 1 开始）
         */
        private long pageIndex;
        /**
         * 分页大小
         */
        private long pageSize;
        /**
         * 分页总数
         */
        private long pageCount;
        /**
         * 数据总数
         */
        private long itemCount;

        public static Pager ofEmpty() {
            return new Pager(0, 0, 0, 0);
        }

        public static Pager ofEmpty(long pageIndex, long pageSize) {
            return new Pager(pageIndex, pageSize, 0, 0);
        }

        public static Pager of(long pageIndex, long pageSize, long pageCount, long itemCount) {
            return new Pager(pageIndex, pageSize, pageCount, itemCount);
        }
    }
}
