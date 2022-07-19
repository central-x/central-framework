package central.net.http.server.controller.param;

import central.util.validate.group.Insert;
import central.util.validate.group.Update;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

/**
 * 创建用户参数
 *
 * @author Alan Yeh
 * @since 2022/07/15
 */
@Data
public class AccountParams {
    @NotBlank(message = "主键[id]必须不为空", groups = Update.class)
    @Null(message = "主键[id]必须为空", groups = Insert.class)
    private String id;

    @NotBlank(message = "姓名[name]必须不为空")
    private String name;

    @NotNull(message = "年龄[age]必须不为空")
    private Integer age;
}
