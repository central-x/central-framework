package central.data;

import java.util.Objects;

/**
 * Optional Entity
 *
 * @author Alan Yeh
 * @since 2022/07/11
 */
public interface OptionalEnum<V> {
    /**
     * 选项名
     */
    String getName();

    /**
     * 选项值
     */
    V getValue();

    /**
     * 判断当前选项与指定选项值是否匹配
     *
     * @param value 指定选项值
     */
    default boolean isCompatibleWith(Object value) {
        if (value == null) {
            return false;
        }
        if (value.getClass().isAssignableFrom(this.getClass())) {
            return Objects.equals(this, value);
        }
        return Objects.equals(this.getValue(), value);
    }
}
