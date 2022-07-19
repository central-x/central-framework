package central.bean;

import central.util.Stringx;

import java.io.Serial;

/**
 * 类型检查异常
 *
 * @author Alan Yeh
 * @since 2022/07/12
 */
public class TypeCheckException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 7212345137343048200L;

    public TypeCheckException(String message) {
        super(message);
    }

    public TypeCheckException(String message, Throwable cause) {
        super(message, cause);
    }

    public TypeCheckException(Class<?> expected, Class<?> actual) {
        super(Stringx.format("Type checked failed: Required type '{}' but provides '{}'", expected.getName(), actual.getName()));
    }
}
