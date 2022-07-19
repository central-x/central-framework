package central.starter.web.param;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 标识查询
 *
 * @author Alan Yeh
 * @since 2022/07/15
 */
@Data
public class CodesParams implements Serializable {

    @Serial
    private static final long serialVersionUID = -1789169044490209864L;

    /**
     * 标识集合
     */
    @NotEmpty(message = "标识[codes]必须不为空")
    private List<String> codes;
}
