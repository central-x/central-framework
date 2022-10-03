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

package central.starter.graphql;

import central.lang.reflect.invoke.ParameterResolver;
import graphql.schema.GraphQLScalarType;

import java.util.Collections;
import java.util.List;

/**
 * GraphQL 配置
 *
 * @author Alan Yeh
 * @since 2022/09/09
 */
public interface GraphQLConfigurer {
    /**
     * 添加标量
     */
    default List<GraphQLScalarType> getScalars() {
        return Collections.emptyList();
    }

    /**
     * 添加参数解析器
     */
    default List<ParameterResolver> getParameterResolvers() {
        return Collections.emptyList();
    }

    /**
     * 获取 Query 对象
     */
    Object getQuery();

    /**
     * 获取 Mutation 对象
     */
    Object getMutation();
}
