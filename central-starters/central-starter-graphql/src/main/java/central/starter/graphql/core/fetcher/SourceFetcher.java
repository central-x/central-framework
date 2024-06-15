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

package central.starter.graphql.core.fetcher;

import central.lang.Stringx;
import central.lang.reflect.invoke.Invocation;
import central.lang.reflect.invoke.ParameterResolver;
import central.starter.graphql.core.ExceptionHandleChain;
import central.starter.graphql.core.source.Source;
import central.util.Context;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 静态源
 *
 * @author Alan Yeh
 * @since 2022/10/01
 */
public class SourceFetcher implements DataFetcher<Object> {

    /**
     * 命令名
     */
    @Getter
    private final String name;

    /**
     * 命令所在对象
     */
    private final Source source;

    /**
     * 命令对应的方法
     */
    private final Method method;

    /**
     * 异常处理链
     */
    private final ExceptionHandleChain handler;

    @Setter
    private List<ParameterResolver> resolvers = new ArrayList<>();

    private SourceFetcher(Source source, String name, Method method, ExceptionHandleChain handler) {
        this.source = source;
        this.name = name;
        this.method = method;
        this.handler = handler;
    }

    public static SourceFetcher of(Source source, Method method, ExceptionHandleChain handler) {
        return new SourceFetcher(source, method.getName(), method, handler);
    }

    public static SourceFetcher ofGetter(Source source, Method method, ExceptionHandleChain handler) {
        return new SourceFetcher(source, Stringx.lowerCaseFirstLetter(Stringx.removePrefix(method.getName(), "get")), method, handler);
    }

    @Override
    public Object get(DataFetchingEnvironment environment) throws Exception {
        var context = Context.clone(environment.getLocalContext());
        context.set(DataFetchingEnvironment.class, environment);
        try {
            return Invocation.of(this.method).resolvers(this.resolvers).invoke(this.source.getSource(context), context);
        } catch (InvocationTargetException | IllegalAccessException throwable) {
            throw handler.handle(this.method, throwable);
        }
    }
}
