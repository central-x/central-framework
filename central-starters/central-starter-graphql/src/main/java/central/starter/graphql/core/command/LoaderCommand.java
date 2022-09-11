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
import central.util.Listx;
import central.util.Stringx;
import com.alibaba.ttl.threadpool.TtlExecutors;
import lombok.Getter;
import lombok.Setter;
import org.dataloader.BatchLoaderEnvironment;
import org.dataloader.BatchLoaderWithContext;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * BatchLoader 命令
 *
 * @author Alan Yeh
 * @since 2022/09/09
 */
public class LoaderCommand implements BatchLoaderWithContext<String, Object> {
    /**
     * 线程池，用于执行 load 方法
     * 这里使用阿里 transmittable-thread-local 框架包装线程池，支持线程池内线程在运行时共享父线程 TransmittableThreadLocal 上下文，解决 DataLoader在不同线程上运行，动态数据源切换失败问题
     */
    private static final Executor executor = TtlExecutors.getTtlExecutorService(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2, new CustomizableThreadFactory("graphql-dataloader")));

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
     * 命令对应方法
     */
    private final Method method;

    /**
     * 命令参数
     */
    private final Parameter[] parameters;

    @Setter
    private List<GraphQLParameterResolver> resolvers = Listx.newArrayList();

    public LoaderCommand(Object target, Method method) {
        var returnType = (ParameterizedType) method.getGenericReturnType();

        this.name = returnType.getActualTypeArguments()[1].getTypeName();
        this.target = target;
        this.method = method;
        this.parameters = method.getParameters();
    }

    @Override
    public CompletionStage<List<Object>> load(List<String> keys, BatchLoaderEnvironment environment) {
        return CompletableFuture.supplyAsync(() -> {
            // 构造调用参数
            var args = new Object[this.parameters.length];

            for (int i = 0; i < this.parameters.length; i++) {
                var parameter = this.parameters[i];
                for (var resolver : this.resolvers) {
                    if (resolver.support(parameter)) {
                        args[i] = resolver.resolve(this.method, parameter, keys, environment);
                        break;
                    }
                }
            }

            try {
                var data = (Map<String, Object>) this.method.invoke(this.target, args);

                // 根据 keys 的顺序返回结果
                return keys.stream().map(data::get).toList();
            } catch (InvocationTargetException | IllegalAccessException ex) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, Stringx.format("执行 {}.{} 出现异常: " + ex.getCause().getLocalizedMessage(), this.method.getDeclaringClass().getSimpleName(), this.method.getName()), ex.getCause());
            }
        }, executor);
    }
}
