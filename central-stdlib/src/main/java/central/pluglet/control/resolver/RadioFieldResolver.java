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

package central.pluglet.control.resolver;

import central.bean.TypeCheckException;
import central.bean.NameValue;
import central.bean.OptionalEnum;
import central.lang.Stringx;
import central.lang.reflect.FieldRef;
import central.pluglet.annotation.Control;
import central.pluglet.control.ControlType;
import central.pluglet.control.PlugletControl;
import central.lang.Arrayx;
import central.lang.Assertx;
import central.util.Objectx;


/**
 * Radio Control
 *
 * @author Alan Yeh
 * @since 2022/07/11
 */
public class RadioFieldResolver extends FieldResolver {
    @Override
    public boolean support(FieldRef field) {
        var annotation = field.getAnnotation(Control.class);
        return annotation != null && ControlType.RADIO == annotation.type();
    }

    @Override
    public PlugletControl resolve(FieldRef field) {
        var annotation = field.getAnnotation(Control.class);

        // 类型检查
        Assertx.mustTrue(field.getType().isEnum(), TypeCheckException::new, "Field '{}' requires Enum type", field.getName());
        // 枚举列表
        var options = Arrayx.asStream(field.getType().getRawClass().getEnumConstants())
                .map(it -> (OptionalEnum<String>)Assertx.requireInstanceOf(OptionalEnum.class, it, () -> new TypeCheckException(Stringx.format("Enum '{}' MUST implements Optional<String>", it.getClass().getSimpleName()))))
                .map(it -> new NameValue<>(it.getName(), it.getValue()))
                .toList();

        return PlugletControl.builder()
                .label(annotation.label())
                .name(Objectx.getOrDefault(annotation.name(), field.getName()))
                .type(annotation.type().name())
                .comment(annotation.comment())
                .defaultValue(Arrayx.getFirstOrNull(annotation.defaultValue()))
                .required(annotation.required())
                .options(options)
                .build();
    }
}
