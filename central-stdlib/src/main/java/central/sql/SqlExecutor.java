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

package central.sql;

import central.io.Resourcex;
import central.sql.data.Entity;
import central.sql.proxy.Mapper;
import central.sql.proxy.MapperHandler;
import central.sql.proxy.MapperProxy;
import central.util.MarkdownResources;
import central.lang.Stringx;
import lombok.SneakyThrows;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Sql Executor
 *
 * @author Alan Yeh
 * @since 2022/08/01
 */
public interface SqlExecutor {
    /**
     * 创建一个代理
     *
     * @param mapper       代理类
     * @param resourcePath 代理资源
     * @param handlers     代理方法处理器
     */
    @SneakyThrows
    default <E extends Entity, T extends Mapper<E>> T getMapper(@Nonnull Class<T> mapper, @Nullable String resourcePath, @Nullable Map<String, MapperHandler> handlers) {
        if (Stringx.isNullOrBlank(resourcePath)) {
            resourcePath = Stringx.format("classpath:///central/orm/sql/{}.md", Stringx.lowerCaseFirstLetter(mapper.getSimpleName()));
        }
        var is = Resourcex.getInputStream(URI.create(resourcePath));
        MarkdownResources resources = new MarkdownResources();
        if (is != null) {
            resources = new MarkdownResources(is);
        }

        return (T) Proxy.newProxyInstance(mapper.getClassLoader(), new Class[]{mapper}, new MapperProxy<>(mapper, this, handlers, resources));
    }

    /**
     * 创建一个代理
     *
     * @param mapper 代理类
     */
    @SneakyThrows
    default <E extends Entity, T extends Mapper<E>> T getMapper(@Nonnull Class<T> mapper) {
        return this.getMapper(mapper, null, null);
    }

    /**
     * 获取数据源
     */
    SqlSource getSource();

    /**
     * 获取数据转换器
     */
    SqlConverter getConverter();

    /**
     * 数据加密器
     */
    SqlCipher getCipher();

    /**
     * 元数据管理
     */
    SqlMetaManager getMetaManager();

    /**
     * 实体转换器
     */
    SqlTransformer getTransformer();

    /**
     * 添加拦截器
     *
     * @param interceptor Sql 拦截器
     */
    void addInterceptor(SqlInterceptor interceptor);

    /**
     * 执行查询，并返回一个结果
     *
     * @param script SQL 脚本
     * @param type   结果类型
     */
    <T> T selectSingle(SqlScript script, Class<T> type) throws SQLException;

    /**
     * 执行查询，并返回结果
     *
     * @param script SQL 脚本
     * @param type   结果类型
     */
    <T> List<T> select(SqlScript script, Class<T> type) throws SQLException;

    /**
     * 执行增删改语句
     *
     * @param script SQL 脚本
     */
    long execute(SqlScript script) throws SQLException;

    /**
     * 批量执行增删改语句
     *
     * @param batchScript 批量 SQL 脚本
     */
    long[] executeBatch(SqlBatchScript batchScript) throws SQLException;
}
