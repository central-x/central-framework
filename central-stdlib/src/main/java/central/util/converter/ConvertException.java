package central.util.converter;

import central.util.Stringx;

import java.io.Serial;

/**
 * Convert Exception
 * 格式转换异常
 *
 * @author Alan Yeh
 * @since 2022/07/11
 */
public class ConvertException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -7706679677177839195L;

    public ConvertException(String message) {
        super(message);
    }

    public ConvertException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConvertException(Object source, Class<?> target) {
        super(Stringx.format("Cannot convert value '{}'({}) to {}", source, source.getClass().getName(), target.getName()));
    }
}
