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
import central.lang.reflect.FieldRef;
import central.lang.reflect.InstanceRef;
import central.pluglet.FieldBinder;
import central.lang.Stringx;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import java.util.Map;

/**
 * Spring Bean
 *
 * @author Alan Yeh
 * @since 2022/07/11
 */
public class SpringBeanFieldBinder implements FieldBinder {
    private final ApplicationContext applicationContext;

    public SpringBeanFieldBinder(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public boolean support(FieldRef field) {
        if (ApplicationContext.class == field.getType().getRawClass()) {
            return true;
        } else if (Environment.class == field.getType().getRawClass()) {
            return true;
        } else {
            return field.getAnnotation(Autowired.class) != null || field.getAnnotation(Qualifier.class) != null;
        }
    }

    @Override
    public void bind(InstanceRef<?> target, FieldRef field, Map<String, Object> params) {
        var autowired = field.getAnnotation(Autowired.class);
        var qualifier = field.getAnnotation(Qualifier.class);

        String name = null;
        if (qualifier != null && Stringx.isNotBlank(qualifier.value())) {
            name = qualifier.value();
        }

        Object bean = null;
        try {
            if (ApplicationContext.class == field.getType().getRawClass()) {
                bean = this.applicationContext;
            } else if (Environment.class == field.getType().getRawClass()) {
                bean = this.applicationContext.getEnvironment();
            } else if (Stringx.isNotBlank(name)) {
                bean = this.applicationContext.getBean(name);
            } else {
                bean = this.applicationContext.getBean(field.getType().getRawClass());
            }
        } catch (BeansException ignored) {
        }

        if (bean == null) {
            if (autowired == null || autowired.required()) {
                if (Stringx.isNullOrBlank(name)) {
                    throw new InitializeException(target.getType().getRawClass(), Stringx.format("Cannot find specific bean(name='{}')", name));
                } else {
                    throw new InitializeException(target.getType().getRawClass(), Stringx.format("Cannot find specific bean(type='{}')", field.getType()));
                }
            }
        } else {
            field.setValue(target, bean);
        }
    }
}
