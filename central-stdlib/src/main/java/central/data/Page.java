package central.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 分页结果
 *
 * @author Alan Yeh
 * @since 2022/07/10
 */
@Data
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
    public static <T extends Serializable> Page<T> emptyPage() {
        return new Page<>(Collections.emptyList(), Pager.emptyPager());
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

        public static Pager emptyPager() {
            return new Pager(0, 0, 0, 0);
        }
    }
}
