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

package central.sql.proxy.mapper;

import central.bean.MethodNotImplementedException;
import central.lang.reflect.MethodSignature;
import central.sql.query.Columns;
import central.sql.query.Conditions;
import central.sql.SqlBuilder;
import central.sql.SqlExecutor;
import central.sql.data.Entity;
import central.sql.meta.entity.EntityMeta;
import central.sql.proxy.Mapper;
import central.sql.proxy.MapperHandler;
import central.sql.proxy.MapperProxy;
import central.lang.Arrayx;

import java.lang.reflect.Method;
import java.sql.SQLException;

/**
 * FindById Handler
 *
 * @author Alan Yeh
 * @see Mapper#findById
 * @since 2022/08/11
 */
public class FindByIdHandler implements MapperHandler {

    @Override
    public Object handle(MapperProxy<?> proxy, SqlExecutor executor, SqlBuilder builder, EntityMeta meta, Method method, Object[] args) throws SQLException {
        String id = null;
        Columns<?> columns = null;

        var signature = MethodSignature.of(method);
        if (signature.equals(MethodSignature.of("findById", Entity.class, String.class))) {
            id = (String) Arrayx.getOrNull(args, 0);
        } else if (signature.equals(MethodSignature.of("findById", Entity.class, String.class, Columns.class))) {
            id = (String) Arrayx.getOrNull(args, 0);
            columns = (Columns<?>) Arrayx.getOrNull(args, 1);
        } else {
            throw new MethodNotImplementedException("MethodNotImplemented: " + signature.getSignature());
        }

        if (id == null) {
            return null;
        }

        var script = builder.forFindBy(executor, meta, 1L, 0L, columns, Conditions.of(Entity.class).eq(meta.getId().getName(), id), null);
        return executor.selectSingle(script, meta.getType());
    }
}
