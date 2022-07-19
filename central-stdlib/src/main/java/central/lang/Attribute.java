package central.lang;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * 属性
 *
 * @author Alan Yeh
 * @since 2022/07/13
 */
@RequiredArgsConstructor
public class Attribute<T> {
    /**
     * Code
     */
    @Getter
    private final String code;

    /**
     * Value Supplier
     */
    private final Supplier<T> supplier;

    public Attribute(String code) {
        this.code = code;
        this.supplier = null;
    }

    /**
     * Get value from supplier
     */
    @Nullable
    public T getValue() {
        if (supplier != null) {
            return supplier.get();
        } else {
            return null;
        }
    }
}
