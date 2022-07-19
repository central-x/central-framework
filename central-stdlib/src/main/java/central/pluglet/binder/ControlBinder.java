package central.pluglet.binder;

import central.bean.InitializeException;
import central.lang.reflect.FieldReference;
import central.lang.reflect.InstanceReference;
import central.pluglet.FieldBinder;
import central.pluglet.annotation.Control;
import central.pluglet.control.ControlType;
import central.util.*;

import java.util.Map;
import java.util.Set;

/**
 * 属性绑定
 *
 * @author Alan Yeh
 * @since 2022/07/12
 */
public class ControlBinder implements FieldBinder {

    private final Set<ControlType> unsupported = Setx.of(ControlType.RADIO, ControlType.CHECKBOX);

    @Override
    public boolean support(FieldReference field) {
        var control = field.getAnnotation(Control.class);
        return control != null && !unsupported.contains(control.type());
    }

    @Override
    public void bind(InstanceReference<?> target, FieldReference field, Map<String, Object> params) {
        var annotation = field.getAnnotation(Control.class);
        var name = Objectx.get(annotation.name(), field.getName());
        var value = params.get(name);
        if (value == null){
            value = Objectx.get(Arrayx.getFirst(annotation.defaultValue()), () -> null);
        }
        if (value != null) {
            if (!Converterx.Default().support(value.getClass(), field.getType().getRawClass())) {
                throw new InitializeException(target.getType().getRawClass(), Stringx.format("Cannot convert '{}' to '{}'", value, field.getType().getRawClass().getName()));
            }
            value = Converterx.Default().convert(value, field.getType().getRawClass());
            field.setValue(target, value);
        }
    }
}
