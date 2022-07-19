package central.starter.web.query;

import central.util.Objectx;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 分页查询
 *
 * @author Alan Yeh
 * @since 2022/07/15
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PageQuery extends KeywordQuery {
    @Serial
    private static final long serialVersionUID = 3818802335415736151L;

    /**
     * 分页索引
     * 由 1 开始
     */
    @Min(value = 1, message = "分页索引[pageIndex]由 1 开始")
    private Long pageIndex;

    public Long getPageIndex() {
        return Objectx.get(this.pageIndex, 1L);
    }

    /**
     * 分页大小
     */
    @Min(value = 1, message = "分页大小[pageSize]最小值为 1")
    private Long pageSize;

    public Long getPageSize(){
        return Objectx.get(this.pageSize, 20L);
    }
}
