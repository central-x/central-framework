package central.net.http.server.controller.data;

import central.data.ModifiableEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 部门信息
 * @author Alan Yeh
 * @since 2022/07/15
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Dept extends ModifiableEntity {
    @Serial
    private static final long serialVersionUID = -2696108123955301809L;

    private String name;
}
