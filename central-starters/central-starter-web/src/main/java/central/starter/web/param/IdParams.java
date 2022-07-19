package central.starter.web.param;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 主键参数
 *
 * @author Alan Yeh
 * @since 2022/07/15
 */
@Data
public class IdParams implements Serializable {
    @Serial
    private static final long serialVersionUID = 6321780117652753197L;

    /**
     * 主键
     */
    @NotBlank(message = "主键[id]必须不为空")
    private String id;
}
