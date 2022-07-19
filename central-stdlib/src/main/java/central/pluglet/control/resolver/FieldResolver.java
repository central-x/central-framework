package central.pluglet.control.resolver;

import central.lang.reflect.FieldReference;
import central.pluglet.control.ControlResolver;
import central.pluglet.annotation.Control;
import central.pluglet.control.ControlType;
import central.pluglet.control.PlugletControl;
import central.util.Arrayx;
import central.util.Converterx;
import central.util.Objectx;
import central.util.Setx;

import java.util.Set;

/**
 * Normal Field Resolver
 *
 * @author Alan Yeh
 * @since 2022/07/11
 */
public class FieldResolver implements ControlResolver {

    private final Set<ControlType> unsupported = Setx.of(ControlType.CHECKBOX, ControlType.RADIO);
    @Override
    public boolean support(FieldReference field) {
        var annotation = field.getAnnotation(Control.class);
        return annotation != null && !unsupported.contains(annotation.type());
    }

    @Override
    public PlugletControl resolve(FieldReference field) {
        var annotation = field.getAnnotation(Control.class);
        return PlugletControl.builder()
                .label(annotation.label())
                .name(Objectx.get(annotation.name(), field.getName()))
                .type(annotation.type().name())
                .comment(annotation.comment())
                .defaultValue(Converterx.Default().convert(Arrayx.getFirst(annotation.defaultValue()), field.getType().getRawClass()))
                .required(annotation.required())
                .build();
    }

//    @Override
//    public void bind(Object target, Field field, Map<String, Object> params) throws IllegalAccessException {
//        var annotation = field.getAnnotation(Control.class);
//
//        var name = Objectx.get(annotation.name(), field.getName());
//        var value = Converterx.Default().convert(params.get(name), field.getType());
//
//        if (value != null) {
//            field.setAccessible(true);
//            field.set(target, value);
//        }
//    }
}
