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

import central.io.Resourcex;
import central.sql.SqlDialect;
import central.sql.SqlExecutor;
import central.util.MarkdownResources;
import central.lang.Stringx;
import central.validation.Label;
import central.validation.Validatex;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Mapper 工厂
 *
 * @author Alan Yeh
 * @since 2022/07/20
 */
@Setter
@Accessors(chain = true)
public class MapperFactory<T extends Mapper<?>> {
    private final Class<T> target;

    public MapperFactory(Class<T> target) {
        this.target = target;
        this.resourcePath = Stringx.format("classpath:///central/orm/{}.md", Stringx.lowerCaseFirstLetter(target.getSimpleName()));
    }

    @NotBlank
    @Label("Sql 资源路径")
    private String resourcePath;

    /**
     * 方法处理器
     * 已默认添加 Mapper 中声明的方法的处理器
     * 这里主要添加开发者需要额外支持的处理器
     */
    private Map<String, MapperHandler> handlers = new HashMap<>();

    /**
     * 为方法添加处理器
     *
     * @param method  方法名
     * @param handler 处理器
     */
    public MapperFactory<T> addMapper(String method, MapperHandler handler) {
        this.handlers.put(method, handler);
        return this;
    }

    @NotNull
    @Label("SQL 执行器")
    private SqlExecutor executor;

    @SneakyThrows(IOException.class)
    public T build() {
        Validatex.Default().validateBean(this);

        var is = Resourcex.getInputStream(URI.create(this.resourcePath));

        MarkdownResources resources = new MarkdownResources();
        if (is != null) {
            resources = new MarkdownResources(is);
        }

        for (var dialect : SqlDialect.values()) {
            // 加载方言
            is = Resourcex.getInputStream(URI.create(Stringx.format("classpath:///central/orm/{}/{}.md", dialect.getName().toLowerCase(), Stringx.lowerCaseFirstLetter(target.getSimpleName()))));
            if (is != null) {
                resources.load(dialect.getName().toLowerCase(), is);
            }
        }

        return (T) Proxy.newProxyInstance(this.target.getClassLoader(), new Class[]{target}, new MapperProxy<>(this.target, this.executor, handlers, resources));
    }
}
