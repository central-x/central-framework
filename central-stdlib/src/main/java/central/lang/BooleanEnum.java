package central.lang;

import central.data.OptionalEnum;
import central.util.Stringx;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Boolean Enum
 * 开关
 *
 * @author Alan Yeh
 * @since 2022/07/13
 */
@Getter
@AllArgsConstructor
public enum BooleanEnum implements OptionalEnum<String> {

    TRUE("是", "1", true),
    FALSE("否", "0", false);

    private final String name;
    private final String value;
    private final Boolean jValue;

    @Override
    public String toString() {
        return this.value;
    }

    @JsonCreator
    public static BooleanEnum resolve(Object value) {
        if (value instanceof String s) {
            if (Stringx.isNotBlank(s) && ("true".equalsIgnoreCase(s) || "1".equalsIgnoreCase(s))) {
                return BooleanEnum.TRUE;
            } else {
                return BooleanEnum.FALSE;
            }
        } else if (value instanceof Boolean b) {
            return b ? BooleanEnum.TRUE : BooleanEnum.FALSE;
        } else {
            return BooleanEnum.FALSE;
        }
    }

    @Override
    public boolean isCompatibleWith(Object value) {
        if (value instanceof BooleanEnum b) {
            return this.equals(b);
        }
        if (value instanceof Boolean b) {
            return this.getJValue().equals(b);
        }
        if (value instanceof String s) {
            return this.getValue().equals(s);
        }
        return false;
    }
}
