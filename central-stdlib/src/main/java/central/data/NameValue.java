package central.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * Name Value Pair
 *
 * @author Alan Yeh
 * @since 2022/07/11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NameValue<V extends Serializable> implements Serializable {
    @Serial
    private static final long serialVersionUID = -839267549007873480L;
    /**
     * Name
     */
    private String name;
    /**
     * Value
     */
    private V value;
}
