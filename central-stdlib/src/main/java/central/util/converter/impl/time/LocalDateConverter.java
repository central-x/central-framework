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

package central.util.converter.impl.time;

import central.util.converter.ConvertException;
import central.util.converter.Converter;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * LocalDate Converter
 *
 * @author Alan Yeh
 * @since 2022/09/16
 */
public class LocalDateConverter implements Converter<LocalDate> {
    @Override
    public boolean support(Class<?> source) {
        if (LocalDate.class.isAssignableFrom(source)) {
            return true;
        } else if (Number.class.isAssignableFrom(source)) {
            return true;
        }
        return false;
    }

    @Override
    public LocalDate convert(Object source) {
        if (source instanceof LocalDate l) {
            return l;
        } else if (source instanceof Number n) {
            return LocalDate.ofInstant(new Date(n.longValue()).toInstant(), ZoneId.systemDefault());
        }

        throw new ConvertException(source, LocalDate.class);
    }
}
