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

import central.lang.reflect.invoke.Invocation;
import central.lang.reflect.invoke.ParameterResolver;
import central.starter.graphql.core.source.Source;
import central.util.Context;
import central.util.Listx;
import central.lang.Stringx;
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
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.UndeclaredThrowableException;
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
public class BatchLoader implements BatchLoaderWithContext<String, Object> {
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
    private final Source source;

    /**
     * 命令对应方法
     */
    private final Method method;

    @Setter
    private List<ParameterResolver> resolvers = Listx.newArrayList();

    public BatchLoader(Source source, Method method) {
        var returnType = (ParameterizedType) method.getGenericReturnType();

        this.name = returnType.getActualTypeArguments()[1].getTypeName();
        this.source = source;
        this.method = method;
    }

    public static BatchLoader of(Source source, Method method) {
        return new BatchLoader(source, method);
    }

    @Override
    public CompletionStage<List<Object>> load(List<String> keys, BatchLoaderEnvironment environment) {
        return CompletableFuture.supplyAsync(() -> {
            Context context = environment.getContext();
            var origin = context.get(BatchLoaderEnvironment.class);
            try {
                context.set(BatchLoaderEnvironment.class, environment);
                context.set("keys", keys);
                context.set("ids", keys);

                try {
                    var data = (Map<String, Object>) Invocation.of(method).resolvers(this.resolvers).invoke(this.source.getSource(context), context);
                    // 根据 keys 的顺序返回结果
                    return keys.stream().map(data::get).toList();
                } catch (InvocationTargetException | IllegalAccessException ex) {
                    if (ex.getCause() instanceof UndeclaredThrowableException throwable) {
                        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, Stringx.format("执行 {}.{} 出现异常: " + throwable.getCause().getLocalizedMessage(), this.method.getDeclaringClass().getSimpleName(), this.method.getName()), throwable.getCause());
                    } else {
                        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, Stringx.format("执行 {}.{} 出现异常: " + ex.getCause().getLocalizedMessage(), this.method.getDeclaringClass().getSimpleName(), this.method.getName()), ex.getCause());
                    }
                }
            } finally {
                context.set(BatchLoaderEnvironment.class, origin);
                context.remove("keys");
                context.remove("ids");
            }
        }, executor);
    }
}
