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

import central.lang.Assertx;
import central.pattern.chain.Processor;
import central.starter.template.core.TemplateRender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * 缓存处理
 *
 * @author Alan Yeh
 * @since 2022/11/15
 */
public abstract class CacheProcessor implements Processor<MethodInvocation, Object> {

    @Setter(onMethod_ = @Autowired)
    private TemplateRender render;

    /**
     * 构建表达式 Boolean 结果
     *
     * @param expression 表达式
     * @param invocation 调用过程
     * @param result     调用结果
     * @return Boolean 值
     */
    protected Boolean evaluateBoolean(String expression, MethodInvocation invocation, Object result) {
        var value = this.evaluate(expression, invocation, result);
        return "true".equalsIgnoreCase(value);
    }

    /**
     * 构建表达式结果
     *
     * @param expression
     * @param invocation
     * @param result
     * @return
     */
    protected String evaluate(String expression, MethodInvocation invocation, Object result) {
        var params = new HashMap<String, Object>();
        params.put("args", invocation.getArguments());
        params.put("method", invocation.getMethod());
        params.put("target", invocation.getThis());
        if (result != null) {
            params.put("result", result);
        }

        try {
            // 计算表达式
            return render.render(expression, params);
        } catch (Throwable throwable) {
            throw new RuntimeException("计算表达式出错，请检查表达式是否正确: " + expression, throwable);
        }
    }

    /**
     * 构建表达式枚举键
     *
     * @param expression
     * @param invocation
     * @param result
     * @param iterableExpression
     * @return
     */
    protected List<String> evaluateKeys(String expression, MethodInvocation invocation, Object result, String iterableExpression) {
        var params = new HashMap<String, Object>();
        params.put("args", invocation.getArguments());
        params.put("method", invocation.getMethod());
        params.put("target", invocation.getThis());
        if (result != null) {
            params.put("result", result);
        }

        try {
            var keys = new ArrayList<String>();

            // 计算表达式
            var iterableObj = this.evaluateIterableObject(iterableExpression, invocation, result);
            Assertx.mustTrue(iterableObj instanceof Collection<?> || iterableObj.getClass().isArray(),
                    "{} 不是一个可以被枚举的类型(Array/Collection)", expression);

            Object[] iterable;
            if (iterableObj instanceof Collection<?> collection) {
                iterable = collection.toArray();
            } else {
                iterable = (Object[]) iterableObj;
            }

            // 计算表达式
            for (var it : iterable) {
                params.put("it", it);
                var key = render.render(expression, params);
                keys.add(key);
            }
            return keys;
        } catch (Throwable throwable) {
            throw new RuntimeException("计算表达式出错，请检查表达式是否正确: " + expression, throwable);
        }
    }

    private Object evaluateIterableObject(String expression, MethodInvocation invocation, Object result) {
        // 通过 SpEL 表达式获取待枚举对象
        var parser = new SpelExpressionParser();

        // SpEL 上下文
        var context = new SpELContext(invocation.getArguments(), result, invocation.getMethod(), invocation.getThis());

        // SqEL 上下文
        var ctx = new StandardEvaluationContext(context);

        try {
            // 计算表达式
            return parser.parseExpression(expression).getValue(ctx);
        } catch (Exception ex) {
            throw new RuntimeException("计算 SpEL 表达式出错: " + expression, ex);
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class SpELContext {
        // 方法执行参数
        private Object[] args;
        // 方法执行结果
        private Object result;
        // 被执行的方法
        private Method method;
        // 被执行的对象
        private Object target;
    }
}
