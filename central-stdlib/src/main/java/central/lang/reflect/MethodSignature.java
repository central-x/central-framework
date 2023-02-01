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

import central.bean.Nullable;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 方法签名
 *
 * @author Alan Yeh
 * @since 2023/05/01
 */
public class MethodSignature {
    /**
     * 签名
     */
    @Getter
    private final String signature;

    public MethodSignature(Method method) {
        this.signature = method.getReturnType().getName() + " " + method.getName() + "(" +
                Arrays.stream(method.getParameterTypes()).map(Class::getName).collect(Collectors.joining(", "))
                + ")";
    }

    public MethodSignature(String name, Class<?> returnType, Class<?>... parameterTypes) {
        this.signature = returnType.getName() + " " + name + "(" +
                Arrays.stream(parameterTypes).map(Class::getName).collect(Collectors.joining(", "))
                + ")";
    }

    /**
     * 根据指定方法创建方法参数
     *
     * @param method 方法
     * @return 方法参数
     */
    public static MethodSignature of(Method method) {
        return new MethodSignature(method);
    }

    /**
     * 根据指定类型下的指定方法创建方法签名
     *
     * @param type           类型
     * @param name           方法名
     * @param parameterTypes 参数类型
     * @return 方法签名
     * @throws NoSuchMethodException 找不到指定的方法
     */
    public static MethodSignature of(Class<?> type, String name, Class<?>... parameterTypes) throws NoSuchMethodException {
        return new MethodSignature(type.getMethod(name, parameterTypes));
    }

    /**
     * 创建方法签名
     *
     * @param name           方法名
     * @param returnType     返回类型
     * @param parameterTypes 参数类型
     * @return 方法签名
     */
    public static MethodSignature of(String name, Class<?> returnType, Class<?>... parameterTypes) {
        return new MethodSignature(name, returnType, parameterTypes);
    }

    /**
     * 查找指定类型与本方法签名相符的方法
     *
     * @param type 类型
     * @return 方法
     */
    public @Nullable Method findMethod(@NotNull Class<?> type) {
        var methods = type.getDeclaredMethods();
        for (var method : methods) {
            var signature = MethodSignature.of(method);
            if (Objects.equals(this.getSignature(), signature.getSignature())) {
                return method;
            }
        }
        return null;
    }

    /**
     * 查找指定类型与本方法签名相符的方法
     *
     * @param type     类型
     * @param modifier 方法修饰
     * @return 方法
     */
    public @Nullable Method findMethod(@Nonnull Class<?> type, int modifier) {
        var methods = type.getDeclaredMethods();
        for (var method : methods) {
            if ((modifier & method.getModifiers()) == 0) {
                continue;
            }

            var signature = MethodSignature.of(method);
            if (Objects.equals(this.getSignature(), signature.getSignature())) {
                return method;
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof MethodSignature other) {
            return Objects.equals(this.signature, other.signature);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.signature);
    }
}
