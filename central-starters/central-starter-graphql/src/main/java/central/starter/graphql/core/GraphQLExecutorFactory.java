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

package central.starter.graphql.core;

import central.bean.Available;
import central.bean.Deletable;
import central.bean.Sortable;
import central.bean.Versional;
import central.io.IOStreamx;
import central.sql.data.*;
import central.starter.graphql.GraphQLConfigurer;
import central.starter.graphql.GraphQLParameterResolver;
import central.starter.graphql.GraphQLService;
import central.starter.graphql.annotation.*;
import central.starter.graphql.core.command.FetcherCommand;
import central.starter.graphql.core.command.GetterCommand;
import central.starter.graphql.core.command.LoaderCommand;
import central.starter.graphql.core.resolver.*;
import central.util.Listx;
import central.util.Mapx;
import central.lang.Stringx;
import graphql.GraphQL;
import graphql.GraphQLException;
import graphql.schema.FieldCoordinates;
import graphql.schema.GraphQLCodeRegistry;
import graphql.schema.GraphQLScalarType;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.*;
import lombok.Setter;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Executor Factory
 *
 * @author Alan Yeh
 * @since 2022/09/09
 */
public class GraphQLExecutorFactory implements FactoryBean<GraphQLExecutor>, InitializingBean {
    @Setter(onMethod_ = @Autowired(required = false))
    private List<GraphQLConfigurer> configurers;

    @Setter(onMethod_ = @Autowired(required = false))
    private Map<String, GraphQLService> services;

    private final List<GraphQLParameterResolver> resolvers = new ArrayList<>();

    private GraphQL graphQL;

    /**
     * 批量数据加载注册中心
     * 此功能用于解决 N + 1 查询性能问题
     */
    private final LoaderCommandRegistry loaderRegistry = new LoaderCommandRegistry();

    @Override
    public void afterPropertiesSet() throws Exception {
        // 类型注册中心
        TypeDefinitionRegistry registry = new TypeDefinitionRegistry();
        // 构建运行时
        RuntimeWiring.Builder wiring = RuntimeWiring.newRuntimeWiring();

        // 初始化标量
        initScalars(wiring);

        // 初始化参数解析器
        initParameterResolvers();

        // 初始化 graphql schemes
        initSchemas(registry, wiring);

        // 完成 Graphql 初始化
        GraphQLSchema schema = new SchemaGenerator().makeExecutableSchema(registry, wiring.build());
        this.graphQL = GraphQL.newGraphQL(schema).build();
    }

    /**
     * 初始化标量
     */
    private void initScalars(RuntimeWiring.Builder wiring) {
        List<GraphQLScalarType> scalars = new ArrayList<>();
        scalars.add(Scalars.ANY);
        scalars.add(Scalars.TIMESTAMP);
        scalars.add(Scalars.LONG);

        if (Listx.isNotEmpty(this.configurers)) {
            for (GraphQLConfigurer configurer : this.configurers) {
                configurer.addScalars(scalars);
            }
        }

        for (GraphQLScalarType scalar : scalars) {
            wiring.scalar(scalar);
        }
    }

    private void initParameterResolvers() {
        // 添加默认的参数解析器
        this.resolvers.add(new GraphQLBeanParameterResolver());
        this.resolvers.add(new ServletParameterResolver());
        this.resolvers.add(new SpringBeanParameterResolver());
        this.resolvers.add(new RequestParamParameterResolver());
        this.resolvers.add(new RequestHeaderParameterResolver());
        this.resolvers.add(new RequestAttributeParameterResolver());

        // 添加开发者指定的参数解析器
        if (Listx.isNotEmpty(this.configurers)) {
            for (GraphQLConfigurer configurer : this.configurers) {
                configurer.addParameterResolvers(this.resolvers);
            }
        }
    }

    private void initSchemas(TypeDefinitionRegistry registry, RuntimeWiring.Builder wiring) throws IOException {
        // Schema 解析器
        SchemaParser parser = new SchemaParser();

        // 解析 root schema
        String root = IOStreamx.readText(Thread.currentThread().getContextClassLoader().getResourceAsStream("central/graphql/root.graphql"), StandardCharsets.UTF_8);
        registry.merge(parser.parse(root));

        // Service 注册的数据实体
        List<Class<?>> types = new ArrayList<>();
        if (Mapx.isNotEmpty(this.services)) {
            // 解析所有 GraphQLService，并将这些 Service 与 GraphQL 绑定
            for (Map.Entry<String, GraphQLService> entry : this.services.entrySet()) {
                Class<?> serviceClass = AopUtils.getTargetClass(entry.getValue());

                // 读取服务对应的 graphql 声明
                String graphqlPath = "central/graphql";
                if (Stringx.isNotBlank(entry.getValue().getGroup())) {
                    graphqlPath += ("/" + entry.getValue().getGroup());
                }
                graphqlPath += ("/" + entry.getKey() + ".graphql");

                InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(graphqlPath);
                if (is == null) {
                    throw new GraphQLException(Stringx.format("解析 GraphQLService 错误: 没有找到服务[{}]对应的 GraphQL 描述文件: {}", serviceClass.getSimpleName(), graphqlPath));
                }

                // 解析 graphql，并将解析后的 graphql 合并到根
                try {
                    TypeDefinitionRegistry definition = parser.parse(new InputStreamReader(is, StandardCharsets.UTF_8));
                    registry.merge(definition);
                } catch (Exception ex) {
                    throw new GraphQLException(Stringx.format("解析 GraphQLService 错误: 解析服务[{}]对应的 GraphQL 描述文件异常: " + ex.getLocalizedMessage(), graphqlPath), ex);
                }

                // 添加 Service 注册的数据类型
                if (Listx.isNullOrEmpty(entry.getValue().getTypes())) {
                    throw new GraphQLException(Stringx.format("解析 GraphQLService 错误: 服务[{}]注册的类型数量不能为空", serviceClass.getSimpleName()));
                }
                types.addAll(entry.getValue().getTypes());

                // 解析 Service 里面的所有方法，找到待暴露的 GraphQL 命令
                for (Method method : serviceClass.getMethods()) {
                    if (method.getAnnotation(GraphQLQuery.class) != null) {
                        // 解析查询命令
                        FetcherCommand command = new FetcherCommand(entry.getValue(), method);
                        command.setResolvers(this.resolvers);
                        // 绑定查询命令到根查询对象（Query）
                        wiring.type(TypeRuntimeWiring.newTypeWiring("Query").dataFetcher(command.getName(), command));
                    }

                    if (method.getAnnotation(GraphQLMutation.class) != null) {
                        // 解析修改命令
                        FetcherCommand command = new FetcherCommand(entry.getValue(), method);
                        command.setResolvers(this.resolvers);
                        // 绑定修改命令到根修改对象（Mutation）
                        wiring.type(TypeRuntimeWiring.newTypeWiring("Mutation").dataFetcher(command.getName(), command));
                    }

                    if (method.getAnnotation(GraphQLBatchLoader.class) != null) {
                        // 解析 BatchLoader
                        // 为了保证代码能正确运行，在启动期间需要对方法进行类型校验，方便开发人员排查问题
                        Type returnType = method.getGenericReturnType();
                        if (returnType instanceof ParameterizedType type) {
                            if (!Map.class.isAssignableFrom((Class<?>) type.getRawType())) {
                                throw new GraphQLException(Stringx.format("BatchLoader[{}.{}] 注册失败: 方法的返回值必须是 Map<String, ?> 类型", serviceClass.getCanonicalName(), method.getName()));
                            }
                            if (!String.class.isAssignableFrom((Class<?>) type.getActualTypeArguments()[0])) {
                                throw new GraphQLException(Stringx.format("BatchLoader[{}.{}] 注册失败: 方法的返回值必须是 Map<String, ?> 类型", serviceClass.getCanonicalName(), method.getName()));
                            }
                        } else {
                            throw new GraphQLException(Stringx.format("BatchLoader[{}.{}] 注册失败: 方法的返回值必须是 Map<String, ?> 类型", serviceClass.getCanonicalName(), method.getName()));
                        }

                        LoaderCommand command = new LoaderCommand(entry.getValue(), method);
                        if (this.loaderRegistry.isRegistered(command.getName())) {
                            throw new GraphQLException(Stringx.format("BatchLoader[{}.{}] 注册失败: 无法为类型[{}]注册多个 Loader", serviceClass.getCanonicalName(), method.getName(), command.getName()));
                        }
                        command.setResolvers(this.resolvers);
                        // 注册 DataLoader
                        this.loaderRegistry.register(command);
                    }
                }
            }
        }
        // 按接口分类数据实体
        List<Class<?>> interfaces = Arrays.asList(Entity.class, Deletable.class, Modifiable.class, Available.class, Sortable.class, Versional.class);
        Map<String, List<Class<?>>> groups = interfaces.stream().collect(Collectors.toMap(Class::getSimpleName, it -> new ArrayList<>()));

        // 遍历数据实体，对其进行分组，绑定关联查询方法
        GraphQLCodeRegistry.Builder fieldRegistry = GraphQLCodeRegistry.newCodeRegistry();
        for (Class<?> clazz : types) {
            var type = clazz.getAnnotation(GraphQLType.class);
            if (type == null) {
                throw new GraphQLException(Stringx.format("{} 类型必须持有 GraphQLType 注解, 且注解的 value 不为空", clazz.getCanonicalName()));
            }
            if (Stringx.isNullOrEmpty(type.value())) {
                throw new GraphQLException(Stringx.format("{} 类型必须持有 GraphQLType 注解, 且注解的 value 不为空", clazz.getCanonicalName()));
            }
            if (!Entity.class.isAssignableFrom(clazz)) {
                throw new GraphQLException(Stringx.format("{} 类型必须是 Entity 的子类", clazz.getCanonicalName()));
            }

            interfaces.forEach(it -> {
                if (it.isAssignableFrom(clazz)) {
                    groups.get(it.getSimpleName()).add(clazz);
                }
            });

            // 绑定关联查询方法
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.getDeclaredAnnotation(GraphQLGetter.class) != null) {
                    GetterCommand command = new GetterCommand(method);
                    command.setResolvers(this.resolvers);
                    fieldRegistry.dataFetcher(FieldCoordinates.coordinates(type.value(), command.getName()), command);
                }
            }
        }

        wiring.codeRegistry(fieldRegistry);

        // 接口与类型绑定
        for (Map.Entry<String, List<Class<?>>> entry : groups.entrySet()) {
            wiring.type(TypeRuntimeWiring.newTypeWiring(entry.getKey()).typeResolver(env -> {
                Object obj = env.getObject();
                for (Class<?> type : entry.getValue()) {
                    if (obj.getClass().isAssignableFrom(type)) {
                        return env.getSchema().getObjectType(type.getAnnotation(GraphQLType.class).value());
                    }
                }
                return null;
            }));
        }
    }

    @Override
    public Class<?> getObjectType() {
        return GraphQLExecutor.class;
    }

    @Override
    public GraphQLExecutor getObject() throws Exception {
        GraphQLExecutor executor = new GraphQLExecutor();
        executor.setGraphQL(this.graphQL);
        executor.setRegistry(this.loaderRegistry);
        return executor;
    }
}
