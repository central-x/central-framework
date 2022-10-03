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

package central.starter.graphql.core.resolver;

import central.lang.reflect.invoke.ParameterResolver;
import central.starter.graphql.GraphQLRequest;
import central.util.Context;
import graphql.schema.DataFetchingEnvironment;
import org.dataloader.BatchLoaderEnvironment;
import org.dataloader.DataLoader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;

/**
 * GraphQL 原生参数注入
 *
 * @author Alan Yeh
 * @since 2022/09/09
 */
public class GraphQLBeanParameterResolver implements ParameterResolver {
    @Override
    public boolean support(@Nonnull Class<?> clazz, @Nonnull Method method, @Nonnull Parameter parameter) {
        return GraphQLRequest.class == parameter.getType() ||
                DataFetchingEnvironment.class == parameter.getType() ||
                BatchLoaderEnvironment.class == parameter.getType() ||
                DataLoader.class == parameter.getType();
    }

    @Nullable
    @Override
    public Object resolve(@NotNull Class<?> clazz, @NotNull Method method, @NotNull Parameter parameter, @NotNull Context context) {
        if (GraphQLRequest.class == parameter.getType()) {
            return context.get(GraphQLRequest.class);
        }

        if (DataFetchingEnvironment.class == parameter.getType()) {
            return context.get(DataFetchingEnvironment.class);
        }

        if (BatchLoaderEnvironment.class == parameter.getType()) {
            return context.get(BatchLoaderEnvironment.class);
        }

        if (DataLoader.class == parameter.getType()) {
            var fetchingEnvironment = context.get(DataFetchingEnvironment.class);
            if (fetchingEnvironment != null) {
                ParameterizedType type = (ParameterizedType) parameter.getParameterizedType();
                return fetchingEnvironment.getDataLoader(type.getActualTypeArguments()[1].getTypeName());
            }
        }

        return null;
    }
}
