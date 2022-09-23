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

import central.lang.Stringx;
import central.sql.Conditions;
import central.sql.Orders;
import central.sql.builder.script.column.AddColumnScript;
import central.sql.builder.script.column.DropColumnScript;
import central.sql.builder.script.column.RenameColumnScript;
import central.sql.builder.script.index.AddIndexScript;
import central.sql.builder.script.index.DropIndexScript;
import central.sql.builder.script.table.AddTableScript;
import central.sql.builder.script.table.DropTableScript;
import central.sql.builder.script.table.RenameTableScript;
import central.bean.Treeable;
import central.lang.Assertx;
import central.sql.*;
import central.sql.meta.entity.EntityMeta;
import central.sql.meta.entity.ForeignMeta;
import central.sql.meta.entity.ForeignTableMeta;
import central.sql.meta.entity.PropertyMeta;
import central.util.*;
import lombok.SneakyThrows;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLSyntaxErrorException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 标准 Sql 构建工具
 *
 * @author Alan Yeh
 * @since 2022/08/01
 */
public abstract class StandardSqlBuilder implements SqlBuilder {
    @Override
    public String processTable(String table) {
        return "\"" + table + "\"";
    }

    @Override
    public String processColumn(String column) {
        return "\"" + column + "\"";
    }

    @Override
    public String processIndex(String index) {
        return "\"" + index + "\"";
    }

    @Override
    public SqlScript forCountBy(SqlExecutor executor, EntityMeta meta, Conditions conditions) throws SQLSyntaxErrorException {
        conditions = Conditions.where(conditions);
        // SELECT COUNT( DISTINCT a.ID ) FROM ${TABLE} AS a

        var sql = new StringBuilder(Stringx.format("SELECT COUNT( DISTINCT a.{} ) FROM {} AS a\n", processColumn(meta.getId().getColumnName(executor.getSource().getConversion())), processTable(meta.getTableName(executor.getSource().getConversion()))));
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
            sql.append("WHERE\n  ")
                    .append(whereSql);
        }

        return new SqlScript(sql.toString(), args);
    }

    @Override
    public SqlScript forFindBy(SqlExecutor executor, EntityMeta meta, Long first, Long offset, Conditions conditions, Orders orders) throws SQLSyntaxErrorException {
        conditions = Conditions.where(conditions);
        orders = Orders.order(orders);
        // SELECT DISTINCT a.* FROM ${TABLE} AS a
        var sql = new StringBuilder(Stringx.format("SELECT a.* FROM {} AS a\n", this.processTable(meta.getTableName(executor.getSource().getConversion()))));
        var args = Listx.newArrayList();
        var whereSql = new StringBuilder();

        preprocessingConditions(conditions);
        // 查找此次查询，会使用哪些关联查询
        var aliases = getAliases(conditions);

        if (aliases.size() >= 1) {
            if (aliases.size() > 1 || !"a".equals(Setx.getAny(aliases))) {
                // 存在关联查询，会有重复数据，需要去重
                sql = new StringBuilder(Stringx.format("SELECT DISTINCT a.* FROM {} AS a\n", this.processTable(meta.getTableName(executor.getSource().getConversion()))));
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
            sql.append("WHERE\n  ").append(whereSql).append("\n");
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


    @Override
    public SqlScript forInsert(SqlExecutor executor, EntityMeta meta, Object entity) throws SQLSyntaxErrorException {
        var batchScript = this.forInsertBatch(executor, meta, Listx.of(entity));
        return new SqlScript(batchScript.getSql(), Listx.getFirst(batchScript.getArgs()));
    }

    @Override
    @SneakyThrows({IllegalAccessException.class, InvocationTargetException.class})
    public SqlBatchScript forInsertBatch(SqlExecutor executor, EntityMeta meta, List<Object> entities) throws SQLSyntaxErrorException {
        // INSERT INTO ${TABLE}(ID, COL1, COL2, ...) VALUES (?, ?, ?, ...)

        // 构建 Sql
        var sql = new StringBuilder(Stringx.format("INSERT INTO {}(", this.processTable(meta.getTableName(executor.getSource().getConversion()))));
        var valueSql = new StringBuilder(") VALUES (");

        var properties = meta.getProperties();
        for (var property : properties) {
            sql.append(this.processColumn(property.getColumnName(executor.getSource().getConversion()))).append(", ");
            valueSql.append("?, ");
        }

        sql.delete(sql.length() - 2, sql.length()).append(valueSql.delete(valueSql.length() - 2, valueSql.length())).append(")");

        SqlBatchScript script = new SqlBatchScript(sql.toString());

        // 构建参数
        for (var entity : entities) {
            var args = Listx.newArrayList();
            for (var property : properties) {
                var value = property.getDescriptor().getReadMethod().invoke(entity);
                if (value == null && property.isPrimary()) {
                    // 主键
                    // 此时需要自动生成主键，并回写到实体
                    value = Guidx.nextID();
                    property.getDescriptor().getWriteMethod().invoke(entity, value);
                }
                args.add(this.convertValue(executor, meta, property, value));
            }

            script.addArgs(args);
        }
        return script;
    }

    @Override
    public SqlScript forDeleteBy(SqlExecutor executor, EntityMeta meta, Conditions conditions) throws SQLSyntaxErrorException {
        conditions = Conditions.where(conditions);
        // DELETE FROM ${TABLE} AS a

        var sql = new StringBuilder(Stringx.format("DELETE FROM {} AS a\n", this.processTable(meta.getTableName(executor.getSource().getConversion()))));
        var args = Listx.newArrayList();

        if (Collectionx.isNotEmpty(conditions)) {
            var whereSql = new StringBuilder();

            preprocessingConditions(conditions);

            // 看看会使用哪些关联查询
            var aliases = this.getAliases(conditions);

            Assertx.mustTrue(Setx.isNullOrEmpty(aliases) || (aliases.size() == 1 && "a".equals(Setx.getAny(aliases))), SQLSyntaxErrorException::new, "DELETE 不支持外键条件");

            // 处理条件
            applyConditions(executor, meta, whereSql, args, conditions);

            if (whereSql.length() > 0) {
                sql.append("WHERE\n  ")
                        .append(whereSql);
            }
        }

        return new SqlScript(sql.toString(), args);
    }

    @Override
    public SqlScript forUpdateBy(SqlExecutor executor, EntityMeta meta, Object entity, Conditions conditions) throws SQLSyntaxErrorException {
        return forUpdate(executor, meta, entity, conditions, false);
    }

    @Override
    public SqlScript forUpdate(SqlExecutor executor, EntityMeta meta, Object entity, Conditions conditions) throws SQLSyntaxErrorException {
        return forUpdate(executor, meta, entity, conditions, true);
    }

    /**
     * 构建 Update
     *
     * @param includeNull 是否更新值为 NULL 的属性
     */
    @SneakyThrows({IllegalAccessException.class, InvocationTargetException.class})
    protected SqlScript forUpdate(SqlExecutor executor, EntityMeta meta, Object entity, Conditions conditions, boolean includeNull) throws SQLSyntaxErrorException {
        conditions = Conditions.where(conditions);
        // UPDATE ${TABLE} AS a set a.col = ? where id = ? and condition1 = ?
        Assertx.mustInstanceOf(meta.getType(), entity, SQLSyntaxErrorException::new, "entity 必须是 {} 类型", meta.getType().getName());

        var sql = new StringBuilder(Stringx.format("UPDATE {} AS a\n", this.processTable(meta.getTableName(executor.getSource().getConversion()))));
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
            conditions = Conditions.where().eq(meta.getId().getName(), id);
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

        Assertx.mustTrue(Setx.isNullOrEmpty(aliases) || (aliases.size() == 1 && "a".equals(Setx.getAny(aliases))), SQLSyntaxErrorException::new, "UPDATE 不支持外键条件");

        // 处理条件
        applyConditions(executor, meta, whereSql, whereArgs, conditions);

        if (!whereSql.isEmpty()) {
            sql.append("WHERE\n  ").append(whereSql);
            args.addAll(whereArgs);
        }

        return new SqlScript(sql.toString(), args);
    }

    // 预处理条件，将 alias 为空的，设为 a
    protected void preprocessingConditions(Conditions conditions) {
        for (var condition : conditions) {
            if (Collectionx.isNullOrEmpty(condition.getChildren())) {
                if (Stringx.isNullOrBlank(condition.getAlias())) {
                    condition.setProperty("a." + condition.getProperty());
                }
            }
        }
    }

    protected Set<String> getAliases(Conditions conditions) {
        Set<String> alias = new HashSet<>();

        for (Conditions.Condition condition : conditions) {
            // 如果在条件里包含了 .，则说明要外键查询
            if (Stringx.isNotEmpty(condition.getAlias())) {
                alias.add(condition.getAlias());
            } else {
                alias.add("a");
            }
        }

        return alias;
    }

    protected void applyJoin(SqlExecutor executor, EntityMeta main, ForeignMeta foreign, StringBuilder sql) {
        EntityMeta target = foreign.getTarget();

        sql.append(Stringx.format("  LEFT JOIN {} AS {} ON a.{} = {}.{} \n",
                this.processTable(target.getTableName(executor.getSource().getConversion())),
                foreign.getAlias(),
                this.processColumn(foreign.getProperty().getColumnName(executor.getSource().getConversion())),
                foreign.getAlias(),
                this.processColumn(foreign.getReferencedProperty().getColumnName(executor.getSource().getConversion()))));
    }

    protected void applyJoin(SqlExecutor executor, EntityMeta main, ForeignTableMeta foreign, StringBuilder sql) {
        EntityMeta rel = foreign.getEntity();
        EntityMeta target = foreign.getTarget();

        sql.append(Stringx.format("  LEFT JOIN {} AS {}_rel ON a.{} = {}_rel.{}\n",
                this.processTable(rel.getTableName(executor.getSource().getConversion())),
                foreign.getAlias(),
                this.processColumn(foreign.getProperty().getColumnName(executor.getSource().getConversion())),
                foreign.getAlias(),
                this.processColumn(foreign.getRelationProperty().getColumnName(executor.getSource().getConversion()))));

        sql.append(Stringx.format("  LEFT JOIN {} AS {} ON {}_rel.{} = {}.{}\n",
                this.processTable(target.getTableName(executor.getSource().getConversion())),
                foreign.getAlias(),
                foreign.getAlias(),
                this.processColumn(foreign.getTargetRelationProperty().getColumnName(executor.getSource().getConversion())),
                foreign.getAlias(),
                this.processColumn(foreign.getTargetProperty().getColumnName(executor.getSource().getConversion()))));
    }

    protected void applyOrders(SqlExecutor executor, EntityMeta main, Orders orders, StringBuilder sql) throws SQLSyntaxErrorException {
        if (Collectionx.isNullOrEmpty(orders)) {
            return;
        }

        StringBuilder orderBy = new StringBuilder();

        for (Orders.Order order : orders) {
            // 主表排序
            PropertyMeta property = main.getProperty(order.getProperty());
            Assertx.mustNotNull(property, SQLSyntaxErrorException::new, "在 {} 没有找到属性 {}，请改正后再试", main.getType().getSimpleName(), order.getProperty());

            // 构建主表查询条件
            applyOrder(executor, "a", orderBy, order, property);
        }

        if (orderBy.length() > 0) {
            sql.append("ORDER BY\n").append(orderBy).append("\n");
        }
    }

    protected void applyOrder(SqlExecutor executor, String alias, StringBuilder orderBy, Orders.Order order, PropertyMeta property) throws SQLSyntaxErrorException {
        if (orderBy.length() > 0) {
            orderBy.append(",\n");
        }

        if (Stringx.isNotBlank(alias)) {
            alias = alias + ".";
        } else {
            alias = "";
        }

        if (order.isDesc()) {
            orderBy.append(Stringx.format("  {}{} DESC", alias, this.processColumn(property.getColumnName(executor.getSource().getConversion()))));
        } else {
            orderBy.append(Stringx.format("  {}{} ASC", alias, this.processColumn(property.getColumnName(executor.getSource().getConversion()))));
        }
    }

    protected void applyConditions(SqlExecutor executor, EntityMeta meta, StringBuilder where, List<Object> args, Conditions conditions) {
        // 构建条件树
        List<Conditions.Condition> expression = Treeable.build(conditions.clone(), Conditions.Condition.DEFAULT_COMPARATOR);

        for (int i = 0, length = expression.size(); i < length; i++) {
            Conditions.Condition condition = expression.get(i);
            if (i != 0) {
                where.append(" ").append(condition.getConnector()).append(" ");
            }
            applyCondition(executor, meta, where, args, condition);
        }
    }

    protected void applyCondition(SqlExecutor executor, @Nonnull EntityMeta meta, StringBuilder where, List<Object> args, Conditions.Condition condition) {
        if (Collectionx.isNotEmpty(condition.getChildren())) {
            // 这是一个用于嵌套的分组条件
            where.append("(");
            for (int i = 0, length = condition.getChildren().size(); i < length; i++) {
                Conditions.Condition child = condition.getChildren().get(i);
                if (i != 0) {
                    where.append(" ").append(child.getConnector()).append(" ");
                }
                applyCondition(executor, meta, where, args, child);
            }
            where.append(")");
        } else {
            final EntityMeta target;
            String alias = condition.getAlias();

            if (Stringx.isNullOrEmpty(alias) || "a".equals(alias)) {
                target = meta;
            } else {
                ForeignMeta foreign = meta.getForeign(alias);
                if (foreign != null) {
                    target = foreign.getTarget();
                } else {
                    ForeignTableMeta foreignTable = meta.getForeignTable(alias);
                    if (foreignTable != null) {
                        target = foreignTable.getTarget();
                    } else {
                        target = null;
                    }
                }
            }

            if (target == null) {
                throw new IllegalArgumentException(Stringx.format("在 {} 没有找到关联关系 {}，请改正后再试", meta.getType().getSimpleName(), alias));
            }

            PropertyMeta property;
            if (Stringx.isNullOrBlank(alias)) {
                property = target.getProperty(condition.getProperty());
            } else {
                property = target.getProperty(condition.getProperty().substring(alias.length() + 1));
            }
            if (property == null) {
                throw new IllegalArgumentException(Stringx.format("在 {} 的外键关联 {} 中没有找到属性 {}，请改正后再试", meta.getType().getSimpleName(), alias, condition.getProperty().substring(condition.getAlias().length() + 1)));
            }

            if (Stringx.isNullOrBlank(alias)) {
                alias = "";
            } else {
                alias = alias + ".";
            }

            switch (condition.getOperator()) {
                case EQ, NE, GT, GE, LT, LE, LIKE, NOT_LIKE -> {
                    where.append(Stringx.format(condition.getOperator().getValue(), alias + this.processColumn(property.getColumnName(executor.getSource().getConversion())), "?"));
                    args.add(convertValue(executor, target, property, condition.getValues()[0]));
                }
                case BETWEEN, NOT_BETWEEN -> {
                    where.append(Stringx.format(condition.getOperator().getValue(), alias + this.processColumn(property.getColumnName(executor.getSource().getConversion())), "?", "?"));
                    args.add(convertValue(executor, target, property, condition.getValues()[0]));
                    args.add(convertValue(executor, target, property, condition.getValues()[1]));
                }
                case IS_NULL, IS_NOT_NULL -> {
                    where.append(Stringx.format(condition.getOperator().getValue(), alias + this.processColumn(property.getColumnName(executor.getSource().getConversion()))));
                }
                case IN, NOT_IN -> {
                    where.append(Stringx.format(condition.getOperator().getValue(), alias + this.processColumn(property.getColumnName(executor.getSource().getConversion())), IntStream.range(0, condition.getValues().length).mapToObj(i -> "?").collect(Collectors.joining(", "))));
                    args.addAll(Arrays.stream(condition.getValues()).map(it -> convertValue(executor, target, property, it)).toList());
                }
            }
        }
    }

    protected StringBuilder applyPage(StringBuilder sql, Long offset, Long pageSize) {
        // TODO 注意分页起始页
        sql.append("LIMIT ").append(offset).append(", ").append(pageSize);
        return sql;
    }

    protected Object convertValue(SqlExecutor executor, EntityMeta meta, PropertyMeta property, Object source) {
        if (source == null) {
            return null;
        }

        Assertx.mustTrue(executor.getConverter().support(source.getClass(), property.getDescriptor().getPropertyType()),
                IllegalArgumentException::new, "{}.{} 是 {} 类型，无法转换 {}", meta.getType().getSimpleName(), property.getDescriptor().getName(), property.getDescriptor().getPropertyType().getSimpleName(), source);

        Object value = executor.getConverter().convert(source, property.getDescriptor().getPropertyType());

        if (value != null && property.isEncrypted()) {
            return executor.getCipher().encrypt(value.toString());
        } else {
            return value;
        }
    }

    @Override
    public String handleSqlType(SqlType type, Integer length) {
        return switch (type) {
            case STRING -> "VARCHAR(" + Objectx.get(length, 32) + ")";
            case BLOB -> "BLOB";
            case INTEGER -> "INT";
            case LONG -> "BIGINT";
            case BOOLEAN -> "TINYINT";
            case BIG_DECIMAL -> "VARCHAR(" + Objectx.get(length, 128) + ")";
            case DATETIME -> "DATETIME";
            default -> throw new RuntimeException("不支持的数据类型: " + type);
        };
    }

    @Override
    public List<SqlScript> forAddTable(AddTableScript script) throws SQLSyntaxErrorException {
        // CREATE TABLE MC_AUTH_CREDENTIAL (
        //     "ID"             VARCHAR(36)    PRIMARY KEY NOT NULL,
        //     "ACCOUNT_ID"     VARCHAR(36)    NOT NULL,
        //     "TYPE"           VARCHAR(255)   NOT NULL,
        //     "CREDENTIAL"     VARCHAR(255)   NOT NULL,
        //     "ENABLED"        VARCHAR(2)     NOT NULL,
        //     "CREATE_DATE"    DATETIME       NOT NULL,
        //     "CREATOR_ID"     VARCHAR(36)    NOT NULL,
        //     "MODIFY_DATE"    DATETIME       NOT NULL,
        //     "MODIFIER_ID"    VARCHAR(36)    NOT NULL,
        //     "TENANT_CODE"    VARCHAR(36)    NOT NULL
        //);
        //
        // COMMENT ON TABLE MC_AUTH_CREDENTIAL IS '第三方应用登录授权';
        // COMMENT ON COLUMN MC_AUTH_CREDENTIAL."ID" IS '主键';
        // COMMENT ON COLUMN MC_AUTH_CREDENTIAL."ACCOUNT_ID" IS '所属账户主键';
        // COMMENT ON COLUMN MC_AUTH_CREDENTIAL."TYPE" IS '凭证类型，参考字典表[mc_dic_credential_type]';
        // COMMENT ON COLUMN MC_AUTH_CREDENTIAL."CREDENTIAL" IS '凭证，通常是第三方授权系统的标识';
        // COMMENT ON COLUMN MC_AUTH_CREDENTIAL."ENABLED" IS '禁用状态(0:禁用，1:启用)';
        // COMMENT ON COLUMN MC_AUTH_CREDENTIAL."CREATE_DATE" IS '创建时间';
        // COMMENT ON COLUMN MC_AUTH_CREDENTIAL."CREATOR_ID" IS '创建人主键';
        // COMMENT ON COLUMN MC_AUTH_CREDENTIAL."MODIFY_DATE" IS '修改时间';
        // COMMENT ON COLUMN MC_AUTH_CREDENTIAL."MODIFIER_ID" IS '修改人主键';
        // COMMENT ON COLUMN MC_AUTH_CREDENTIAL."TENANT_CODE" IS '租户标识';

        var result = new ArrayList<SqlScript>(script.getColumns().size() + 1);
        var builder = new StringBuilder("CREATE TABLE ").append(this.processTable(script.getName())).append(" (\n");
        result.add(new SqlScript(Stringx.format("COMMENT ON TABLE {} IS '{}'", this.processTable(script.getName()), script.getRemarks())));

        AddTableScript.Column primaryKey = null;
        var columnBuilder = new StringBuilder();
        for (var column : script.getColumns()) {
            if (!columnBuilder.isEmpty()) {
                columnBuilder.append(",\n");
            }
            columnBuilder.append("    ").append(Stringx.padding(this.processColumn(column.getName()), 30, ' ')).append(Stringx.padding(this.handleSqlType(column.getType(), column.getSize()), 20, ' '));
            if (column.isPrimaryKey()) {
                if (primaryKey != null) {
                    throw new SQLSyntaxErrorException("生成 {} 的建表语句出错: 不允许声明多个主键", script.getName());
                }
                primaryKey = column;
                columnBuilder.append("PRIMARY KEY NOT NULL");
            } else {
                columnBuilder.append("NOT NULL");
            }
            result.add(new SqlScript(Stringx.format("COMMENT ON COLUMN {}.{} IS '{}'", this.processTable(script.getName()), this.processColumn(column.getName()), column.getRemarks())));
        }

        builder.append(columnBuilder).append("\n)");
        result.add(0, new SqlScript(builder.toString()));
        return result;
    }

    @Override
    public List<SqlScript> forDropTable(DropTableScript script) throws SQLSyntaxErrorException {
        // DROP TABLE "MC_API";

        var result = new SqlScript(Stringx.format("DROP TABLE {}", this.processTable(script.getName())));
        return Collections.singletonList(result);
    }

    @Override
    public List<SqlScript> forRenameTable(RenameTableScript script) throws SQLSyntaxErrorException {
        // ALTER TABLE "MC_REL_DEPT_PARENT" RENAME TO "MC_REL_DEPT_FUNCTION";

        var result = new SqlScript(Stringx.format("ALTER TABLE {} RENAME TO {}", script.getName(), script.getNewName()));
        return Collections.singletonList(result);
    }

    @Override
    public List<SqlScript> forAddColumn(AddColumnScript script) throws SQLSyntaxErrorException {
        // ALTER TABLE "MC_ORG_DEPT" ADD COLUMN "LEADER_ID" VARCHAR(36);
        // COMMENT ON COLUMN "MC_ORG_DEPT"."LEADER_ID" IS '负责人主键';

        var result = new ArrayList<SqlScript>(2);
        result.add(new SqlScript(Stringx.format("ALTER TABLE {} ADD COLUMN {} {} NOT NULL", this.processTable(script.getTable()), this.processColumn(script.getName()), this.handleSqlType(script.getType(), script.getLength()))));
        result.add(new SqlScript(Stringx.format("COMMENT ON COLUMN {}.{} IS '{}'", this.processTable(script.getTable()), this.processColumn(script.getName()), script.getComment())));
        return result;
    }

    @Override
    public List<SqlScript> forDropColumn(DropColumnScript script) throws SQLSyntaxErrorException {
        // ALTER TABLE "MC_ORG_DEPT" DROP COLUMN "LEADER";

        var result = new SqlScript(Stringx.format("ALTER TABLE {} DROP COLUMN {}", this.processTable(script.getTable()), this.processColumn(script.getName())));
        return Collections.singletonList(result);
    }

    @Override
    public List<SqlScript> forRenameColumn(RenameColumnScript script) throws SQLSyntaxErrorException {
        // ALTER TABLE "MC_ATT_ATTACHMENT" RENAME COLUMN "CODE" TO "KEY";
        // COMMENT ON COLUMN "MC_ATT_ATTACHMENT"."KEY" IS '文件在网盘的存储标识';

        var result = new ArrayList<SqlScript>(2);
        result.add(new SqlScript(Stringx.format("ALTER TABLE {} RENAME COLUMN {} TO {}", this.processTable(script.getTable()), this.processColumn(script.getName()), this.processColumn(script.getNewName()))));
        result.add(new SqlScript(Stringx.format("COMMENT ON COLUMN {}.{} IS '{}'", this.processTable(script.getTable()), this.processColumn(script.getNewName()), script.getRemarks())));
        return result;
    }

    @Override
    public List<SqlScript> forAddIndex(AddIndexScript script) throws SQLSyntaxErrorException {
        // CREATE [UNIQUE] INDEX `IDX_MC_API_CODE` ON `MC_API` ( `CODE` );

        var builder = new StringBuilder("CREATE ");
        if (script.isUnique()) {
            builder.append("UNIQUE ");
        }
        builder.append(Stringx.format("INDEX {} ON {} ({})", this.processTable(script.getName()), this.processTable(script.getTable()), this.processColumn(script.getColumn())));

        var result = new SqlScript(builder.toString());
        return Collections.singletonList(result);
    }

    @Override
    public List<SqlScript> forDropIndex(DropIndexScript script) throws SQLSyntaxErrorException {
        // DROP INDEX `IDX_MC_AUTH_CREDENTIAL_ACCOUNTID` ON `MC_AUTH_CREDENTIAL`;

        var result = new SqlScript(Stringx.format("DROP INDEX {} ON {}", this.processIndex(script.getName()), this.processTable(script.getTable())));
        return Collections.singletonList(result);
    }
}
