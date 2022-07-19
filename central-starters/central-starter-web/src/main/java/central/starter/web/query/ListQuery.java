package central.starter.web.query;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 列表查询入参
 *
 * @author Alan Yeh
 * @since 2022/07/15
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ListQuery extends KeywordQuery {
    @Serial
    private static final long serialVersionUID = 3411599472644011096L;

}
