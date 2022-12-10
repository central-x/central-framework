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

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * 属性
 *
 * @author Alan Yeh
 * @since 2022/07/13
 */
@PublicApi
@RequiredArgsConstructor
public class Attribute<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = -821276198244689067L;

    /**
     * Code
     */
    @Getter
    private final String code;

    /**
     * Value Supplier
     */
    private final Supplier<T> supplier;

    public Attribute(String code) {
        this.code = code;
        this.supplier = null;
    }

    public static <T> Attribute<T> of(String code, T value) {
        return new Attribute<>(code, () -> value);
    }

    public static <T> Attribute<T> of(String code, Supplier<T> value) {
        return new Attribute<>(code, value);
    }

    public static <T> Attribute<T> of(String code) {
        return new Attribute<>(code);
    }

    /**
     * Get value from supplier
     */
    public @Nullable T getValue() {
        if (supplier != null) {
            return supplier.get();
        } else {
            return null;
        }
    }

    /**
     * Get nonnull value from supplier
     */
    public @Nonnull T requireValue() {
        return Assertx.requireNotNull(this.getValue(), IllegalStateException::new, "Cannot return null value form supplier");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Attribute<?> attribute = (Attribute<?>) o;
        return code.equals(attribute.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public String toString() {
        return Stringx.format("Attribute{code: {}}", this.getCode());
    }
}
