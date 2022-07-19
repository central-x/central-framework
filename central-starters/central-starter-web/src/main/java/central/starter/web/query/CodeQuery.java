package central.starter.web.query;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 根据标识查询
 *
 * @author Alan Yeh
 * @since 2022/07/15
 */
@Data
public class CodeQuery implements Serializable {
    @Serial
    private static final long serialVersionUID = 3138572266790126537L;

    /**
     * 标识
     */
    @NotBlank(message = "标识[code]必须不为空")
    private String code;
}
