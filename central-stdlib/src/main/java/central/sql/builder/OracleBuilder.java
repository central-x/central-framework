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

package central.sql.builder;

import central.lang.Assertx;
import central.sql.*;
import central.sql.meta.entity.EntityMeta;
import central.sql.meta.entity.ForeignMeta;
import central.sql.meta.entity.ForeignTableMeta;
import central.sql.query.Columns;
import central.sql.query.Conditions;
import central.sql.query.Orders;
import central.util.Collectionx;
import central.util.Listx;
import central.util.Setx;
import central.lang.Stringx;
import lombok.SneakyThrows;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLSyntaxErrorException;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Oracle 方言
 * 主要是很多 SQL 不支持 AS 语法，分页方式不一样
 *
 * @author Alan Yeh
 * @since 2022/08/03
 */
public class OracleBuilder extends StandardSqlBuilder {

    @Override
    public String handleSqlType(SqlType type, Integer length) {
        if (SqlType.DATETIME.isCompatibleWith(type)) {
            return "TIMESTAMP";
        } else {
            return super.handleSqlType(type, length);
        }
    }

    /**
     * 和 StandardSqlBuilder，主要去掉了 AS
     */
    @Override
    public SqlScript forCountBy(SqlExecutor executor, EntityMeta meta, Conditions<?> conditions) throws SQLSyntaxErrorException {
        conditions = Conditions.of(conditions);
        // SELECT COUNT( DISTINCT a.ID ) FROM ${TABLE} a

        var sql = new StringBuilder(Stringx.format("SELECT COUNT( DISTINCT a.{} ) FROM {} a\n", processColumn(meta.getId().getColumnName(executor.getSource().getConversion()))));
        var args = Listx.newArrayList();

        var whereSql = new StringBuilder();
        preprocessingConditions(conditions);
        // 查找此次查询，会使用哪些关联查询
        var aliases = getAliases(conditions);

        // 处理 LEFT JOIN
        for (var alias : aliases) {
            while (!"a".equals(alias)) {
                var foreign = meta.getForeign(alias);
                if (foreign != null) {
                    applyJoin(executor, meta, foreign, sql);
                    break;
                }

                var foreignTable = meta.getForeignTable(alias);
                if (foreignTable != null) {
                    applyJoin(executor, meta, foreignTable, sql);
                    break;
                }

                throw new SQLSyntaxErrorException(Stringx.format("在 {} 没有找到关联关系 {}，请改正后再试", meta.getType().getSimpleName(), alias));
            }
        }

        // 处理查询条件
        applyConditions(executor, meta, whereSql, args, conditions);

        if (whereSql.length() > 0) {
            sql.append("WHERE\n")
                    .append(whereSql);
        }

        return new SqlScript(sql.toString(), args);
    }

    /**
     * 和 StandardSqlBuilder，主要去掉了 AS
     */
    @Override
    public SqlScript forFindBy(SqlExecutor executor, EntityMeta meta, Long first, Long offset, Columns<?> columns, Conditions<?> conditions, Orders<?> orders) throws SQLSyntaxErrorException {
        // SELECT DISTINCT a.* FROM ${TABLE} a
        // SELECT DISTINCT a.column1, a.column2 FORM ${TABLE} a

        columns = Columns.of(columns);
        conditions = Conditions.of(conditions);
        orders = Orders.of(orders);

        String colSql;
        if (columns.isEmpty()) {
            // SELECT DISTINCT a.* FROM ${TABLE} AS a
            colSql = "a.*";
        } else {
            // SELECT DISTINCT a.column1, a.column2 FORM ${TABLE} AS a
            colSql = columns.stream().map(it -> meta.getProperty(it.getProperty())).filter(Objects::nonNull)
                    .map(it -> "a." + this.processColumn(it.getColumnName(executor.getSource().getConversion()))).distinct().collect(Collectors.joining(", "));
        }
        var sql = new StringBuilder(Stringx.format("SELECT {} FROM {} a\n", colSql, this.processTable(meta.getTableName(executor.getSource().getConversion()))));
        var args = Listx.newArrayList();
        var whereSql = new StringBuilder();

        preprocessingConditions(conditions);
        // 查找此次查询，会使用哪些关联查询
        var aliases = getAliases(conditions);

        if (aliases.size() >= 1) {
            if (aliases.size() > 1 || !"a".equals(Setx.getAnyOrNull(aliases))) {
                // 存在关联查询，会有重复数据，需要去重
                sql = new StringBuilder(Stringx.format("SELECT DISTINCT {} FROM {} a\n", colSql, this.processTable(meta.getTableName(executor.getSource().getConversion()))));
            }
        }

        // 处理 LEFT JOIN
        for (var alias : aliases) {
            while (!"a".equals(alias)) {
                var foreign = meta.getForeign(alias);
                if (foreign != null) {
                    applyJoin(executor, meta, foreign, sql);
                    break;
                }
                var foreignTable = meta.getForeignTable(alias);
                if (foreignTable != null) {
                    applyJoin(executor, meta, foreignTable, sql);
                    break;
                }

                throw new SQLSyntaxErrorException(Stringx.format("在 {} 没有找到关联关系 {}，请改正后再试", meta.getType().getSimpleName(), alias));
            }
        }

        // 处理查询条件
        applyConditions(executor, meta, whereSql, args, conditions);

        if (whereSql.length() > 0) {
            sql.append("WHERE\n").append(whereSql).append("\n");
        }
        // 处理排序
        this.applyOrders(executor, meta, orders, sql);

        if (first != null) {
            if (offset == null) {
                offset = 0L;
            }

            sql = applyPage(sql, offset, first);
        }
        return new SqlScript(sql.toString(), args);
    }

    /**
     * 和 StandardSqlBuilder，主要去掉了 AS
     */
    @Override
    public SqlScript forDeleteBy(SqlExecutor executor, EntityMeta meta, Conditions<?> conditions) throws SQLSyntaxErrorException {
        conditions = Conditions.of(conditions);
        // DELETE FROM ${TABLE} a WHERE

        var sql = new StringBuilder(Stringx.format("DELETE FROM {} a\n", this.processTable(meta.getTableName(executor.getSource().getConversion()))));
        var args = new LinkedList<>();

        if (Collectionx.isNotEmpty(conditions)) {
            var whereSql = new StringBuilder();

            preprocessingConditions(conditions);
            // 查找此次查询，会使用哪些关联查询
            Set<String> aliases = getAliases(conditions);

            Assertx.mustTrue(Setx.isNullOrEmpty(aliases) || (aliases.size() == 1 && "a".equals(Setx.getAnyOrNull(aliases))), SQLSyntaxErrorException::new, "DELETE 不支持外键条件");

            // 处理过滤条件
            applyConditions(executor, meta, whereSql, args, conditions);

            if (!whereSql.isEmpty()) {
                sql.append("WHERE\n").append(whereSql);
            }
        }

        return new SqlScript(sql.toString(), args);
    }

    /**
     * 和 StandardSqlBuilder，主要去掉了 AS
     */
    @Override
    @SneakyThrows({IllegalAccessException.class, InvocationTargetException.class})
    protected SqlScript forUpdate(SqlExecutor executor, EntityMeta meta, Object entity, Conditions<?> conditions, boolean includeNull) throws SQLSyntaxErrorException {
        conditions = Conditions.of(conditions);
        // UPDATE ${TABLE} a set a.col = ? where id = ? and condition1 = ?
        Assertx.mustInstanceOf(meta.getType(), entity, SQLSyntaxErrorException::new, "entity 必须是 {} 类型", meta.getType().getName());

        var sql = new StringBuilder(Stringx.format("UPDATE {} a\n", this.processTable(meta.getTableName(executor.getSource().getConversion()))));
        var args = Listx.newArrayList();
        var whereSql = new StringBuilder();
        var whereArgs = Listx.newArrayList();
        var setSql = new StringBuilder();
        var setArgs = Listx.newArrayList();

        // 构建默认的 where 条件
        if (Collectionx.isNullOrEmpty(conditions)) {
            // 如果更新条件为 null，则要求必须使用 id 进行更新
            var id = meta.getId().getDescriptor().getReadMethod().invoke(entity);
            Assertx.mustNotNull(id, SQLSyntaxErrorException::new, "entity#{} 必须不为空", meta.getId().getName());
            conditions = Conditions.of(conditions).eq(meta.getId().getName(), id);
        }

        // 处理 SET 语句
        var properties = meta.getProperties();

        for (var property : properties) {
            if (!property.isUpdatable() || !property.isInsertable()) {
                continue;
            }
            if (property.isPrimary()) {
                // 主键不能更新
                continue;
            }

            var value = property.getDescriptor().getReadMethod().invoke(entity);
            if (value == null && !includeNull) {
                // 不更新空字段
                continue;
            }

            if (setSql.length() > 0) {
                setSql.append(",\n");
            }

            if (value == null) {
                setSql.append("  a.").append(this.processColumn(property.getColumnName(executor.getSource().getConversion()))).append(" = NULL");
            } else {
                setSql.append("  a.").append(this.processColumn(property.getColumnName(executor.getSource().getConversion()))).append(" = ?");
                setArgs.add(this.convertValue(executor, meta, property, value));
            }
        }

        Assertx.mustTrue(!setSql.isEmpty(), SQLSyntaxErrorException::new, "找不到待更新字段");

        sql.append("SET\n").append(setSql).append("\n");
        args.addAll(setArgs);

        preprocessingConditions(conditions);
        // 查找此次查询，会使用哪些关联查询
        var aliases = getAliases(conditions);

        Assertx.mustTrue(Setx.isNullOrEmpty(aliases) || (aliases.size() == 1 && "a".equals(Setx.getAnyOrNull(aliases))), SQLSyntaxErrorException::new, "UPDATE 不支持外键条件");

        // 处理条件
        applyConditions(executor, meta, whereSql, whereArgs, conditions);

        if (!whereSql.isEmpty()) {
            sql.append("WHERE\n").append(whereSql);
            args.addAll(whereArgs);
        }

        return new SqlScript(sql.toString(), args);
    }

    /**
     * ORACLE 的分页
     */
    @Override
    protected StringBuilder applyPage(StringBuilder sql, Long offset, Long pageSize) {
        // Oracle 需要包装一层，根据 rownum 分页
        return new StringBuilder("SELECT * FROM (SELECT pager_t.*, ROWNUM pager_rn FROM (")
                .append(sql)
                .append(") pager_t WHERE ROWNUM <= ").append(offset + pageSize).append(")")
                .append(" WHERE pager_rn >= ").append(offset + 1);
    }

    /**
     * 和 StandardSqlBuilder，主要去掉了 AS
     */
    @Override
    protected void applyJoin(SqlExecutor executor, EntityMeta main, ForeignMeta foreign, StringBuilder sql) {
        var target = foreign.getTarget();

        sql.append(Stringx.format("  LEFT JOIN {} {} ON a.{} = {}.{} \n",
                this.processTable(target.getTableName(executor.getSource().getConversion())),
                foreign.getAlias(),
                this.processColumn(foreign.getProperty().getColumnName(executor.getSource().getConversion())),
                foreign.getAlias(),
                this.processColumn(foreign.getReferencedProperty().getColumnName(executor.getSource().getConversion()))));
    }

    /**
     * 和 StandardSqlBuilder，主要去掉了 AS
     */
    @Override
    protected void applyJoin(SqlExecutor executor, EntityMeta main, ForeignTableMeta foreign, StringBuilder sql) {
        var rel = foreign.getEntity();
        var target = foreign.getTarget();

        sql.append(Stringx.format("  LEFT JOIN {} {}_rel ON a.{} = {}_rel.{}\n",
                this.processTable(rel.getTableName(executor.getSource().getConversion())),
                foreign.getAlias(),
                this.processColumn(foreign.getProperty().getColumnName(executor.getSource().getConversion())),
                foreign.getAlias(),
                this.processColumn(foreign.getRelationProperty().getColumnName(executor.getSource().getConversion()))));

        sql.append(Stringx.format("  LEFT JOIN {} {} ON {}_rel.{} = {}.{}\n",
                this.processTable(target.getTableName(executor.getSource().getConversion())),
                foreign.getAlias(),
                foreign.getAlias(),
                this.processColumn(foreign.getTargetRelationProperty().getColumnName(executor.getSource().getConversion())),
                foreign.getAlias(),
                this.processColumn(foreign.getTargetProperty().getColumnName(executor.getSource().getConversion()))));
    }
}
