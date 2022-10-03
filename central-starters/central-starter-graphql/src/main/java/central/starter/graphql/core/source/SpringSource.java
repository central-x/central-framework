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

package central.starter.graphql.core.source;

import central.lang.Assertx;
import central.lang.Stringx;
import central.util.Context;
import org.springframework.context.ApplicationContext;

/**
 * Spring 动态源
 *
 * @author Alan Yeh
 * @since 2022/10/03
 */
public class SpringSource implements Source {
    private final String qualifier;
    private final Class<?> requiredType;

    private Object bean;

    public SpringSource(String qualifier, Class<?> requiredType) {
        Assertx.mustTrue(Stringx.isNotBlank(qualifier) || requiredType != null, "参数[qualifier]与参数[requiredType]不能同时为空");
        this.qualifier = qualifier;
        this.requiredType = requiredType;
    }

    public static SpringSource of(String qualifier) {
        return new SpringSource(qualifier, null);
    }

    public static SpringSource of(Class<?> type) {
        return new SpringSource(null, type);
    }

    public static SpringSource of(String qualifier, Class<?> requiredType) {
        return new SpringSource(qualifier, requiredType);
    }

    @Override
    public Object getSource(Context context) {
        if (bean == null) {
            var applicationContext = context.require(ApplicationContext.class);
            if (Stringx.isNotBlank(this.qualifier) && this.requiredType != null) {
                bean = applicationContext.getBean(this.qualifier, this.requiredType);
            } else if (Stringx.isNotBlank(this.qualifier)) {
                bean = applicationContext.getBean(this.qualifier);
            } else if (this.requiredType != null) {
                bean = applicationContext.getBean(requiredType);
            }
        }

        return bean;
    }
}
