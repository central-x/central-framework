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

package central.lang.reflect.invoke;

import central.util.Context;
import central.util.Objectx;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 方法调用
 *
 * @author Alan Yeh
 * @since 2022/10/01
 */
public class Invocation {
    /**
     * 类
     */
    private final Class<?> clazz;

    /**
     * 方法
     */
    private final Method method;

    /**
     * 参数
     */
    private final Parameter[] parameters;

    /**
     * 参数解析器
     */
    private final List<ParameterResolver> resolvers = new ArrayList<>();

    private Invocation(Method method) {
        this.clazz = method.getDeclaringClass();
        this.method = method;
        this.parameters = method.getParameters();
    }

    /**
     * 快速构建
     *
     * @param method 待调用方法
     */
    public static Invocation of(Method method) {
        return new Invocation(method);
    }

    /**
     * 添加参数解析器
     *
     * @param resolvers 参数解析器
     */
    public Invocation resolvers(List<ParameterResolver> resolvers) {
        this.resolvers.addAll(Objectx.get(resolvers, Collections.emptyList()));
        this.resolvers.sort(Comparator.comparing(ParameterResolver::getOrder));
        return this;
    }

    /**
     * 调用方法
     * 参数解析器会从调用上下文中解析需要的参数
     *
     * @param source  待调用对象
     * @param context 调用上下文
     * @return 调用结果
     */
    public Object invoke(Object source, Context context) throws InvocationTargetException, IllegalAccessException {
        // 构建调用参数列表
        Object[] args = new Object[this.parameters.length];

        for (int i = 0; i < this.parameters.length; i++) {
            var parameter = this.parameters[i];
            for (var resolver : this.resolvers) {
                if (resolver.support(this.clazz, this.method, parameter)) {
                    args[i] = resolver.resolve(this.clazz, this.method, parameter, context);
                }
            }
        }

        return this.method.invoke(source, args);
    }
}
