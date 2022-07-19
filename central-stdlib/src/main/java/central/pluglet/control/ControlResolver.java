package central.pluglet.control;

import central.bean.MethodNotImplementedException;
import central.lang.reflect.FieldReference;
import central.lang.reflect.MethodReference;
import central.pluglet.control.PlugletControl;

import java.lang.reflect.Field;

/**
 * Pluglet Control Resolver
 * 用于解析 Pluglet 中暴露的控件信息
 *
 * @author Alan Yeh
 * @since 2022/07/11
 */
public interface ControlResolver {
    boolean support(FieldReference field);

    default PlugletControl resolve(FieldReference field) {
        throw new MethodNotImplementedException(new MethodReference() {});
    }
}
