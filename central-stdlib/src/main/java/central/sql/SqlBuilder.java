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

import central.sql.builder.script.column.AddColumnScript;
import central.sql.builder.script.column.DropColumnScript;
import central.sql.builder.script.column.RenameColumnScript;
import central.sql.builder.script.index.AddIndexScript;
import central.sql.builder.script.index.DropIndexScript;
import central.sql.builder.script.table.AddTableScript;
import central.sql.builder.script.table.DropTableScript;
import central.sql.builder.script.table.RenameTableScript;
import central.sql.meta.entity.EntityMeta;

import java.sql.SQLSyntaxErrorException;
import java.util.List;

/**
 * Sql Dialect
 *
 * @author Alan Yeh
 * @since 2022/08/01
 */
public interface SqlBuilder {

    /**
     * 处理表名
     *
     * @param table 原始表名
     * @return 处理后的表名
     */
    String processTable(String table);

    /**
     * 处理字段名
     *
     * @param column 字段名
     * @return 处理后的字段名
     */
    String processColumn(String column);

    /**
     * 处理索引名
     * @param index 索引名
     * @return 处理后的索引名
     */
    String processIndex(String index);

    /**
     * Count Sql
     *
     * @param executor   Sql 执行器
     * @param meta       主表元数据
     * @param conditions 筛选条件
     * @return 构建好的 Sql
     */
    SqlScript forCountBy(SqlExecutor executor, EntityMeta meta, Conditions conditions) throws SQLSyntaxErrorException;

    /**
     * Select Sql
     *
     * @param executor   Sql 执行器
     * @param meta       主表元数据
     * @param first      查询前 N 条数据
     * @param offset     跳过前 N 条数据
     * @param conditions 筛选条件
     * @param orders     排序条件
     * @return 构建好的 Sql
     */
    SqlScript forFindBy(SqlExecutor executor, EntityMeta meta, Long first, Long offset, Conditions conditions, Orders orders) throws SQLSyntaxErrorException;

    /**
     * Insert Sql
     *
     * @param executor Sql 执行器
     * @param meta     主表元数据
     * @param entity   实体数据
     * @return 构建好的 Sql
     */
    SqlScript forInsert(SqlExecutor executor, EntityMeta meta, Object entity) throws SQLSyntaxErrorException;

    /**
     * Insert Batch Sql
     *
     * @param executor Sql 执行器
     * @param meta     主表元数据
     * @param entities 实体数据
     * @return 构建好的 Sql
     */
    SqlBatchScript forInsertBatch(SqlExecutor executor, EntityMeta meta, List<Object> entities) throws SQLSyntaxErrorException;

    /**
     * Delete Sql
     *
     * @param executor   Sql 执行器
     * @param meta       主表元数据
     * @param conditions 筛选条件
     * @return 构建好的 Sql
     */
    SqlScript forDeleteBy(SqlExecutor executor, EntityMeta meta, Conditions conditions) throws SQLSyntaxErrorException;

    /**
     * Insert Sql
     *
     * @param executor   Sql 执行器
     * @param meta       主表元数据
     * @param entity     实体数据
     * @param conditions 更新条件
     * @return 构建好的 Sql
     */
    SqlScript forUpdateBy(SqlExecutor executor, EntityMeta meta, Object entity, Conditions conditions) throws SQLSyntaxErrorException;

    /**
     * Insert Sql
     *
     * @param executor   Sql 执行器
     * @param meta       主表元数据
     * @param entity     实体数据
     * @param conditions 更新条件
     * @return 构建好的 Sql
     */
    SqlScript forUpdate(SqlExecutor executor, EntityMeta meta, Object entity, Conditions conditions) throws SQLSyntaxErrorException;

    /**
     * 处理数据类型
     *
     * @param type   Sql 类型
     * @param length 类型长度
     */
    String handleSqlType(SqlType type, Integer length);

    /**
     * 创建表
     */
    List<SqlScript> forAddTable(AddTableScript script) throws SQLSyntaxErrorException;

    /**
     * 删除表
     */
    List<SqlScript> forDropTable(DropTableScript script) throws SQLSyntaxErrorException;

    /**
     * 重命名表
     */
    List<SqlScript> forRenameTable(RenameTableScript script) throws SQLSyntaxErrorException;

    /**
     * 添加字段
     */
    List<SqlScript> forAddColumn(AddColumnScript script) throws SQLSyntaxErrorException;

    /**
     * 删除字段
     */
    List<SqlScript> forDropColumn(DropColumnScript script) throws SQLSyntaxErrorException;

    /**
     * 重命名字段
     */
    List<SqlScript> forRenameColumn(RenameColumnScript script) throws SQLSyntaxErrorException;

    /**
     * 添加索引
     */
    List<SqlScript> forAddIndex(AddIndexScript script) throws SQLSyntaxErrorException;

    /**
     * 删除索引
     */
    List<SqlScript> forDropIndex(DropIndexScript script) throws SQLSyntaxErrorException;
}

