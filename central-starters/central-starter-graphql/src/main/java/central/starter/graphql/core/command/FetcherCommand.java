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

package central.starter.graphql.core.command;

import central.starter.graphql.GraphQLParameterResolver;
import central.starter.graphql.GraphQLService;
import central.util.Listx;
import central.lang.Stringx;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;

/**
 * 查询命令
 *
 * @author Alan Yeh
 * @since 2022/09/09
 */
public class FetcherCommand implements DataFetcher<Object> {

    /**
     * 命令名
     */
    @Getter
    private final String name;

    /**
     * 命令所在对象
     */
    private final Object target;

    /**
     * 命令对应的方法
     */
    private final Method method;

    /**
     * 命令参数
     */
    private final Parameter[] parameters;

    @Setter
    private List<GraphQLParameterResolver> resolvers = Listx.newArrayList();

    public FetcherCommand(Object target, Method method) {
        var service = (GraphQLService) target;
        this.name = service.getNamespace() + "_" + method.getName();
        this.target = target;
        this.method = method;
        this.parameters = method.getParameters();
    }

    @Override
    public Object get(DataFetchingEnvironment environment) throws Exception {
        // 构造调用参数
        Object[] args = new Object[this.parameters.length];

        for (int i = 0; i < this.parameters.length; i++) {
            Parameter parameter = this.parameters[i];
            for (GraphQLParameterResolver resolver : this.resolvers) {
                if (resolver.support(parameter)) {
                    args[i] = resolver.resolve(this.method, parameter, environment);
                }
            }
        }

        // 调用命令
        try {
            return this.method.invoke(this.target, args);
        } catch (InvocationTargetException | IllegalAccessException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, Stringx.format("执行 {}.{} 出现异常: " + ex.getCause().getLocalizedMessage(), this.method.getDeclaringClass().getSimpleName(), this.method.getName()), ex.getCause());
        }
    }
}
