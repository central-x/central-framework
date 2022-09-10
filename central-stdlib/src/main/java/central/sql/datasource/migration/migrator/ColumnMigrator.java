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

import central.sql.builder.script.column.DropColumnScript;
import central.sql.builder.script.column.RenameColumnScript;
import central.sql.datasource.migration.Column;
import central.sql.datasource.migration.action.DropColumnMigration;
import central.sql.datasource.migration.action.RenameColumnMigration;
import central.sql.datasource.migration.data.ColumnData;
import central.sql.datasource.migration.data.TableData;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

/**
 * 字段迁移
 *
 * @author Alan Yeh
 * @since 2022/08/31
 */
@RequiredArgsConstructor
public class ColumnMigrator implements Column {
    private final DatabaseMigrator migrator;

    private final TableData table;

    @Delegate
    private final ColumnData column;

    @Override
    public void drop() {
        var action = new DropColumnScript();
        action.setName(this.getName());
        action.setTable(this.table.getName());

        migrator.addAction(new DropColumnMigration(action));
        table.getColumns().remove(this.column);
    }

    @Override
    public void rename(String newName) {
        var action = new RenameColumnScript();
        action.setTable(this.table.getName());
        action.setName(this.getName());
        action.setNewName(newName);
        action.setType(this.getType());
        action.setLength(this.getSize());
        action.setRemarks(this.getRemarks());
        migrator.addAction(new RenameColumnMigration(action));

        this.column.setName(newName);
    }
}
