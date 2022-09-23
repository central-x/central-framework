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

package central.lang;

import central.bean.OptionalEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Boolean Enum
 * 开关
 *
 * @author Alan Yeh
 * @since 2022/07/13
 */
@Getter
@PublicApi
@AllArgsConstructor
public enum BooleanEnum implements OptionalEnum<String> {

    TRUE("是", "1", true),
    FALSE("否", "0", false);

    private final String name;
    private final String value;
    private final Boolean jValue;

    @Override
    public String toString() {
        return this.value;
    }

    @JsonCreator
    public static BooleanEnum resolve(Object value) {
        if (value instanceof String s) {
            if (Stringx.isNotBlank(s) && ("true".equalsIgnoreCase(s) || "1".equalsIgnoreCase(s))) {
                return BooleanEnum.TRUE;
            } else {
                return BooleanEnum.FALSE;
            }
        } else if (value instanceof Boolean b) {
            return b ? BooleanEnum.TRUE : BooleanEnum.FALSE;
        } else {
            return BooleanEnum.FALSE;
        }
    }

    @Override
    public boolean isCompatibleWith(Object value) {
        if (value instanceof BooleanEnum b) {
            return this.equals(b);
        }
        if (value instanceof Boolean b) {
            return this.getJValue().equals(b);
        }
        if (value instanceof String s) {
            return this.getValue().equals(s);
        }
        return false;
    }
}
