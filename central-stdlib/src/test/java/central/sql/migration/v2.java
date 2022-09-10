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
import central.sql.datasource.migration.Migrator;
import central.util.Version;

/**
 * @author Alan Yeh
 * @since 2022/09/09
 */
public class v2 extends Migration {

    public v2() {
        super(Version.of("1.0.0"), Version.of("1.0.1"));
    }

    @Override
    public void migrate(Migrator migrator) {
        // 添加字段
        migrator.getTable("X_TEST_ACCOUNT")
                .addColumn(Column.of("PASSWORD", SqlType.STRING, 128, "密码"));

        // 重命名字段
        migrator.getTable("X_TEST_ACCOUNT")
                .getColumn("NAME")
                .rename("NEW_NAME");

        // 删除字段
        migrator.getTable("X_TEST_ACCOUNT")
                .getColumn("NAME")
                .drop();

        // 添加索引
        migrator.getTable("X_TEST_ACCOUNT")
                .addIndex(Index.of("C_IDX_TA_US", true, "USERNAME"));

        // 删除索引
        migrator.getTable("X_TEST_ACCOUNT")
                .getIndex("X_IDX_TA_US")
                .drop();

        // 重命名表
        migrator.getTable("X_TEST_ACCOUNT")
                .rename("X_TEST_ACCOUNT_NEW");

        // 删除表
        migrator.getTable("X_TEST_ACCOUNT_NEW")
                .drop();
    }
}
