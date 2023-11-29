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

package central.pattern.proxy;

import central.lang.Arrayx;
import central.lang.Stringx;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 代理工厂
 *
 * @author Alan Yeh
 * @since 2022/07/20
 */
public class ProxyFactory {

    private final List<ProxyMethod> methods = new ArrayList<>();

    public void addMethod(ProxyMethod method) {
        this.methods.add(method);
    }

    private ProxyMethod fallback = null;

    public void setFallback(ProxyMethod method) {
        this.fallback = method;
    }

    @SuppressWarnings("unchecked")
    public <T> T create(Class<T> target) {
        return (T) Proxy.newProxyInstance(target.getClassLoader(), Arrayx.newArray(target), new ProxyInvocationHandler(Collections.unmodifiableList(this.methods), fallback));
    }

    private static class ProxyInvocationHandler implements InvocationHandler {

        private final List<ProxyMethod> methods;

        private final ProxyMethod fallback;

        public ProxyInvocationHandler(List<ProxyMethod> methods, ProxyMethod fallback) {
            this.methods = methods;
            this.fallback = fallback;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.isDefault()) {
                // interface 的 default 方法
                return InvocationHandler.invokeDefault(proxy, method, args);
            } else {
                // 寻找可以支持的方法
                for (var m : this.methods) {
                    if (m.support(method)) {
                        return m.invoke(proxy, method, args);
                    }
                }

                // 最后的方法
                if (this.fallback != null && this.fallback.support(method)) {
                    return this.fallback.invoke(proxy, method, args);
                }

                throw new NoSuchMethodException(Stringx.format("{}#{}", proxy.getClass().getName(), method.getName()));
            }
        }
    }
}
