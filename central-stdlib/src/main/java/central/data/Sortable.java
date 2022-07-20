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
