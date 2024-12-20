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
import central.bean.Page;
import central.lang.Arrayx;
import central.lang.Assertx;
import central.lang.reflect.MethodSignature;
import central.sql.SqlBuilder;
import central.sql.SqlExecutor;
import central.sql.meta.entity.EntityMeta;
import central.sql.proxy.Mapper;
import central.sql.proxy.MapperHandler;
import central.sql.proxy.MapperProxy;
import central.sql.query.Columns;
import central.sql.query.Conditions;
import central.sql.query.Orders;

import java.lang.reflect.Method;
import java.sql.SQLException;

/**
 * FindPageBy Handler
 *
 * @author Alan Yeh
 * @see Mapper#findPageBy
 * @since 2022/08/11
 */
public class FindPageByHandler implements MapperHandler {
    @Override
    public Object handle(MapperProxy<?> proxy, SqlExecutor executor, SqlBuilder builder, EntityMeta meta, Method method, Object[] args) throws SQLException {
        long pageIndex = (long) Arrayx.getOrNull(args, 0);
        long pageSize = (long) Arrayx.getOrNull(args, 1);
        Columns<?> columns = null;
        Conditions<?> conditions = null;
        Orders<?> orders = null;

        var signature = MethodSignature.of(method);
        if (signature.equals(MethodSignature.of("findPageBy", Page.class, Long.class, Long.class, Conditions.class, Orders.class))) {
            conditions = (Conditions<?>) Arrayx.getOrNull(args, 2);
            orders = (Orders<?>) Arrayx.getOrNull(args, 3);
        } else if (signature.equals(MethodSignature.of("findPageBy", Page.class, Long.class, Long.class, Columns.class, Conditions.class, Orders.class))) {
            columns = (Columns<?>) Arrayx.getOrNull(args, 2);
            conditions = (Conditions<?>) Arrayx.getOrNull(args, 3);
            orders = (Orders<?>) Arrayx.getOrNull(args, 4);
        } else {
            throw new MethodNotImplementedException("MethodNotImplemented: " + signature.getSignature());
        }

        Assertx.mustTrue(pageIndex > 0, "分页下标[pageIndex]必须大于等于 1");
        Assertx.mustTrue(pageSize > 0, "分页大小[pageSize]必须大于等于 1");
        Assertx.mustTrue(pageIndex * pageSize < 1_000_000, "分页查询时不允许查询超过 100W 的数据");

        // 计算总数
        var countScript = builder.forCountBy(executor, meta, conditions);
        Long count = executor.selectSingle(countScript, Long.class);

        if (count == null || count.equals(0L)) {
            return Page.ofEmpty(pageIndex, pageSize);
        } else {
            var selectScript = builder.forFindBy(executor, meta, pageSize, (pageIndex - 1) * pageSize, columns, conditions, orders);
            var data = executor.select(selectScript, meta.getType());
            return Page.of(data, pageIndex, pageSize, (long) Math.ceil(count * 1.0d / pageSize), count);
        }
    }
}
