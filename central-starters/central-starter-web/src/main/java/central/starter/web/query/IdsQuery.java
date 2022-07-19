package central.starter.web.query;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 主键查询
 *
 * @author Alan Yeh
 * @since 2022/07/15
 */
@Data
public class IdsQuery implements Serializable {
    @Serial
    private static final long serialVersionUID = -4904725290170815070L;

    /**
     * 主键集合
     */
    @NotEmpty(message = "主键[ids]必须不为空")
    private List<String> ids;
}
