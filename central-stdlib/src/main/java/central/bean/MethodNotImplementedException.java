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
import central.lang.reflect.MethodReference;
import central.util.Stringx;
import lombok.experimental.StandardException;

import java.io.Serial;
import java.lang.reflect.Method;

/**
 * 方法未实现异常
 *
 * @author Alan Yeh
 * @since 2022/07/12
 */
@PublicApi
@StandardException
public class MethodNotImplementedException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -7315730828175305310L;

    public MethodNotImplementedException() {
        super();
    }

    public MethodNotImplementedException(Method method) {
        super(Stringx.format("Method '{}#{}' not implemented", method.getDeclaringClass().getName(), method.getName()));
    }

    public MethodNotImplementedException(Class<?> clazz, String method) {
        super(Stringx.format("Method '{}#{}' not implemented", clazz.getName(), method));
    }

    public MethodNotImplementedException(MethodReference reference) {
        super(Stringx.format("Method '{}#{}' not implemented", reference.getMethod().getDeclaringClass().getName(), reference.getMethod().getName()));
    }
}
