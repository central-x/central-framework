package central.starter.web.param;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 主键参数
 *
 * @author Alan Yeh
 * @since 2022/07/15
 */
@Data
public class IdsParams implements Serializable {
    @Serial
    private static final long serialVersionUID = 6931276106106768839L;

    /**
     * 主键集合
     */
    @NotEmpty(message = "主键[ids]必须不为空")
    private List<String> ids;
}
