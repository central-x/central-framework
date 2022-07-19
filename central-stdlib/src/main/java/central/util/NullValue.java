package central.util;

import java.io.Serial;
import java.io.Serializable;

/**
 * 空值
 *
 * @author Alan Yeh
 * @since 2022/07/13
 */
public class NullValue<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 2753865325839731879L;

    private T value = null;

    public void setValue(T value) {
        if (value != null) {
            throw new UnsupportedOperationException("Cannot change null value");
        }
    }

    public T getValue() {
        return null;
    }
}
