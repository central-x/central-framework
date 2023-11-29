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

package central.sql.proxy;

import central.lang.Assertx;
import central.lang.reflect.TypeRef;
import central.sql.SqlExecutor;
import central.sql.SqlScript;
import central.sql.data.Entity;
import central.sql.datasource.dynamic.lookup.LookupKey;
import central.sql.datasource.dynamic.lookup.LookupKeyHolder;
import central.sql.proxy.mapper.*;
import central.util.Mapx;
import central.util.MarkdownResources;
import central.lang.Stringx;
import lombok.Getter;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.rmi.StubNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mapper 代理
 *
 * @author Alan Yeh
 * @since 2022/08/01
 */
public class MapperProxy<T extends Mapper<?>> implements InvocationHandler {

    /**
     * Mapper 类型
     */
    @Getter
    private final Class<T> mapperType;

    /**
     * 方法处理器
     */
    private final Map<String, MapperHandler> handlers;

    /**
     * 实体类型
     */
    @Getter
    private final Class<? extends Entity> entityType;

    /**
     * Sql 执行器
     */
    private final SqlExecutor executor;

    /**
     * Markdown 资源
     */
    private final MarkdownResources resources;

    /**
     * 当前 Proxy 是否有指定数据源
     */
    private final String lookupKey;

    @SuppressWarnings("unchecked")
    public MapperProxy(Class<T> mapperType, SqlExecutor executor, Map<String, MapperHandler> handlers, MarkdownResources resources) {
        this.mapperType = mapperType;
        this.entityType = (Class<? extends Entity>) TypeRef.of(mapperType).getInterfaceType(0).getActualTypeArgument(0).getRawClass();
        Assertx.mustAssignableFrom(Entity.class, this.entityType, "Mapper 指定的实体类型必须继承于 Entity");

        this.executor = executor;
        this.handlers = new HashMap<>();
        this.handlers.put("destroy", new DestroyHandler());
        this.handlers.put("getMapper", new GetMapperHandler());
        this.handlers.put("insert", new InsertHandler());
        this.handlers.put("insertBatch", new InsertBatchHandler());
        this.handlers.put("deleteById", new DeleteByIdHandler());
        this.handlers.put("deleteByIds", new DeleteByIdsHandler());
        this.handlers.put("deleteBy", new DeleteByHandler());
        this.handlers.put("deleteAll", new DeleteAllHandler());
        this.handlers.put("update", new UpdateHandler());
        this.handlers.put("updateBy", new UpdateByHandler());
        this.handlers.put("findById", new FindByIdHandler());
        this.handlers.put("findByIds", new FindByIdsHandler());
        this.handlers.put("findFirstBy", new FindFirstByHandler());
        this.handlers.put("findBy", new FindByHandler());
        this.handlers.put("findAll", new FindAllHandler());
        this.handlers.put("findPageBy", new FindPageByHandler());
        this.handlers.put("count", new CountHandler());
        this.handlers.put("countBy", new CountByHandler());
        this.handlers.put("existsBy", new ExistsByHandler());
        this.handlers.put("toString", new ToStringHandler());
        if (Mapx.isNotEmpty(handlers)) {
            this.handlers.putAll(handlers);
        }
        this.resources = resources;

        var lookupKey = this.mapperType.getAnnotation(LookupKey.class);
        if (lookupKey != null) {
            this.lookupKey = lookupKey.value();
        } else {
            this.lookupKey = null;
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.isDefault()) {
            // mapper 的 default 方法
            return InvocationHandler.invokeDefault(proxy, method, args);
        } else {
            var lookupKey = this.lookupKey;
            // 判断当前方法有没有指定数据源
            var lookupKeyAnno = method.getAnnotation(LookupKey.class);
            if (lookupKeyAnno != null) {
                lookupKey = lookupKeyAnno.value();
            }

            String originLookupKey = null;
            if (Stringx.isNotBlank(lookupKey)) {
                // 记录当前的数据源，完成执行后再切回来
                originLookupKey = LookupKeyHolder.getLookupKey();
                // 强制切换数据源
                LookupKeyHolder.setLookupKey(lookupKey);
            }

            try {
                // 查看是否已经注册了方法处理器
                var handler = this.handlers.get(method.getName());
                if (handler != null) {
                    return handler.handle(this, this.executor, this.executor.getSource().getDialect().getBuilder(), this.executor.getMetaManager().getMeta(this.entityType), method, args);
                }

                // 如果没有注册方法处理器，那么就查找 Sql 资源
                var resource = this.resources.get(method.getName());
                if (resource == null) {
                    throw new StubNotFoundException(Stringx.format("找不到 {}.{} 对应的 Sql 声明", this.mapperType.getSimpleName(), method.getName()));
                }

                Map<String, Object> params = new HashMap<>();
                Parameter[] parameters = method.getParameters();
                for (int i = 0; i < parameters.length; i++) {
                    params.put(parameters[i].getName(), args[i]);
                }

                // TODO 解析 Sql
                var script = new SqlScript(resource.getContent());

                if (method.getReturnType().isAssignableFrom(List.class)) {
                    return this.executor.select(script, method.getReturnType());
                } else {
                    return this.executor.selectSingle(script, method.getReturnType());
                }
            } finally {
                if (Stringx.isNotBlank(lookupKey)) {
                    // 还原 LookupKey
                    LookupKeyHolder.setLookupKey(originLookupKey);
                }
            }
        }
    }
}
