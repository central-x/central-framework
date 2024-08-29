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

package central.validation.validator;

import central.bean.OptionalEnum;
import central.validation.Enums;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.Collection;

/**
 * 枚举校验器
 *
 * @author Alan Yeh
 * @since 2022/11/07
 */
public class EnumsValidator implements ConstraintValidator<Enums, Object> {

    private Class<? extends OptionalEnum<?>> enumType;

    @Override
    public void initialize(Enums constraintAnnotation) {
        this.enumType = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        // 待校验的枚举常量
        var constants = enumType.getEnumConstants();

        if (value instanceof Collection<?> collection) {
            // 校验集合里面的元素
            for (var item : collection) {
                if (Arrays.stream(constants).noneMatch(constant -> constant.isCompatibleWith(item))) {
                    return false;
                }
            }
            return true;
        } else {
            return Arrays.stream(constants).anyMatch(constant -> constant.isCompatibleWith(value));
        }
    }
}
