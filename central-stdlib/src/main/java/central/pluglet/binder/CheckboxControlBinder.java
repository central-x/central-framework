/*
 * MIT License
 *
 * Copyright (c) 2022-present Alan Yeh <alan@yeh.cn>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package central.pluglet.binder;

import central.bean.OptionalEnum;
import central.lang.reflect.FieldReference;
import central.lang.reflect.InstanceReference;
import central.pluglet.FieldBinder;
import central.pluglet.annotation.Control;
import central.pluglet.control.ControlType;
import central.lang.Arrayx;
import central.lang.Assertx;
import central.util.Objectx;
import central.lang.Stringx;

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
                .map(it -> (OptionalEnum<String>)Assertx.requireInstanceOf(OptionalEnum.class, it, "Enum '{}' MUST implements Optional<String>"))
                .toList();

        var name = Objectx.getOrDefault(annotation.name(), field.getName());
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
