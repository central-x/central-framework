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

package central.sql.conversion;

import central.sql.SqlConversion;
import central.sql.data.Entity;
import central.util.Stringx;

import java.lang.reflect.Field;

/**
 * 下划线命名
 *
 * @author Alan Yeh
 * @since 2022/08/05
 */
public class UnderlineConversion implements SqlConversion {
    @Override
    public String getTableName(Class<? extends Entity> type) {
        var result = new StringBuilder();
        var chars = Stringx.lowerCaseFirstLetter(type.getSimpleName()).toCharArray();

        for (char c : chars) {
            if (Character.isUpperCase(c)) {
                result.append("_");
            }
            result.append(Character.toUpperCase(c));
        }

        return result.toString();
    }

    @Override
    public String getColumnName(String property) {
        var result = new StringBuilder();
        var chars = property.toCharArray();

        for (char c : chars) {
            if (Character.isUpperCase(c)) {
                result.append("_");
            }
            result.append(Character.toUpperCase(c));
        }

        return result.toString();
    }

    @Override
    public String getPropertyName(String column) {
        var result = new StringBuilder();
        var chars = column.toCharArray();

        boolean nextUpperCase = false;
        for (char c : chars) {
            if (c == '_') {
                nextUpperCase = true;
                continue;
            }
            if (nextUpperCase) {
                result.append(Character.toUpperCase(c));
            } else {
                result.append(Character.toLowerCase(c));
            }
        }
        return result.toString();
    }
}
