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

package central.bean;

import central.lang.Arrayx;
import central.lang.PublicApi;

import java.util.Objects;

/**
 * Optional Enum
 *
 * @author Alan Yeh
 * @since 2022/07/11
 */
@PublicApi
public interface OptionalEnum<V> {
    /**
     * 选项名
     */
    String getName();

    /**
     * 选项值
     */
    V getValue();

    /**
     * 判断当前选项与指定选项值是否匹配
     *
     * @param value 指定选项值
     */
    default boolean isCompatibleWith(Object value) {
        if (value == null) {
            return false;
        }
        // 如果两个都是 OptionalEnum，且对方与当前是同一个类，则对比其值
        if (value.getClass().isAssignableFrom(this.getClass())) {
            return Objects.equals(this, value);
        }
        // 对比当前选项的值是否相等
        return Objects.equals(this.getValue(), value);
    }

    /**
     * 查找与指定值相等的选项
     */
    static <T extends OptionalEnum<?>> T resolve(Class<? extends OptionalEnum<?>> type, Object value) {
        return (T) Arrayx.asStream(type.getEnumConstants())
                .filter(it -> Objects.equals(it.getValue(), value))
                .findFirst().orElse(null);
    }
}
