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

import central.bean.BeanValidateException;
import central.lang.Arrayx;
import central.util.Setx;
import jakarta.validation.*;
import jakarta.validation.groups.Default;
import org.hibernate.validator.HibernateValidator;

import java.util.function.Function;

/**
 * 参数校验工具
 *
 * @author Alan Yeh
 * @since 2022/08/04
 */
public class Validatex {

    private final ValidatorFactory factory;

    private Validatex() {
        this.factory = Validation.byProvider(HibernateValidator.class)
                .configure()
                .messageInterpolator(new MessageInterpolator())
                .buildValidatorFactory();
    }

    private static final Validatex INSTANCE = new Validatex();

    public static Validatex Default() {
        return INSTANCE;
    }

    public <T, E extends Throwable> void validate(T object, Class<?>[] groups, Function<? super String, ? extends E> error) throws E {
        if (Arrayx.isNullOrEmpty(groups)) {
            groups = new Class[]{Default.class};
        }
        var violations = this.factory.getValidator().validate(object, groups);
        if (Setx.isNotEmpty(violations)) {
            throw error.apply(Setx.getAny(violations).getMessage());
        }
    }

    /**
     * 校验 Bean
     *
     * @param object 待校验对象
     * @param groups 校验分组
     */
    public <T> void validateBean(T object, Class<?>... groups) throws BeanValidateException {
        this.validate(object, groups, BeanValidateException::new);
    }

    /**
     * 校验参数
     */
    public <T> void validate(T object, Class<?>... groups) throws IllegalArgumentException {
        this.validate(object, groups, IllegalArgumentException::new);
    }
}
