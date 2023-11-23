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

package central.sql.impl.standard;

import central.sql.SqlConverter;
import central.util.Convertx;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * 标准 Sql Converter
 *
 * @author Alan Yeh
 * @since 2022/09/14
 */
public class StandardConverter implements SqlConverter {

    private final Convertx converter;

    public StandardConverter(Convertx converter) {
        this.converter = converter;
    }

    public StandardConverter() {
        this.converter = Convertx.Default();
    }

    @Override
    public boolean support(@Nonnull Class<?> source, @Nonnull Class<?> target) {
        return converter.support(source, target);
    }

    @Override
    public <T> T convert(@Nullable Object source, @Nonnull Class<T> target) {
        return converter.convert(source, target);
    }
}
