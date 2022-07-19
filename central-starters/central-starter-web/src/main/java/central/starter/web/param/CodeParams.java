package central.starter.web.param;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 标识参数
 *
 * @author Alan Yeh
 * @since 2022/07/15
 */
@Data
public class CodeParams implements Serializable {
    @Serial
    private static final long serialVersionUID = 546203729981715685L;

    /**
     * 标识
     */
    @NotBlank(message = "标识[code]必须不为空")
    private String code;
}
