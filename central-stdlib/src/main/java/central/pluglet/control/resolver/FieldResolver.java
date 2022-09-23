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

import central.lang.reflect.FieldReference;
import central.pluglet.control.ControlResolver;
import central.pluglet.annotation.Control;
import central.pluglet.control.ControlType;
import central.pluglet.control.PlugletControl;
import central.lang.Arrayx;
import central.util.Convertx;
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
                .defaultValue(Convertx.Default().convert(Arrayx.getFirst(annotation.defaultValue()), field.getType().getRawClass()))
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
