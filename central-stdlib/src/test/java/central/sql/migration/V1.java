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
 * Version 1
 * 这个版本是 TestMapper 里面要用到的数据结构，是正常的数据结构
 *
 * @author Alan Yeh
 * @since 2022/09/14
 */
public class V1 extends Migration {
    public V1() {
        super(Version.of("1.0.0"));
    }

    @Override
    public void upgrade(Database database) throws SQLException {
        {
            // 创建用户
            var columns = List.of(
                    Column.of("ID", true, SqlType.STRING, 36, "主键"),
                    Column.of("USERNAME", SqlType.STRING, 36, "用户名"),
                    Column.of("NAME", SqlType.STRING, 50, "姓名"),
                    Column.of("AGE", SqlType.INTEGER, "年龄"),
                    Column.of("DEPT_ID", SqlType.STRING, 36, "部门主键"),
                    Column.of("ENABLED", SqlType.BOOLEAN, "启用/禁用"),
                    Column.of("AVATAR", SqlType.BLOB, "头像"),
                    Column.of("SALARY", SqlType.BIG_DECIMAL, "工资"),
                    Column.of("HIREDATE", SqlType.LONG, "入职日期"),
                    Column.of("CREATOR_ID", SqlType.STRING, 36, "创建人主键"),
                    Column.of("CREATE_DATE", SqlType.DATETIME, "创建时间"),
                    Column.of("MODIFIER_ID", SqlType.STRING, 36, "更新人主键"),
                    Column.of("MODIFY_DATE", SqlType.DATETIME, "更新时间")
            );

            var indies = List.of(
                    Index.of("XT_ACCOUNT_USERNAME", true, "USERNAME")
            );

            var table = Table.of("XT_ACCOUNT", "帐户信息", columns, indies);

            database.addTable(table);
        }

        {
            // 创建部门
            var columns = List.of(
                    Column.of("ID", true, SqlType.STRING, 36, "主键"),
                    Column.of("CODE", SqlType.STRING, 36, "标识"),
                    Column.of("NAME", SqlType.STRING, 50, "名称"),
                    Column.of("CREATOR_ID", SqlType.STRING, 36, "创建人主键"),
                    Column.of("CREATE_DATE", SqlType.DATETIME, "创建时间"),
                    Column.of("MODIFIER_ID", SqlType.STRING, 36, "更新人主键"),
                    Column.of("MODIFY_DATE", SqlType.DATETIME, "更新时间")
            );

            var indies = List.of(
                    Index.of("XT_DEPT_CODE", true, "CODE")
            );

            var table = Table.of("XT_DEPT", "部门信息", columns, indies);
            database.addTable(table);
        }

        {
            // 创建角色
            var columns = List.of(
                    Column.of("ID", true, SqlType.STRING, 36, "主键"),
                    Column.of("CODE", SqlType.STRING, 36, "标识"),
                    Column.of("NAME", SqlType.STRING, 50, "名称"),
                    Column.of("CREATOR_ID", SqlType.STRING, 36, "创建人主键"),
                    Column.of("CREATE_DATE", SqlType.DATETIME, "创建时间"),
                    Column.of("MODIFIER_ID", SqlType.STRING, 36, "更新人主键"),
                    Column.of("MODIFY_DATE", SqlType.DATETIME, "更新时间")
            );

            var indies = List.of(
                    Index.of("XT_ROLE_CODE", false, "CODE")
            );

            var table = Table.of("XT_ROLE", "角色信息表", columns, indies);

            database.addTable(table);
        }

        {
            // 创建角色与帐户的关联
            var columns = List.of(
                    Column.of("ID", true, SqlType.STRING, 36, "主键"),
                    Column.of("ACCOUNT_ID", SqlType.STRING, 36, "帐户主键"),
                    Column.of("ROLE_ID", SqlType.STRING, 36, "角色主键"),
                    Column.of("CREATOR_ID", SqlType.STRING, 36, "创建人主键"),
                    Column.of("CREATE_DATE", SqlType.DATETIME, "创建时间")
            );

            var table = Table.of("XT_REL_ROLE_ACCOUNT", "角色信息表", columns, Collections.emptyList());

            database.addTable(table);
        }
    }

    @Override
    public void downgrade(Database database) throws SQLException {
        var table = database.getTable("XT_ACCOUNT");
        if (table != null) {
            table.drop();
        }

        table = database.getTable("XT_DEPT");
        if (table != null) {
            table.drop();
        }

        table = database.getTable("XT_ROLE");
        if (table != null) {
            table.drop();
        }

        table = database.getTable("XT_REL_ROLE_ACCOUNT");
        if (table != null) {
            table.drop();
        }
    }
}
