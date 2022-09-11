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

import graphql.schema.DataFetchingEnvironment;
import org.dataloader.BatchLoaderEnvironment;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;

/**
 * 参数处理
 * 在调用 GraphQL 服务或 Getter 之类时的参入注入处理器
 *
 * @author Alan Yeh
 * @since 2022/09/09
 */
public interface GraphQLParameterResolver {

    boolean support(Parameter parameter);

    /**
     * 处理参数
     *
     * @param method      被调用的方法
     * @param parameter   参数信息
     * @param environment 调用环境信息
     * @return 处理后的参数值
     */
    Object resolve(Method method, Parameter parameter, DataFetchingEnvironment environment);

    /**
     * 处理 BatchLoader 参数
     *
     * @param method      被调用的方法
     * @param parameter   参数信息
     * @param keys        Batch Keys
     * @param environment 调用环境信息
     * @return 处理后的参数
     */
    Object resolve(Method method, Parameter parameter, List<String> keys, BatchLoaderEnvironment environment);
}
