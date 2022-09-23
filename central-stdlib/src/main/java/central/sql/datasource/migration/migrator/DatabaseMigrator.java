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

package central.sql.datasource.migration.migrator;

import central.sql.SqlExecutor;
import central.sql.builder.script.index.AddIndexScript;
import central.sql.builder.script.table.AddTableScript;
import central.sql.datasource.migration.MigrateAction;
import central.sql.datasource.migration.Database;
import central.sql.datasource.migration.Table;
import central.sql.datasource.migration.action.AddIndexMigration;
import central.sql.datasource.migration.action.AddTableMigration;
import central.sql.datasource.migration.data.DatabaseData;
import central.sql.meta.database.DatabaseMeta;
import central.util.Listx;
import lombok.Getter;
import lombok.experimental.Delegate;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 迁移程序实现
 *
 * @author Alan Yeh
 * @since 2022/08/30
 */
public class DatabaseMigrator implements Database {

    private final DatabaseData database;

    @Override
    public String getUrl() {
        return this.database.getUrl();
    }

    @Override
    public String getName() {
        return this.database.getName();
    }

    @Override
    public String getVersion() {
        return this.database.getVersion();
    }

    @Override
    public String getDriverName() {
        return this.database.getDriverName();
    }

    @Override
    public String getDriverVersion() {
        return this.database.getDriverVersion();
    }

    @Override
    public List<Table> getTables() {
        return Listx.asStream(this.database.getTables())
                .map(it -> new TableMigrator(this, this.database, it))
                .collect(Collectors.toList());
    }

    /**
     * 迁移动作
     */
    @Getter
    private final List<MigrateAction> actions = new ArrayList<>();


    /**
     * 根据元数据初始化迁移程序
     *
     * @param meta 数据库元数据
     */
    public DatabaseMigrator(DatabaseMeta meta) {
        this.database = DatabaseData.fromMeta(meta);
    }

    @Override
    public void addAction(MigrateAction action) {
        this.actions.add(action);
    }

    @Override
    public void addTable(Table table) {
        // 构建添加表的脚本
        var script = new AddTableScript();
        script.setName(table.getName());
        script.setRemarks(table.getRemarks());
        script.setColumns(table.getColumns().stream().map(it -> {
            var column = new AddTableScript.Column();
            column.setName(it.getName());
            column.setRemarks(it.getRemarks());
            column.setType(it.getType());
            column.setSize(it.getSize());
            column.setPrimaryKey(it.isPrimary());
            return column;
        }).toList());

        this.actions.add(new AddTableMigration(script));

        if (Listx.isNotEmpty(table.getIndices())) {
            for (var index : table.getIndices()) {
                this.actions.add(new AddIndexMigration(new AddIndexScript(index.getName(), script.getName(), index.getColumn(), index.isUnique())));
            }
        }
    }

    @Override
    public Table getTable(String name) {
        return this.database.getTables().stream()
                .filter(it -> it.getName().equals(name))
                .map(it -> new TableMigrator(this, this.database, it))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void drop() {

    }

    public void migrate(SqlExecutor executor) throws SQLException {
        Connection connection = executor.getSource().getConnection();

        try {
            for (var action : this.actions) {
                action.migrate(executor);
            }
        } finally {
            executor.getSource().returnConnection(connection);
        }
    }
}
