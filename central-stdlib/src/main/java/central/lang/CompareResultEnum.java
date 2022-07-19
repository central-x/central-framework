package central.lang;

import central.data.OptionalEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 比较结果
 * -1: left < right
 * 0: left = right
 * 1: left > right
 *
 * @author Alan Yeh
 * @since 2022/07/13
 */
@Getter
@AllArgsConstructor
public enum CompareResultEnum implements OptionalEnum<String> {

    LESS("小于", "<", -1),
    EQUALS("等于", "=", 0),
    GREATER("大于", ">", 1);

    private final String name;
    private final String value;
    private final int result;

    @Override
    public boolean isCompatibleWith(Object value) {
        if (value instanceof String s) {
            return this.getValue().equals(s);
        } else if (value instanceof Number n) {
            return this.result == n.intValue();
        } else {
            return false;
        }
    }
}
