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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;
import java.io.Serial;
import java.io.Serializable;

/**
 * Value
 *
 * @author Alan Yeh
 * @since 2022/07/17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Value<T extends Serializable> implements Serializable {
    @Serial
    private static final long serialVersionUID = 3043802081813135212L;

    @Nullable
    private volatile T value;

    /**
     * 获取当前的值
     */
    @Nullable
    public T get() {
        return this.value;
    }

    /**
     * 获取值，如果当前的值为空，则返回 other
     *
     * @param other 另一个值
     */
    public T get(T other) {
        return isNotNull() ? value : other;
    }

    /**
     * 判断当前的值是否为 null
     */
    public boolean isNull() {
        return this.value == null;
    }

    /**
     * 判断当前的值是否不为 null
     */
    public boolean isNotNull() {
        return this.value != null;
    }

    /**
     * 快速创建 Value 对象
     *
     * @param value 值
     * @param <T>   值类型
     */
    public static <T extends Serializable> Value<T> of(@Nullable T value) {
        return new Value<>(value);
    }
}
