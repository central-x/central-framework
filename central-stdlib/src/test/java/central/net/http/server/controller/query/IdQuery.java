package central.net.http.server.controller.query;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 主键查询
 *
 * @author Alan Yeh
 * @since 2022/07/15
 */
@Data
public class IdQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 4029804593817481197L;

    /**
     * 主键
     */
    @NotBlank(message = "主键[id]必须不为空")
    private String id;
}
