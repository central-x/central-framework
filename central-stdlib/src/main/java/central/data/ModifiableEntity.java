package central.data;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.sql.Timestamp;

/**
 * Modifiable Entity
 * 可更新实体
 *
 * @author Alan Yeh
 * @since 2022/07/10
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ModifiableEntity extends Entity implements Modifiable{
    @Serial
    private static final long serialVersionUID = 2093546258253512035L;

    /**
     * 更新人唯一标识
     */
    private String modifierId;

    /**
     * 更新时间
     */
    private Timestamp modifyDate;
}
