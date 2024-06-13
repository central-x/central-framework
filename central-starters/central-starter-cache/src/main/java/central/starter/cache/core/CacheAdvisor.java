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

package central.starter.cache.core;

import central.pattern.chain.ProcessChain;
import central.starter.cache.core.annotation.CacheEvict;
import central.starter.cache.core.annotation.CachePut;
import central.starter.cache.core.annotation.Cacheable;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;

import java.io.Serial;
import java.lang.reflect.Method;
import java.util.List;


public class CacheAdvisor extends AbstractPointcutAdvisor implements InitializingBean, MethodInterceptor, Ordered {
    @Serial
    private static final long serialVersionUID = 623201814085892146L;

    /**
     * 切面顺序
     */
    @Getter
    private final int order;

    @Setter(onMethod_ = @Autowired)
    private List<CacheProcessor> handlers;

    public CacheAdvisor(Integer order) {
        this.order = order;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    @Nullable
    @Override
    public Object invoke(@Nonnull MethodInvocation invocation) throws Throwable {
        return new ProcessChain<>(this.handlers).process(invocation);
    }

    @Override
    public @Nonnull Advice getAdvice() {
        return this;
    }

    @Getter
    private final Pointcut pointcut = new StaticMethodMatcherPointcut() {
        @Override
        public boolean matches(@Nonnull Method method, @Nonnull Class<?> targetClass) {
            return method.isAnnotationPresent(Cacheable.class) ||
                    method.isAnnotationPresent(CacheEvict.class) || method.isAnnotationPresent(CacheEvict.List.class) ||
                    method.isAnnotationPresent(CachePut.class);
        }
    };
}
