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

package central.sql;

import central.sql.conversion.UnderlineConversion;
import central.sql.datasource.factory.hikari.HikariDataSourceFactory;
import central.sql.datasource.migration.*;
import central.sql.impl.standard.StandardSqlExecutor;
import central.sql.migration.v1;
import central.sql.migration.v2;
import central.util.Version;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

/**
 * 测试数据库版本迁移
 *
 * @author Alan Yeh
 * @since 2022/08/15
 */
public class TestDataSourceMigrator {

    /**
     * 测试添加表
     */
    @Test
    public void case1() throws SQLException {
//        var dataSource = new HikariDataSourceFactory()
//                .build("com.mysql.cj.jdbc.Driver", "jdbc:mysql://127.0.0.1:3306/centralx?useUnicode=true&characterEncoding=utf8&useSSL=false", "root", "root");

        var dataSource = new HikariDataSourceFactory().build("org.h2.Driver", "jdbc:h2:mem:centralx", "centralx", "central.x");

        var executor = StandardSqlExecutor.builder()
                .dataSource(dataSource)
                .dialect(SqlDialect.H2)
                .conversion(new UnderlineConversion())
                .migrator(new DataSourceMigrator("test", new Version("1.1.0")).add(new v1()))
                .build();

        executor.initialize();
    }
}
