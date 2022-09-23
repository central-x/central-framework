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

package central.validation;

import org.hibernate.validator.internal.engine.MessageInterpolatorContext;
import org.hibernate.validator.internal.engine.messageinterpolation.InterpolationTerm;
import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.hibernate.validator.resourceloading.PlatformResourceBundleLocator;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

/**
 * 错误信息修改
 *
 * @author Alan Yeh
 * @since 2022/08/05
 */
public class MessageInterpolator extends ResourceBundleMessageInterpolator {

    public MessageInterpolator() {
//        Setx.of(Locale.forLanguageTag("zh"), Locale.forLanguageTag("zh_CN"), Locale.forLanguageTag("en")),
        super(new PlatformResourceBundleLocator("i18n.ValidationMessages", MessageInterpolator.class.getClassLoader()));
    }

    @Override
    protected String interpolate(Context context, Locale locale, String term) {
        if (InterpolationTerm.isElExpression(term)) {
            if (context instanceof MessageInterpolatorContext ctx) {
                if (Objects.equals("${property}", term)) {
                    return ctx.getPropertyPath().toString();
                }
                if (Objects.equals("${label}", term)) {
                    return this.getLabelByPath(ctx.getRootBeanType(), ctx.getPropertyPath().toString().split("\\."));
                }
            }
        }

        return super.interpolate(context, locale, term);
    }

    protected String getLabelByPath(Class<?> type, String[] paths) {
        Field field;
        try {
            field = type.getDeclaredField(paths[0]);
        } catch (NoSuchFieldException ex) {
            return null;
        }

        if (paths.length > 1) {
            return getLabelByPath(field.getType(), Arrays.copyOfRange(paths, 1, paths.length));
        } else {
            var label = field.getAnnotation(Label.class);
            if (label == null) {
                return null;
            }
            return label.value();
        }
    }
}
