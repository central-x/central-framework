package central.pluglet.binder;

import central.data.OptionalEnum;
import central.lang.reflect.FieldReference;
import central.lang.reflect.InstanceReference;
import central.pluglet.FieldBinder;
import central.pluglet.annotation.Control;
import central.pluglet.control.ControlType;
import central.util.Arrayx;
import central.util.Assertx;
import central.util.Objectx;
import central.util.Stringx;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Checkbox Control Binder
 *
 * @author Alan Yeh
 * @since 2022/07/13
 */
public class CheckboxControlBinder implements FieldBinder {
    @Override
    public boolean support(FieldReference field) {
        var control = field.getAnnotation(Control.class);
        return control != null && ControlType.CHECKBOX == control.type();
    }

    @Override
    public void bind(InstanceReference<?> target, FieldReference field, Map<String, Object> params) {
        var annotation = field.getAnnotation(Control.class);

        // 类型检查
        Assertx.mustAssignableFrom(List.class, field.getType().getRawClass(), "Field '{}' requires 'List<? extend Enum>'", field.getName());
        Assertx.mustTrue(field.getType().isParameterized(), "Field '{}' requires 'List<? extend Enum>'", field.getName());
        var enumType = field.getType().getActualTypeArgument(0);
        Assertx.mustTrue(enumType.isEnum(), "Field '{}' requires 'List<? extend Enum>'", field.getName());

        // 枚举列表
        var options = Arrayx.asStream(enumType.getRawClass().getEnumConstants())
                .peek(it -> Assertx.mustInstanceOf(OptionalEnum.class, it, "Enum '{}' MUST implements Optional<String>"))
                .map(it -> (OptionalEnum<String>) it)
                .toList();

        var name = Objectx.get(annotation.name(), field.getName());
        var param = params.get(name);
        var paramValues = new ArrayList<String>();
        if (param instanceof List<?> l) {
            paramValues.addAll(l.stream().map(Objectx::toString).filter(Objectx::isNotNull).toList());
        } else if (param != null) {
            paramValues.add(param.toString());
        } else {
            paramValues.addAll(Arrayx.asStream(annotation.defaultValue()).filter(Stringx::isNotBlank).toList());
        }

        var values = new ArrayList<>();
        for (var value : paramValues) {
            var optional = options.stream().filter(it -> it.isCompatibleWith(value)).findFirst();
            if (optional.isEmpty()) {
                throw new IllegalArgumentException(Stringx.format("Field '{}' need enum values({})", options.stream().map(OptionalEnum::getValue).collect(Collectors.joining(", "))));
            }
            values.add(optional.get());
        }

        field.setValue(target, values);
    }
}
