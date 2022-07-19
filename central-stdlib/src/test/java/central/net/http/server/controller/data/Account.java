package central.net.http.server.controller.data;

import central.data.ModifiableEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 帐户
 *
 * @author Alan Yeh
 * @since 2022/07/15
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Account extends ModifiableEntity {
    @Serial
    private static final long serialVersionUID = 8286491283299449417L;

    private String name;

    private Integer age;

    private String deptId;

    private Dept dept;
}
