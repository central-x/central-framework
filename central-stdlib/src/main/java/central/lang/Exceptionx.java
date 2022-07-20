package central.lang;

import central.util.Stringx;

/**
 * Exception 工具类
 *
 * @author Alan Yeh
 * @since 2022/07/20
 */
public class Exceptionx {
    public static IllegalArgumentException newIllegalNullArgument(String argument) {
        return new IllegalArgumentException(Stringx.format("Illegal argument '{}'", argument));
    }
}
