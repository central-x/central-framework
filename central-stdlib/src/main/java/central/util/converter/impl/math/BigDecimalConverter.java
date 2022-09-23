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

package central.util.converter.impl.math;

import central.util.converter.ConvertException;
import central.util.converter.Converter;

import java.math.BigDecimal;

/**
 * BigDecimal Converter
 *
 * @author Alan Yeh
 * @since 2022/09/16
 */
public class BigDecimalConverter implements Converter<BigDecimal> {
    @Override
    public boolean support(Class<?> source) {
        if (BigDecimal.class.isAssignableFrom(source)) {
            return true;
        } else if (Number.class.isAssignableFrom(source)) {
            return true;
        } else if (String.class.isAssignableFrom(source)) {
            return true;
        }
        return false;
    }

    @Override
    public BigDecimal convert(Object source) {
        if (source instanceof BigDecimal b) {
            return b;
        } else if (source instanceof Double d) {
            return BigDecimal.valueOf(d);
        } else if (source instanceof Float f) {
            return BigDecimal.valueOf(f.doubleValue());
        } else if (source instanceof Number n) {
            return BigDecimal.valueOf(n.longValue());
        } else if (source instanceof String s) {
            return new BigDecimal(s);
        }

        throw new ConvertException(source, BigDecimal.class);
    }
}
