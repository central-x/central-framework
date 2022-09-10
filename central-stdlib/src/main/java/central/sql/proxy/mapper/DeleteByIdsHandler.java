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

import central.sql.Conditions;
import central.sql.SqlExecutor;
import central.sql.meta.entity.EntityMeta;
import central.sql.proxy.Mapper;
import central.sql.proxy.MapperHandler;
import central.sql.proxy.MapperProxy;
import central.util.Arrayx;
import central.util.Listx;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.List;

/**
 * DeleteByIds Handler
 *
 * @author Alan Yeh
 * @see Mapper#deleteByIds
 * @since 2022/08/10
 */
public class DeleteByIdsHandler implements MapperHandler {
    @Override
    public Object handle(MapperProxy<?> proxy, SqlExecutor executor, EntityMeta meta, Method method, Object[] args) throws SQLException {
        if (Arrayx.isNullOrEmpty(args)) {
            return 0L;
        }

        var ids = (List<String>) Arrayx.getFirst(args);
        if (Listx.isNullOrEmpty(ids)) {
            return 0L;
        }

        Conditions conditions;
        if (ids.size() == 1) {
            conditions = Conditions.where().eq(meta.getId().getName(), ids.get(0));
        } else {
            conditions = Conditions.where().in(meta.getId().getName(), ids);
        }

        var script = executor.getBuilder().forDeleteBy(executor, meta, conditions);
        return executor.execute(script);
    }
}
