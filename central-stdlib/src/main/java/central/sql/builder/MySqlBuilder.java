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
import central.sql.*;
import central.sql.builder.script.column.AddColumnScript;
import central.sql.builder.script.column.RenameColumnScript;
import central.sql.builder.script.table.AddTableScript;
import central.sql.builder.script.table.RenameTableScript;
import central.sql.meta.entity.EntityMeta;
import central.sql.query.Conditions;
import central.util.*;
import jakarta.annotation.Nonnull;

import java.sql.SQLSyntaxErrorException;
import java.util.Collections;
import java.util.List;

/**
 * MySql 方言
 *
 * @author Alan Yeh
 * @since 2022/08/03
 */
public class MySqlBuilder extends StandardSqlBuilder {

    @Override
    public String processTable(String table) {
        return "`" + table + "`";
    }

    @Override
    public String processColumn(String column) {
        return "`" + column + "`";
    }

    @Override
    public String processIndex(String index) {
        return "`" + index + "`";
    }

    @Override
    public String handleSqlType(SqlType type, Integer length) {
        return switch (type) {
            case STRING -> "VARCHAR(" + Objectx.getOrDefault(length, 32) + ")";
            case CLOB -> "LONGTEXT";
            case BLOB -> "BLOB";
            case INTEGER -> "INT";
            case LONG -> "BIGINT";
            case BOOLEAN -> "TINYINT";
            case DATETIME -> "DATETIME(3)"; // 保留 3 个精度
            case BIG_DECIMAL -> "VARCHAR(" + Objectx.getOrDefault(length, 128) + ")";
            default -> throw new RuntimeException("不支持的数据类型: " + type);
        };
    }

    @Override
    public SqlScript forDeleteBy(SqlExecutor executor, EntityMeta meta, Conditions<?> conditions) throws SQLSyntaxErrorException {
        conditions = Conditions.of(conditions);
        // DELETE a FROM ${TABLE} AS a

        var sql = new StringBuilder(Stringx.format("DELETE a FROM {} AS a\n", this.processTable(meta.getTableName(executor.getSource().getConversion()))));
        var args = Listx.newArrayList();

        if (Collectionx.isNotEmpty(conditions)) {
            var whereSql = new StringBuilder();

            preprocessingConditions(conditions);

            // 看看会使用哪些关联查询
            var aliases = this.getAliases(conditions);

            Assertx.mustTrue(Setx.isNullOrEmpty(aliases) || (aliases.size() == 1 && "a".equals(Setx.getAnyOrNull(aliases))), SQLSyntaxErrorException::new, "DELETE 不支持外键条件");

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
    public @Nonnull List<SqlScript> forAddTable(@Nonnull AddTableScript script) throws SQLSyntaxErrorException {
        // CREATE TABLE `MC_AUTH_CREDENTIAL` (
        //    `ID`                   VARCHAR(36)               NOT NULL     COMMENT '主键',
        //    `ACCOUNT_ID`           VARCHAR(36)               DEFAULT NULL COMMENT '所属账户主键',
        //    `TYPE`                 VARCHAR(255)              DEFAULT NULL COMMENT '凭证类型，参考字典表[mc_dic_credential_type]',
        //    `CREDENTIAL`           VARCHAR(255)              DEFAULT NULL COMMENT '凭证，通常是第三方授权系统的标识',
        //    `ENABLED`              VARCHAR(2)                DEFAULT NULL COMMENT '禁用状态(0:禁用，1:启用)',
        //    `CREATE_DATE`          DATETIME(3)               DEFAULT NULL COMMENT '创建时间',
        //    `CREATOR_ID`           VARCHAR(36)               DEFAULT NULL COMMENT '创建人主键',
        //    `MODIFY_DATE`          DATETIME(3)               DEFAULT NULL COMMENT '修改时间',
        //    `MODIFIER_ID`          VARCHAR(36)               DEFAULT NULL COMMENT '修改人主键',
        //    `TENANT_CODE`          VARCHAR(36)               DEFAULT NULL COMMENT '租户标识',
        //  PRIMARY KEY (`ID`)
        // ) COMMENT='第三方应用登录授权';

        var result = new StringBuilder("CREATE TABLE ").append(this.processTable(script.getName())).append(" (\n");
        AddTableScript.Column primaryKey = null;
        for (AddTableScript.Column column : script.getColumns()) {
            result.append("    ").append(Stringx.paddingRight(this.processColumn(column.getName()), 30, ' ')).append(Stringx.paddingRight(this.handleSqlType(column.getType(), column.getSize()), 20, ' '));
            if (column.isPrimaryKey()) {
                if (primaryKey != null) {
                    throw new IllegalArgumentException(Stringx.format("生成 {} 的建表语句出错: 不允许声明多个主键", script.getName()));
                }
                primaryKey = column;
                result.append(Stringx.paddingRight("NOT NULL", 15, ' '));
            } else {
                result.append(Stringx.paddingRight("NOT NULL", 15, ' '));
            }

            result.append(" COMMENT '").append(column.getRemarks()).append("',\n");
        }

        if (primaryKey == null) {
            throw new IllegalArgumentException(Stringx.format("生成 {} 的建表语句出错: 没有声明主键", script.getName()));
        }

        result.append("  PRIMARY KEY (").append(this.processColumn(primaryKey.getName())).append(")\n")
                .append(") COMMENT='").append(script.getRemarks()).append("'");

        return Collections.singletonList(new SqlScript(result.toString()));
    }

    @Override
    public @Nonnull List<SqlScript> forRenameTable(@Nonnull RenameTableScript script) throws SQLSyntaxErrorException {
        // RENAME TABLE `MC_REL_DEPT_PARENT` TO `MC_REL_DEPT_FUNCTION`;
        var result = new SqlScript(Stringx.format("RENAME TABLE {} TO {}", this.processTable(script.getName()), this.processTable(script.getNewName())));
        return Collections.singletonList(result);
    }

    @Override
    public @Nonnull List<SqlScript> forAddColumn(@Nonnull AddColumnScript script) throws SQLSyntaxErrorException {
        // ALTER TABLE `MC_ORG_DEPT` ADD COLUMN `LEADER_ID` VARCHAR(36) DEFAULT NULL COMMENT '负责人主键' AFTER `SORT_NO`;
        var sql = new StringBuilder(Stringx.format("ALTER TABLE {} ADD COLUMN {} {} NOT NULL", this.processTable(script.getTable()), this.processColumn(script.getName()), this.handleSqlType(script.getType(), script.getLength())));

        sql.append(Stringx.format(" COMMENT '{}'", script.getComment()));

        if (Stringx.isNotBlank(script.getAfter())) {
            sql.append(" AFTER ").append(this.processColumn(script.getAfter()));
        }

        return Collections.singletonList(new SqlScript(sql.toString()));
    }

    @Override
    public @Nonnull List<SqlScript> forRenameColumn(@Nonnull RenameColumnScript script) throws SQLSyntaxErrorException {
        // ALTER TABLE `MC_ATT_ATTACHMENT` CHANGE COLUMN `CODE` `KEY` VARCHAR(1024) DEFAULT NULL COMMENT '文件在网盘的存储标识'
        var result = new SqlScript(Stringx.format("ALTER TABLE {} CHANGE COLUMN {} {} {} DEFAULT NULL COMMENT '{}'", this.processTable(script.getTable()), this.processColumn(script.getName()), this.processColumn(script.getNewName()), this.handleSqlType(script.getType(), script.getLength()), script.getRemarks()));

        return Collections.singletonList(result);
    }
}
