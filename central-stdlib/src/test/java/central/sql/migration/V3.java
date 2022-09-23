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

package central.sql.migration;

import central.sql.SqlType;
import central.sql.datasource.migration.Column;
import central.sql.datasource.migration.Index;
import central.sql.datasource.migration.Migration;
import central.sql.datasource.migration.Database;
import central.util.Version;

import java.sql.SQLException;

/**
 * @author Alan Yeh
 * @since 2022/09/16
 */
public class V3 extends Migration {
    public V3() {
        super(Version.of("1.0.1"), Version.of("1.0.2"));
    }

    @Override
    public void upgrade(Database database) throws SQLException {
        {
            // 添加字段
            var table = database.getTable("XT_ORIGIN");
            table.addColumn(Column.of("TEST_COL", SqlType.LONG, "测试字段"));
        }

        {
            // 添加索引
            var table = database.getTable("XT_ORIGIN");
            var index = Index.of("UNI_CODE", false, "CODE");
            table.addIndex(index);
        }

        {
            // 删除索引
            var table = database.getTable("XT_ORIGIN");
            var index = table.getIndex("XT_ORIGIN_CATEGORY");
            index.drop();
        }

        {
            // 重命名字段
            var table = database.getTable("XT_ORIGIN");
            var column = table.getColumn("CATEGORY");
            column.rename("TYPE");
        }
    }

    @Override
    public void downgrade(Database database) throws SQLException {
        {
            // 重命名字段
            var table = database.getTable("XT_ORIGIN");
            var column = table.getColumn("TYPE");
            if (column != null){
                column.rename("CATEGORY");
            }
        }

        {
            // 恢复索引
            var table = database.getTable("XT_ORIGIN");
            var index = table.getIndex("XT_ORIGIN_CATEGORY");
            if (index == null){
                table.addIndex(Index.of("XT_ORIGIN_CATEGORY", false, "CATEGORY"));
            }
        }

        {
            // 删除索引
            var table = database.getTable("XT_ORIGIN");
            var index = table.getIndex("UNI_CODE");
            if (index != null){
                index.drop();
            }
        }

        {
            // 删除字段
            var table = database.getTable("XT_ORIGIN");
            var column = table.getColumn("TEST_COL");
            if (column != null){
                column.drop();
            }
        }
    }
}
