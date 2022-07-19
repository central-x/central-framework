package central.bean;

import central.lang.reflect.MethodReference;
import central.util.Stringx;

import java.io.Serial;
import java.lang.reflect.Method;

/**
 * 方法未实现异常
 *
 * @author Alan Yeh
 * @since 2022/07/12
 */
public class MethodNotImplementedException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -7315730828175305310L;

    public MethodNotImplementedException() {
        super();
    }

    public MethodNotImplementedException(Method method) {
        super(Stringx.format("Method '{}#{}' not implemented", method.getDeclaringClass().getName(), method.getName()));
    }

    public MethodNotImplementedException(Class<?> clazz, String method) {
        super(Stringx.format("Method '{}#{}' not implemented", clazz.getName(), method));
    }

    public MethodNotImplementedException(MethodReference reference) {
        super(Stringx.format("Method '{}#{}' not implemented", reference.getMethod().getDeclaringClass().getName(), reference.getMethod().getName()));
    }
}
