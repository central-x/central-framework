package central.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * Code Value Pair
 *
 * @author Alan Yeh
 * @since 2022/07/05
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CodeValue<V extends Serializable> implements Codeable, Serializable {
    @Serial
    private static final long serialVersionUID = 4057748517887523735L;

    /**
     * Code
     */
    private String code;

    /**
     * Value
     */
    private V value;
}
