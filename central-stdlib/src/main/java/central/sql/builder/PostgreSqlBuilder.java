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
import central.lang.Stringx;
import central.sql.query.Conditions;
import central.sql.SqlExecutor;
import central.sql.SqlScript;
import central.sql.SqlType;
import central.sql.builder.script.index.DropIndexScript;
import central.sql.meta.entity.EntityMeta;
import central.util.*;
import jakarta.annotation.Nonnull;
import lombok.SneakyThrows;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLSyntaxErrorException;
import java.util.Collections;
import java.util.List;

/**
 * PostgreSql
 *
 * @author Alan Yeh
 * @since 2022/08/03
 */
public class PostgreSqlBuilder extends StandardSqlBuilder {
    @Override
    public String handleSqlType(SqlType type, Integer length) {
        return switch (type) {
            case STRING -> "VARCHAR(" + Objectx.getOrDefault(length, 32) + ")";
            case CLOB -> "TEXT";
            case BLOB -> "BYTEA";
            case INTEGER -> "INTEGER";
            case LONG -> "BIGINT";
            case BOOLEAN -> "BOOLEAN";
            case BIG_DECIMAL -> "VARCHAR(" + Objectx.getOrDefault(length, 256) + ")";
            case DATETIME -> "TIMESTAMP";
            default -> throw new RuntimeException("不支持的数据类型: " + type);
        };
    }

    /**
     * 构建 Update
     *
     * @param includeNull 是否更新值为 NULL 的属性
     */
    @SneakyThrows({IllegalAccessException.class, InvocationTargetException.class})
    protected SqlScript forUpdate(SqlExecutor executor, EntityMeta meta, Object entity, Conditions<?> conditions, boolean includeNull) throws SQLSyntaxErrorException {
        conditions = Conditions.of(conditions);
        // UPDATE ${TABLE} AS a SET a.col = ? WHERE id = ? AND condition1 = ?
        Assertx.mustInstanceOf(meta.getType(), entity, SQLSyntaxErrorException::new, "entity 必须是 {} 类型", meta.getType().getName());

        var sql = new StringBuilder(Stringx.format("UPDATE\n  {} AS a\n", this.processTable(meta.getTableName(executor.getSource().getConversion()))));
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
                setSql.append("  ").append(this.processColumn(property.getColumnName(executor.getSource().getConversion()))).append(" = NULL");
            } else {
                setSql.append("  ").append(this.processColumn(property.getColumnName(executor.getSource().getConversion()))).append(" = ?");
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
            sql.append("WHERE\n  ").append(whereSql);
            args.addAll(whereArgs);
        }

        return new SqlScript(sql.toString(), args);
    }

    protected StringBuilder applyPage(StringBuilder sql, Long offset, Long pageSize) {
        sql.append("LIMIT ").append(pageSize).append(" OFFSET ").append(offset);
        return sql;
    }

    @Override
    public @Nonnull List<SqlScript> forDropIndex(@Nonnull DropIndexScript script) throws SQLSyntaxErrorException {
        // DROP INDEX `IDX_X_ACCOUNT_USERNAME`;

        var result = new SqlScript(Stringx.format("DROP INDEX {}", this.processIndex(script.getName())));
        return Collections.singletonList(result);
    }
}
