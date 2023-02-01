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

package central.lang.reflect;

import central.lang.Stringx;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Function;

/**
 * Property Reference
 * 属性引用
 *
 * @author Alan Yeh
 * @since 2022/07/13
 */
@FunctionalInterface
public interface PropertyRef<T, R> extends Function<T, R>, Serializable {
    /**
     * 根据属性名
     */
    default String getPropertyName() {
        try {
            Method method = this.getClass().getDeclaredMethod("writeReplace");
            method.setAccessible(true);
            SerializedLambda lambda = (SerializedLambda) method.invoke(this);

            String getter = lambda.getImplMethodName();
            return Stringx.lowerCaseFirstLetter(Stringx.removePrefix(Stringx.removePrefix(getter, "get"), "is"));
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
            throw new RuntimeException("[PropertyReference] 解析属性异常", ex);
        }
    }
}
