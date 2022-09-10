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

import central.sql.SqlExecutor;
import central.sql.meta.entity.EntityMeta;

import java.lang.reflect.Method;
import java.sql.SQLException;

/**
 * Mapper 方法处理器
 *
 * @author Alan Yeh
 * @since 2022/08/01
 */
public interface MapperHandler {
    /**
     * 处理 Mapper 方法
     *
     * @param proxy    代理
     * @param executor Sql 处理器
     * @param meta     实体元数据
     * @param method   被调用的方法
     * @param args     被调的参数
     * @return 处理结果
     */
    Object handle(MapperProxy<?> proxy, SqlExecutor executor, EntityMeta meta, Method method, Object[] args) throws SQLException;
}
