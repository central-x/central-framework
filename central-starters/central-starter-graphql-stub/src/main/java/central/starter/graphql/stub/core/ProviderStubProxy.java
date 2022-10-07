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

package central.starter.graphql.stub.core;

import central.lang.Arrayx;
import central.lang.Assertx;
import central.lang.Stringx;
import central.lang.reflect.TypeReference;
import central.starter.graphql.stub.GraphQLRequest;
import central.starter.graphql.stub.ProviderClient;
import central.starter.graphql.stub.Provider;
import central.starter.graphql.stub.annotation.BodyPath;
import central.util.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ValueConstants;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * GraphQL Proxy
 *
 * @author Alan Yeh
 * @since 2022/09/25
 */
public class ProviderStubProxy implements InvocationHandler {

    private final Class<? extends Provider> stub;

    private final ProviderClient client;

    private final MarkdownResources resources;

    public ProviderStubProxy(Class<? extends Provider> stub, ProviderClient client, MarkdownResources resources) {
        this.stub = stub;
        this.client = client;
        this.resources = resources;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.isDefault()) {
            // default 方法
            return InvocationHandler.invokeDefault(proxy, method, args);
        } else if ("toString".equals(method.getName())) {
            return Stringx.format("{}@{}", stub.getSimpleName(), Integer.toHexString(this.hashCode()));
        }

        // 根据方法名查找 GraphQL 语句
        var resource = resources.get(method.getName());
        Assertx.mustNotNull(resource, IllegalStateException::new, "执行 {}.{} 出错: 找不到对应的 GraphQL 声明", this.stub.getSimpleName(), method.getName());
        var graphql = Assertx.requireNotBlank(resource.getContent(), IllegalStateException::new, "执行 {}.{} 出错: 找不到对应的 GraphQL 声明", this.stub.getSimpleName(), method.getName());

        // 解析参数
        var variables = new HashMap<String, Object>();
        var headers = new LinkedMultiValueMap<String, String>();
        this.resolveParameters(method, args, variables, headers);

        // 执行方法
        var response = this.client.graphql(new GraphQLRequest(graphql, variables), headers);

        // 直接返回字符串
        if (String.class.isAssignableFrom(method.getReturnType())) {
            return response;
        }

        // 解析 Json
        String path = "";
        var classPath = stub.getAnnotation(BodyPath.class);
        if (classPath != null && Stringx.isNotBlank(classPath.value())) {
            path = classPath.value();
        }
        var methodPath = method.getAnnotation(BodyPath.class);
        if (methodPath != null && Stringx.isNotBlank(methodPath.value())) {
            path += ("." + methodPath.value());
        }
        if (Stringx.isNullOrEmpty(path)) {
            var result = Jsonx.Default().deserialize(response, TypeReference.ofMap(String.class, TypeReference.ofMap(String.class, TypeReference.of(method.getAnnotatedReturnType().getType()))));
            return result.getOrDefault(method.getName(), Mapx.newHashMap()).get(method.getName());
        } else {
            var paths = Arrayx.asStream(path.trim().split("[.]")).map(String::trim).filter(Stringx::isNotEmpty).toList();
            TypeReference<?> returnType = TypeReference.ofMap(String.class, TypeReference.of(method.getAnnotatedReturnType().getType()));
            for (int i = 0; i < paths.size(); i++) {
                returnType = TypeReference.ofMap(String.class, returnType);
            }
            var result = Jsonx.Default().deserialize(response, returnType);
            for (int i = 0; i < paths.size(); i++) {
                if (result instanceof Map<?, ?> map) {
                    result = Objectx.getOrDefault(map.get(paths.get(i)), Mapx.newHashMap());
                }
            }
            return ((Map<?, ?>) result).get(method.getName());
        }
    }

    /**
     * 解析参数
     */
    private void resolveParameters(Method method, Object[] args, Map<String, Object> variables, MultiValueMap<String, String> headers) throws Throwable {
        var parameters = method.getParameters();

        for (int i = 0; i < parameters.length; i++) {
            var parameter = parameters[i];
            String name = parameter.getName();

            var header = parameter.getAnnotation(RequestHeader.class);
            if (header != null) {
                // 该参数是请求头
                var value = args[i];
                if (value == null && Stringx.isNotBlank(header.defaultValue()) && !ValueConstants.DEFAULT_NONE.equals(header.defaultValue())) {
                    // 设置默认值
                    value = header.defaultValue();
                }

                if (header.required() && (value == null || Stringx.isNullOrEmpty(value.toString()))) {
                    throw new IllegalArgumentException(Stringx.format("执行 {}.{} 出错: 参数[{}]缺失", this.stub.getSimpleName(), method.getName(), parameter.getName()));
                }

                if (Stringx.isNotBlank(header.value())) {
                    name = header.value();
                }
                if (Stringx.isNotBlank(header.name())) {
                    name = header.name();
                }

                if (value != null && Stringx.isNullOrEmpty(value.toString())) {
                    headers.add(name, value.toString());
                }
                continue;
            }

            var param = parameter.getAnnotation(RequestParam.class);
            if (param != null) {
                var value = args[i];
                if (value == null && Stringx.isNotBlank(param.defaultValue()) && !ValueConstants.DEFAULT_NONE.equals(param.defaultValue())) {
                    if (!parameter.getType().equals(String.class)) {
                        if (!Convertx.Default().support(String.class, parameter.getType())) {
                            throw new IllegalArgumentException(Stringx.format("执行 {}.{} 出错: 无法将 @RequestParam 指定的默认值[{}]转换成参数[{}]指定的类型[{}]", this.stub.getSimpleName(), method.getName(), parameter.getName(), parameter.getType().getName()));
                        }
                        value = Convertx.Default().convert(param.defaultValue(), parameter.getType());
                    } else {
                        value = param.defaultValue();
                    }
                }
                if (param.required() && (value == null || Stringx.isNullOrBlank(value.toString()))) {
                    throw new IllegalArgumentException(Stringx.format("执行 {}.{} 出错: 参数[{}]缺失", this.stub.getSimpleName(), method.getName(), parameter.getName()));
                }

                if (Stringx.isNotBlank(param.value())) {
                    name = param.value();
                }

                if (Stringx.isNotBlank(param.name())) {
                    name = param.name();
                }

                if (value != null) {
                    variables.put(name, value);
                }
                continue;
            }

            // 其它参数
            if (args[i] != null) {
                variables.put(name, args[i]);
            }
        }
    }
}
