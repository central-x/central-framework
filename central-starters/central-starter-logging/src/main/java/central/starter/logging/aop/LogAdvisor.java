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

package central.starter.logging.aop;

import central.lang.Stringx;
import central.starter.logging.aop.annotation.LogPoint;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.core.Ordered;

import java.io.Serial;
import java.lang.reflect.Method;

/**
 * 日志切面
 *
 * @author Alan Yeh
 * @since 2023/02/05
 */
@Slf4j
public class LogAdvisor extends AbstractPointcutAdvisor implements MethodInterceptor, Ordered {
    @Serial
    private static final long serialVersionUID = 6679820548345187163L;

    @Getter
    private final int order;

    public LogAdvisor(Integer order) {
        this.order = order;
    }


    @Nullable
    @Override
    public Object invoke(@NotNull MethodInvocation invocation) throws Throwable {
        var point = invocation.getMethod().getAnnotation(LogPoint.class);
        StringBuilder message = new StringBuilder(Stringx.format("{}.{}", invocation.getMethod().getDeclaringClass().getName(), invocation.getMethod().getName()));

        switch (point.level()) {
            case INFO -> log.info(message.toString());
            case WARN -> log.warn(message.toString());
            case DEBUG -> log.debug(message.toString());
            case ERROR -> log.error(message.toString());
            case TRACE -> log.trace(message.toString());
        }

        return invocation.proceed();
    }


    @Override
    public Advice getAdvice() {
        return this;
    }

    @Getter
    private final Pointcut pointcut = new StaticMethodMatcherPointcut() {
        @Override
        public boolean matches(Method method, Class<?> targetClass) {
            return method.isAnnotationPresent(LogPoint.class);
        }
    };
}
