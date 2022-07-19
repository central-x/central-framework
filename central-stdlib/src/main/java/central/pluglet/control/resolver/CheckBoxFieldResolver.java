package central.pluglet.control.resolver;

import central.data.NameValue;
import central.data.OptionalEnum;
import central.lang.reflect.FieldReference;
import central.pluglet.annotation.Control;
import central.pluglet.control.ControlType;
import central.pluglet.control.PlugletControl;
import central.util.*;

import java.util.List;

/**
 * CheckBox Control
 *
 * @author Alan Yeh
 * @since 2022/07/11
 */
public class CheckBoxFieldResolver extends FieldResolver {
    @Override
    public boolean support(FieldReference field) {
        var annotation = field.getAnnotation(Control.class);
        return annotation != null && ControlType.CHECKBOX == annotation.type();
    }

    @Override
    public PlugletControl resolve(FieldReference field) {
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
                .map(it -> new NameValue<>(it.getName(), it.getValue()))
                .toList();

        return PlugletControl.builder()
                .label(annotation.label())
                .name(Objectx.get(annotation.name(), field.getName()))
                .type(annotation.type().name())
                .comment(annotation.comment())
                .defaultValue(List.of(annotation.defaultValue()))
                .required(annotation.required())
                .options(options)
                .build();
    }
}
