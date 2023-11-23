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

package central.sql.datasource.migration;

import central.sql.SqlExecutor;
import central.util.Version;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.sql.SQLException;

/**
 * 数据库迁移工具
 *
 * @author Alan Yeh
 * @since 2022/09/19
 */
public interface DataSourceMigrator {
    /**
     * 迁移名
     * 不同的迁移名之间的版本可以共存
     */
    @Nonnull
    String getName();

    /**
     * 基线版本
     * 低于基线版本的 Migration 将不被执行
     */
    @Nullable
    Version getBaseline();

    /**
     * 目标版本
     * baseline < version <= target 的 Migration 将被执行
     */
    @Nonnull
    Version getTarget();

    /**
     * 添加迁移
     *
     * @param migration 迁移
     */
    void addMigration(Migration migration);

    /**
     * 升级 Sql 执行器
     *
     * @param executor Sql 执行器
     */
    void upgrade(SqlExecutor executor) throws SQLException;

    /**
     * 降级 Sql 执行器
     * @param executor
     * @throws SQLException
     */
    void downgrade(SqlExecutor executor) throws SQLException;
}
