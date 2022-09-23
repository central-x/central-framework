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
import central.sql.datasource.migration.*;
import central.util.Version;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * 用于测试新增表
 *
 * @author Alan Yeh
 * @since 2022/09/14
 */
public class V2 extends Migration {

    public V2() {
        super(Version.of("1.0.0"), Version.of("1.0.1"));
    }

    @Override
    public void upgrade(Database database) {
        {
            // 添加测试用表
            var columns = List.of(
                    Column.of("ID", true, SqlType.STRING, 36, "主键"),
                    Column.of("CODE", SqlType.STRING, 36, "标识"),
                    Column.of("NAME", SqlType.STRING, 50, "名称"),
                    Column.of("CATEGORY", SqlType.STRING, 30, "类型"),
                    Column.of("CREATOR_ID", SqlType.STRING, 36, "创建人主键"),
                    Column.of("CREATE_DATE", SqlType.DATETIME, "创建时间"),
                    Column.of("MODIFIER_ID", SqlType.STRING, 36, "更新人主键"),
                    Column.of("MODIFY_DATE", SqlType.DATETIME, "更新时间")
            );

            var indies = List.of(
                    Index.of("XT_ORIGIN_CODE", true, "CODE"),
                    Index.of("XT_ORIGIN_CATEGORY", false, "CATEGORY")
            );

            var table = Table.of("XT_ORIGIN", "测试用表", columns, indies);

            database.addTable(table);
        }
    }

    @Override
    public void downgrade(Database database) throws SQLException {
        var table = database.getTable("XT_ORIGIN");
        if (table != null) {
            table.drop();
        }
    }
}