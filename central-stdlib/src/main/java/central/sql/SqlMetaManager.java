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

import central.sql.data.Entity;
import central.sql.meta.database.DatabaseMeta;
import central.sql.meta.entity.EntityMeta;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.SQLException;
import java.util.function.Predicate;

/**
 * 元数据管理
 *
 * @author Alan Yeh
 * @since 2022/09/13
 */
public interface SqlMetaManager {
    /**
     * 构建数据库元数据
     * 由于数据库里面有许多表，为了减少扫描表的时间，可以通过表匹配器确认哪些表需要扫描
     *
     * @param executor Sql 执行器
     * @param matcher  表匹配器，参数为表名。如果表匹配器为空，或返回 true 时解析表
     * @return 数据库元数据
     */
    DatabaseMeta getMeta(@Nonnull SqlExecutor executor, @Nullable Predicate<String> matcher) throws SQLException;

    /**
     * 构建数据库元数据
     *
     * @param executor Sql 执行器
     * @return 数据库元数据
     */
    default DatabaseMeta getMeta(@Nonnull SqlExecutor executor) throws SQLException {
        return this.getMeta(executor, null);
    }

    /**
     * 构建实体元数据
     *
     * @param entity 实体类型
     * @return 实体元数据
     */
    EntityMeta getMeta(@Nonnull Class<? extends Entity> entity);
}
