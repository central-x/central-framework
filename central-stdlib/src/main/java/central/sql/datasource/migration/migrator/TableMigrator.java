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

import central.lang.Assertx;
import central.sql.builder.script.column.AddColumnScript;
import central.sql.builder.script.index.AddIndexScript;
import central.sql.builder.script.table.DropTableScript;
import central.sql.builder.script.table.RenameTableScript;
import central.sql.datasource.migration.Column;
import central.sql.datasource.migration.Index;
import central.sql.datasource.migration.Table;
import central.sql.datasource.migration.action.AddColumnMigration;
import central.sql.datasource.migration.action.AddIndexMigration;
import central.sql.datasource.migration.action.DropTableMigration;
import central.sql.datasource.migration.action.RenameTableMigration;
import central.sql.datasource.migration.data.ColumnData;
import central.sql.datasource.migration.data.DatabaseData;
import central.sql.datasource.migration.data.IndexData;
import central.sql.datasource.migration.data.TableData;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 表迁移工具
 *
 * @author Alan Yeh
 * @since 2022/08/31
 */
@RequiredArgsConstructor
public class TableMigrator implements Table {
    private final DatabaseMigrator migrator;

    private final DatabaseData database;

    private final TableData table;

    @Override
    public String getName() {
        return this.table.getName();
    }

    @Override
    public String getRemarks() {
        return this.table.getRemarks();
    }

    @Override
    public List<Column> getColumns() {
        return this.table.getColumns().values().stream()
                .map(it -> new ColumnMigrator(this.migrator, table, it))
                .collect(Collectors.toList());
    }

    @Override
    public Column getColumn(String name) {
        var column = this.table.getColumns().get(name);
        if (column == null) {
            return null;
        } else {
            return new ColumnMigrator(this.migrator, table, column);
        }
    }

    @Override
    public Table addColumn(Column column) {
        Assertx.mustTrue(!column.isPrimary(), "不允许向表[{}]添加新主键", this.table.getName());
        Assertx.mustTrue(!this.table.getColumns().containsKey(column.getName()), "表[{}]已存在同名字段[{}]", this.table.getName(), column.getName());

        var script = new AddColumnScript();
        script.setTable(this.table.getName());
        script.setName(column.getName());
        script.setType(column.getType());
        script.setLength(column.getSize());
        script.setComment(column.getRemarks());
        this.migrator.addAction(new AddColumnMigration(script));

        this.table.getColumns().put(column.getName(), new ColumnData(column.getName(), column.isPrimary(), column.getType(), column.getSize(), column.getRemarks()));
        return this;
    }

    @Override
    public List<Index> getIndices() {
        return this.table.getIndices().values().stream()
                .map(it -> new IndexMigrator(this.migrator, this.table, it))
                .collect(Collectors.toList());
    }

    @Override
    public Index getIndex(String name) {
        var index = this.table.getIndices().get(name);
        if (index == null) {
            return null;
        } else {
            return new IndexMigrator(this.migrator, this.table, index);
        }
    }

    @Override
    public Table addIndex(Index index) {
        Assertx.mustTrue(!this.table.getIndices().containsKey(index.getName()), "表[{}]已存在同名索引[{}]", this.table.getName(), index.getName());

        var script = new AddIndexScript();
        script.setName(index.getName());
        script.setTable(this.table.getName());
        script.setColumn(index.getColumn());
        this.migrator.addAction(new AddIndexMigration(script));

        this.table.getIndices().put(index.getName(), new IndexData(index.getName(), index.isUnique(), index.getColumn()));

        return this;
    }

    @Override
    public void drop() {
        var script = new DropTableScript();
        script.setName(this.table.getName());

        this.migrator.addAction(new DropTableMigration(script));
        this.database.getTables().remove(this.table.getName());
    }

    @Override
    public void rename(String newName) {
        var script = new RenameTableScript();
        script.setName(this.table.getName());
        script.setNewName(newName);
        this.migrator.addAction(new RenameTableMigration(script));
        this.database.getTables().remove(this.table.getName());
        this.table.setName(newName);
        this.database.getTables().put(this.table.getName(), this.table);
    }
}
