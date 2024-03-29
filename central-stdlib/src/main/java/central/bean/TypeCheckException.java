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

import central.lang.PublicApi;
import central.lang.Stringx;

import java.io.Serial;

/**
 * 类型检查异常
 *
 * @author Alan Yeh
 * @since 2022/07/12
 */
@PublicApi
public class TypeCheckException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 7212345137343048200L;

    public TypeCheckException(String message) {
        super(message);
    }

    public TypeCheckException(String message, Throwable cause) {
        super(message, cause);
    }

    public TypeCheckException(Throwable cause) {
        super(cause);
    }

    public static void asserts(Class<?> expected, Class<?> actual) {
        if (actual.isAssignableFrom(expected)) {
            throw new TypeCheckException(Stringx.format("Type checked failed: Required type '{}' but provides '{}'", expected.getName(), actual.getName()));
        }
    }
}
