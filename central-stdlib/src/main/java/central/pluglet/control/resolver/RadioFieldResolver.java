package central.pluglet.control.resolver;

import central.bean.TypeCheckException;
import central.data.NameValue;
import central.data.OptionalEnum;
import central.lang.reflect.FieldReference;
import central.pluglet.annotation.Control;
import central.pluglet.control.ControlType;
import central.pluglet.control.PlugletControl;
import central.util.Arrayx;
import central.lang.Assertx;
import central.util.Objectx;
import central.util.Stringx;


/**
 * Radio Control
 *
 * @author Alan Yeh
 * @since 2022/07/11
 */
public class RadioFieldResolver extends FieldResolver {
    @Override
    public boolean support(FieldReference field) {
        var annotation = field.getAnnotation(Control.class);
        return annotation != null && ControlType.RADIO == annotation.type();
    }

    @Override
    public PlugletControl resolve(FieldReference field) {
        var annotation = field.getAnnotation(Control.class);

        // 类型检查
        Assertx.mustTrue(field.getType().isEnum(), () -> new TypeCheckException(Stringx.format("Field '{}' requires Enum type", field.getName())));
        // 枚举列表
        var options = Arrayx.asStream(field.getType().getRawClass().getEnumConstants())
                .peek(it -> Assertx.mustInstanceOf(OptionalEnum.class, it, () -> new TypeCheckException("Enum '{}' MUST implements Optional<String>")))
                .map(it -> (OptionalEnum<String>) it)
                .map(it -> new NameValue<>(it.getName(), it.getValue()))
                .toList();

        return PlugletControl.builder()
                .label(annotation.label())
                .name(Objectx.get(annotation.name(), field.getName()))
                .type(annotation.type().name())
                .comment(annotation.comment())
                .defaultValue(Arrayx.getFirst(annotation.defaultValue()))
                .required(annotation.required())
                .options(options)
                .build();
    }
}
