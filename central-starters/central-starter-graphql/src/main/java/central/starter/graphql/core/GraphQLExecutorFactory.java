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

import central.bean.*;
import central.lang.Arrayx;
import central.lang.Assertx;
import central.lang.reflect.invoke.ParameterResolver;
import central.sql.data.*;
import central.starter.graphql.*;
import central.starter.graphql.annotation.*;
import central.starter.graphql.annotation.GraphQLSchema;
import central.starter.graphql.annotation.GraphQLType;
import central.starter.graphql.core.fetcher.BatchLoader;
import central.starter.graphql.core.fetcher.SourceFetcher;
import central.starter.graphql.core.resolver.*;
import central.starter.graphql.core.source.GraphQLSource;
import central.starter.graphql.core.source.SpringSource;
import central.starter.graphql.core.source.StaticSource;
import central.util.Listx;
import central.lang.Stringx;
import central.util.Objectx;
import graphql.GraphQL;
import graphql.GraphQLException;
import graphql.schema.*;
import graphql.schema.idl.*;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Executor Factory
 *
 * @author Alan Yeh
 * @since 2022/09/09
 */
@Slf4j
public class GraphQLExecutorFactory implements FactoryBean<GraphQLExecutor>, InitializingBean {
    /**
     * Executor 配置器
     */
    @Setter(onMethod_ = @Autowired)
    private GraphQLConfigurer configurer;

    /**
     * 参数解析器
     */
    private final List<ParameterResolver> resolvers = new ArrayList<>();

    private GraphQL graphQL;

    /**
     * 批量数据加载注册中心
     * 此功能用于解决 N + 1 查询性能问题
     */
    private final LoaderRegistry loaderRegistry = new LoaderRegistry();

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
        initRootSchema(registry, wiring);
        initSchema(registry, wiring);

        // 完成 Graphql 初始化
        this.graphQL = GraphQL.newGraphQL(new SchemaGenerator().makeExecutableSchema(registry, wiring.build())).build();
    }

    /**
     * 初始化标量
     */
    private void initScalars(RuntimeWiring.Builder wiring) {
        wiring.scalar(Scalars.ANY).scalar(Scalars.TIMESTAMP).scalar(Scalars.LONG);

        var scalars = configurer.getScalars();
        if (Listx.isNotEmpty(scalars)) {
            for (GraphQLScalarType scalar : scalars) {
                wiring.scalar(scalar);
            }
        }
    }

    /**
     * 初始化参数解析器
     */
    private void initParameterResolvers() {
        // 添加默认的参数解析器
        this.resolvers.add(new GraphQLBeanParameterResolver());
        this.resolvers.add(new ServletParameterResolver());
        this.resolvers.add(new SpringBeanParameterResolver());
        this.resolvers.add(new RequestParamParameterResolver());
        this.resolvers.add(new RequestHeaderParameterResolver());
        this.resolvers.add(new RequestAttributeParameterResolver());

        // 添加开发者指定的参数解析器
        this.resolvers.addAll(Objectx.get(configurer.getParameterResolvers(), Collections.emptyList()));
    }

    /**
     * 初始化 GraphQL 根声明
     */
    private void initRootSchema(TypeDefinitionRegistry registry, RuntimeWiring.Builder wiring) throws IOException {
        // Schema 解析器
        SchemaParser parser = new SchemaParser();

        // 解析 root schema
        var resources = Thread.currentThread().getContextClassLoader().getResources("central/graphql/root.graphql");
        Assertx.mustTrue(resources.hasMoreElements(), GraphQLException::new, "初始化 GraphQL 异常: 没有找到 central/graphql/root.graphql 文件");

        while (resources.hasMoreElements()) {
            var resource = resources.nextElement();

            try (var is = resource.openStream()) {
                var definition = parser.parse(new InputStreamReader(is, StandardCharsets.UTF_8));
                registry.merge(definition);
            } catch (IOException ex) {
                throw new GraphQLException("初始化 GraphQL 异常: 解析 central/graphql/root.graphql 文件异常: " + ex.getLocalizedMessage(), ex);
            }
        }
    }

    /**
     * 根据源类型递归扫描后面的所有类型
     *
     * @param source  源类型
     * @param types   已注册类型
     * @param schemas 忆注册 Schemas
     */
    private void scanTypes(@Nonnull Class<?> source, @Nonnull Map<String, Class<?>> types, @Nonnull Set<String> schemas) throws IOException {
        {
            // 注册类型
            String name = source.getSimpleName();
            var annotation = source.getAnnotation(GraphQLType.class);
            if (annotation != null) {
                name = Objectx.get(annotation.value(), source.getSimpleName());
            }

            var type = types.get(name);
            if (type != null && type != source) {
                throw new IllegalStateException(Stringx.format("GraphQL 类型[{}]存在多个映射类型: {}, {}", name, source.getName(), type.getName()));
            }

            types.put(name, source);
        }

        {
            // 注册 graphql schema
            var schema = source.getAnnotation(GraphQLSchema.class);
            if (schema != null) {
                var graphql = Path.of("central", "graphql", schema.path(), Objectx.get(schema.name(), Stringx.lowerCaseFirstLetter(source.getSimpleName())) + ".graphql");
                schemas.add(graphql.toString());
                // 如果有声明类，则递归注册
                if (Arrayx.isNotEmpty(schema.types())) {
                    for (var it : schema.types()) {
                        this.scanTypes(it, types, schemas);
                    }
                }
            }
        }
    }

    /**
     * 初始化 GraphQL Query 声明
     */
    private void initSchema(TypeDefinitionRegistry registry, RuntimeWiring.Builder wiring) throws IOException {
        // Schema 解析器
        SchemaParser parser = new SchemaParser();
        // 遍历数据实体，对其进行分组，绑定关联查询方法
        GraphQLCodeRegistry.Builder fieldRegistry = GraphQLCodeRegistry.newCodeRegistry();
        // 待注册的数据类型
        Map<String, Class<?>> types = new HashMap<>();
        Set<String> schemas = new HashSet<>();

        // 按接口分类数据实体
        List<Class<?>> interfaces = List.of(Entity.class, Modifiable.class, Deletable.class, Codeable.class, Available.class, Orderable.class, Versional.class);
        Map<String, List<Class<?>>> groups = interfaces.stream().collect(Collectors.toMap(Class::getSimpleName, it -> new ArrayList<>()));

        // 解析 Query
        {
            var query = Assertx.requireNotNull(this.configurer.getQuery(), NullPointerException::new, "{} 的 getQuery 不能返回 null", GraphQLConfigurer.class.getSimpleName());
            var queryType = AopUtils.getTargetClass(query);
            this.scanTypes(queryType, types, schemas);
        }

        // 解析 Mutation
        {
            var mutation = Assertx.requireNotNull(this.configurer.getMutation(), NullPointerException::new, "{} 的 getMutation 不能返回 null", GraphQLConfigurer.class.getSimpleName());
            var mutationType = AopUtils.getTargetClass(mutation);
            this.scanTypes(mutationType, types, schemas);
        }

        for (var it : types.entrySet()) {
            var name = it.getKey();
            var clazz = it.getValue();

            interfaces.forEach(inter -> {
                if (inter.isAssignableFrom(clazz)) {
                    groups.get(inter.getSimpleName()).add(clazz);
                }
            });

            for (var method : clazz.getMethods()) {
                if (!Modifier.isPublic(method.getModifiers())) {
                    continue;
                }

                var fetcher = method.getAnnotation(GraphQLFetcher.class);
                if (fetcher != null) {
                    SourceFetcher dataFetcher;
                    if ("Query".equals(name)) {
                        dataFetcher = SourceFetcher.of(StaticSource.of(this.configurer.getQuery()), method);
                    } else if ("Mutation".equals(name)) {
                        dataFetcher = SourceFetcher.of(StaticSource.of(this.configurer.getMutation()), method);
                    } else {
                        dataFetcher = SourceFetcher.of(new GraphQLSource(), method);
                    }
                    dataFetcher.setResolvers(this.resolvers);
                    wiring.type(TypeRuntimeWiring.newTypeWiring(name).dataFetcher(dataFetcher.getName(), dataFetcher));
                }

                var getter = method.getAnnotation(GraphQLGetter.class);
                if (getter != null) {
                    SourceFetcher dataFetcher;
                    if ("Query".equals(name)) {
                        dataFetcher = SourceFetcher.ofGetter(StaticSource.of(this.configurer.getQuery()), method);
                    } else if ("Mutation".equals(name)) {
                        dataFetcher = SourceFetcher.ofGetter(StaticSource.of(this.configurer.getMutation()), method);
                    } else {
                        dataFetcher = SourceFetcher.ofGetter(new GraphQLSource(), method);
                    }
                    dataFetcher.setResolvers(this.resolvers);
                    wiring.type(TypeRuntimeWiring.newTypeWiring(name).dataFetcher(dataFetcher.getName(), dataFetcher));
                }

                if (method.getAnnotation(GraphQLBatchLoader.class) != null) {
                    // 解析 BatchLoader
                    // 为了保证代码能正确运行，在启动期间需要对方法进行类型校验，方便开发人员排查问题
                    var returnType = method.getGenericReturnType();
                    if (returnType instanceof ParameterizedType type) {
                        if (!Map.class.isAssignableFrom((Class<?>) type.getRawType())) {
                            throw new GraphQLException(Stringx.format("BatchLoader[{}.{}] 注册失败: 方法的返回值必须是 Map<String, ?> 类型", clazz.getCanonicalName(), method.getName()));
                        }
                        if (!String.class.isAssignableFrom((Class<?>) type.getActualTypeArguments()[0])) {
                            throw new GraphQLException(Stringx.format("BatchLoader[{}.{}] 注册失败: 方法的返回值必须是 Map<String, ?> 类型", clazz.getCanonicalName(), method.getName()));
                        }
                    } else {
                        throw new GraphQLException(Stringx.format("BatchLoader[{}.{}] 注册失败: 方法的返回值必须是 Map<String, ?> 类型", clazz.getCanonicalName(), method.getName()));
                    }

                    BatchLoader loader = new BatchLoader(SpringSource.of(clazz), method);
                    if (this.loaderRegistry.isRegistered(loader.getName())) {
                        throw new GraphQLException(Stringx.format("BatchLoader[{}.{}] 注册失败: 无法为类型[{}]注册多个 Loader", clazz.getCanonicalName(), method.getName(), loader.getName()));
                    }
                    loader.setResolvers(this.resolvers);
                    // 注册 DataLoader
                    this.loaderRegistry.register(loader);
                }
            }
        }


        // 注册 schema
        for (var schema : schemas) {
            var resources = Thread.currentThread().getContextClassLoader().getResources(schema);
            if (!resources.hasMoreElements()) {
                log.warn("跳过解析 Query: 没有找到文件[{}]", schema);
                continue;
            }
            while (resources.hasMoreElements()) {
                var url = resources.nextElement();
                // 解析 graphql，并将解析后的 graphql 合并到根
                try (var is = url.openStream()) {
                    TypeDefinitionRegistry definition = parser.parse(new InputStreamReader(is, StandardCharsets.UTF_8));
                    registry.merge(definition);
                } catch (Exception ex) {
                    throw new GraphQLException(Stringx.format("解析 Query 错误: 解析 GraphQL 描述文件异常: " + ex.getLocalizedMessage(), schema), ex);
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
