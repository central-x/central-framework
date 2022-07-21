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

import java.util.UUID;

/**
 * 随机主键生成
 *
 * @author Alan Yeh
 * @since 2022/07/05
 */
public class Guidx {
    private static String digits(long val, int digits) {
        var hi = 1L << (digits * 4);
        return Numberx.toString(hi | (val & (hi - 1)), Numberx.MAX_RADIX)
                .substring(1);
    }

    public static String nextID() {
        var uuid = UUID.randomUUID();

        var result = new StringBuilder();

        result.append(digits(uuid.getMostSignificantBits() >> 32, 8));
        result.append(digits(uuid.getMostSignificantBits() >> 16, 4));
        result.append(digits(uuid.getMostSignificantBits(), 4));
        result.append(digits(uuid.getLeastSignificantBits() >> 48, 4));
        result.append(digits(uuid.getLeastSignificantBits(), 12));

        return result.toString();
    }
}
