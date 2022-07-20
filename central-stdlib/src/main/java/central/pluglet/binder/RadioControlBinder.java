package central.pluglet.binder;

import central.bean.TypeCheckException;
import central.data.OptionalEnum;
import central.lang.reflect.FieldReference;
import central.lang.reflect.InstanceReference;
import central.pluglet.FieldBinder;
import central.pluglet.annotation.Control;
import central.pluglet.control.ControlType;
import central.util.Arrayx;
import central.lang.Assertx;
import central.util.Objectx;
import central.util.Stringx;

import java.util.Map;

/**
 * Radio Control Binder
 *
 * @author Alan Yeh
 * @since 2022/07/13
 */
public class RadioControlBinder implements FieldBinder {
    @Override
    public boolean support(FieldReference field) {
        var control = field.getAnnotation(Control.class);
        return control != null && ControlType.RADIO == control.type();
    }

    @Override
    public void bind(InstanceReference<?> target, FieldReference field, Map<String, Object> params) {
        var annotation = field.getAnnotation(Control.class);

        // 类型检查
        Assertx.mustTrue(field.getType().isEnum(), () -> new TypeCheckException(Stringx.format("Field '{}' requires Enum type", field.getName())));
        // 枚举列表
        var options = Arrayx.asStream(field.getType().getRawClass().getEnumConstants())
                .peek(it -> Assertx.mustInstanceOf(OptionalEnum.class, it, () -> new TypeCheckException("Enum '{}' MUST implements Optional<String>")))
                .map(it -> (OptionalEnum<String>) it)
                .toList();

        var name = Objectx.get(annotation.name(), field.getName());
        var value = params.get(name);
        if (value == null) {
            value = Arrayx.getFirst(annotation.defaultValue());
        }

        if (value != null) {
            value = value.toString();
            for (var option : options) {
                if (option.isCompatibleWith(value)) {
                    value = option;
                    break;
                }
            }
            field.setValue(target, value);
        }

    }
}
