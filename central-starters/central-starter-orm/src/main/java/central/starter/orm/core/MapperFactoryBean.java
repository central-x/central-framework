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

package central.starter.orm.core;

import central.sql.SqlExecutor;
import central.sql.proxy.Mapper;
import central.sql.proxy.MapperFactory;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.FactoryBean;

/**
 * Mapper Factory
 *
 * @author Alan Yeh
 * @since 2022/09/22
 */
public class MapperFactoryBean<T extends Mapper<?>> implements FactoryBean<T> {

    /**
     * Mapper 类型
     */
    @Getter
    private final Class<T> objectType;

    /**
     * Mapper 对象名称
     */
    @Setter
    private String name;

    @Setter
    private SqlExecutor executor;

    public MapperFactoryBean(Class<T> objectType) {
        this.objectType = objectType;
    }

    @Override
    public T getObject() throws Exception {
        return new MapperFactory<>(this.objectType)
                .setExecutor(this.executor)
                .build();
    }
}
