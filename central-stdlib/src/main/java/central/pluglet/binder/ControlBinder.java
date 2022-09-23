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

import central.bean.InitializeException;
import central.lang.Arrayx;
import central.lang.Stringx;
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
            value = Arrayx.getFirst(annotation.defaultValue());
        }
        if (value != null) {
            if (!Convertx.Default().support(value.getClass(), field.getType().getRawClass())) {
                throw new InitializeException(target.getType().getRawClass(), Stringx.format("Cannot convert '{}' to '{}'", value, field.getType().getRawClass().getName()));
            }
            value = Convertx.Default().convert(value, field.getType().getRawClass());
            field.setValue(target, value);
        }
    }
}
