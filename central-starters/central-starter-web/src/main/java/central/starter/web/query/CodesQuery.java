package central.starter.web.query;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 根据标识查询
 *
 * @author Alan Yeh
 * @since 2022/07/15
 */
@Data
public class CodesQuery implements Serializable {
    @Serial
    private static final long serialVersionUID = -6439623074180542011L;

    /**
     * 标识集合
     */
    @NotEmpty(message = "标识[codes]必须不为空")
    private List<String> codes;
}
