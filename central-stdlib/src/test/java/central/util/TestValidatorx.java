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

package central.util;

import central.validation.Label;
import central.validation.Validatorx;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Validatorx Test Cases
 *
 * @author Alan Yeh
 * @since 2022/08/04
 */
public class TestValidatorx {

    @Data
    public static class Bean {
        @Label("姓名")
        @NotBlank
        private String name;

        @Label("年龄")
        @Max(value = 100)
        private Integer age;

        @Valid
        @Label("子")
        @NotNull
        private SubBean bean;
    }

    public static class SubBean {
        @Label("姓名")
        @NotBlank
        private String name;
    }

    @Test
    public void case1() {
        var bean = new Bean();
        bean.setAge(120);
        bean.setBean(new SubBean());

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Validatorx.Default().validate(bean);
        });
    }
}
