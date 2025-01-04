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

import central.sql.datasource.factory.hikari.HikariDataSourceFactory;
import central.sql.impl.standard.StandardDataSourceMigrator;
import central.sql.impl.standard.StandardSource;
import central.sql.impl.standard.StandardExecutor;
import central.sql.impl.standard.StandardMetaManager;
import central.sql.interceptor.LogInterceptor;
import central.sql.migration.*;
import central.util.Version;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试数据库版本迁移
 *
 * @author Alan Yeh
 * @since 2022/08/15
 */
public class TestDataSourceMigrator {

    private SqlExecutor executor;

    @BeforeEach
    public void before() {
        // H2
        var driver = "org.h2.Driver";
        var url = "jdbc:h2:mem:centralx";
        var username = "centralx";
        var password = "central.x";

        // mysql
//        var driver = "com.mysql.jdbc.Driver";
//        var url = "jdbc:mysql://10.10.20.20:3306/centralx?useUnicode=true&characterEncoding=utf8&useSSL=false";
//        var username = "root";
//        var password = "root";

        // oracle
//        var driver = "oracle.jdbc.OracleDriver";
//        var url = "jdbc:oracle:thin:@10.10.20.20:1521:xe";
//        var username = "centralx";
//        var password = "123456";

        // PostgreSql
//        var driver = "org.postgresql.Driver";
//        var url = "jdbc:postgresql://10.10.20.20:5432/postgres";
//        var username = "postgres";
//        var password = "root";

        var source = StandardSource.builder()
                .dataSource(new HikariDataSourceFactory().build(driver, url, username, password))
                .dialect(SqlDialect.resolve(url))
                .build();

        this.executor = StandardExecutor.builder()
                .source(source)
                .metaManager(new StandardMetaManager(name -> name.startsWith("XT_")))
                .addInterceptor(new LogInterceptor())
                .build();
    }

    @AfterEach
    public void after() throws SQLException {
        // 测试完了之后降级到初始状态
        var migrator = StandardDataSourceMigrator.builder().name("test").target(Version.of("0")).add(new V1()).add(new V2()).add(new V3()).add(new V4()).add(new V5()).add(new V1_V3()).build();
        migrator.downgrade(executor);
    }

    /**
     * 测试添加表
     */
    @Test
    public void case1() throws SQLException {
        // 升级数据库
        var migrator = StandardDataSourceMigrator.builder().name("test").target(Version.of("1.0.0")).add(new V1()).add(new V2()).add(new V3()).add(new V4()).add(new V5()).add(new V1_V3()).build();
        migrator.upgrade(executor);

        var meta = executor.getMetaManager().getMeta(executor, name -> name.startsWith("XT_"));
        assertNotNull(meta);
        assertFalse(meta.getTables().isEmpty());
        assertNotNull(meta.getUrl());
        assertNotNull(meta.getName());
        assertNotNull(meta.getVersion());
        assertNotNull(meta.getDriverName());
        assertNotNull(meta.getDriverVersion());

        // 用户表
        var account = meta.getTable("XT_ACCOUNT");
        assertNotNull(account);
        assertEquals(13, account.getColumns().size());
        assertEquals(2, account.getIndies().size());

        // 部门表
        var dept = meta.getTable("XT_DEPT");
        assertNotNull(dept);
        assertEquals(7, dept.getColumns().size());
        assertEquals(2, dept.getIndies().size());

        // 角色表
        var role = meta.getTable("XT_ROLE");
        assertNotNull(role);
        assertEquals(7, role.getColumns().size());
        assertEquals(2, role.getIndies().size());

        // 角色帐户关联表
        var rel = meta.getTable("XT_REL_ROLE_ACCOUNT");
        assertNotNull(rel);
        assertEquals(5, rel.getColumns().size());
        assertEquals(1, rel.getIndies().size());
    }

    @Test
    public void case2() throws SQLException {
        // 升级数据库
        var migrator = StandardDataSourceMigrator.builder().name("test").target(Version.of("1.0.1")).add(new V1()).add(new V2()).add(new V3()).add(new V4()).add(new V5()).add(new V1_V3()).build();
        migrator.upgrade(executor);

        var meta = executor.getMetaManager().getMeta(executor, name -> name.startsWith("XT_"));
        assertNotNull(meta);

        // 测试表
        var origin = meta.getTable("XT_ORIGIN");
        assertNotNull(origin);
        assertEquals(8, origin.getColumns().size());
        assertEquals(3, origin.getIndies().size());

        var codeIndex = origin.getIndex("XT_ORIGIN_CODE");
        assertTrue(codeIndex.isUnique());
        assertEquals("CODE", codeIndex.getColumn());

        var categoryIndex = origin.getIndex("XT_ORIGIN_CATEGORY");
        assertFalse(categoryIndex.isUnique());
        assertEquals("CATEGORY", categoryIndex.getColumn());
    }

    @Test
    public void case3() throws SQLException {
        // 升级数据库
        var migrator = StandardDataSourceMigrator.builder().name("test").target(Version.of("1.0.2")).add(new V1()).add(new V2()).add(new V3()).add(new V4()).add(new V5()).build();
        migrator.upgrade(executor);

        var meta = executor.getMetaManager().getMeta(executor, name -> name.startsWith("XT_"));
        assertNotNull(meta);

        // 测试表结构
        var origin = meta.getTable("XT_ORIGIN");
        assertNotNull(origin);
        assertEquals(9, origin.getColumns().size());
        assertEquals(3, origin.getIndies().size());

        // 测试添加字段
        var testColumn = origin.getColumn("TEST_COL");
        assertNotNull(testColumn);
        assertEquals(SqlType.LONG, SqlType.resolve(testColumn.getType()));
        assertEquals("测试字段", testColumn.getRemarks());

        // 测试重命名字段
        var categoryColumn = origin.getColumn("CATEGORY");
        assertNull(categoryColumn);

        var typeColumn = origin.getColumn("TYPE");
        assertNotNull(typeColumn);
        assertEquals(SqlType.STRING, SqlType.resolve(typeColumn.getType()));
        assertEquals("类型", typeColumn.getRemarks());

        // 测试添加索引
        var codeIndex = origin.getIndex("XT_ORIGIN_CODE");
        assertTrue(codeIndex.isUnique());
        assertEquals("CODE", codeIndex.getColumn());

        // 测试删除索引
        var categoryIndex = origin.getIndex("XT_ORIGIN_CATEGORY");
        assertNull(categoryIndex);
    }

    @Test
    public void case4() throws SQLException {
        // 升级数据库
        var migrator = StandardDataSourceMigrator.builder().name("test").target(Version.of("1.0.3")).add(new V1()).add(new V2()).add(new V3()).add(new V4()).add(new V5()).build();
        migrator.upgrade(executor);

        var meta = executor.getMetaManager().getMeta(executor, name -> name.startsWith("XT_"));
        assertNotNull(meta);

        // 测试重命名
        // 测试表结构
        var origin = meta.getTable("XT_NEW");
        assertNotNull(origin);
        assertEquals(8, origin.getColumns().size());
        assertEquals(3, origin.getIndies().size());

        // 测试删除字段
        var testColumn = origin.getColumn("TEST_COL");
        assertNull(testColumn);
    }

    @Test
    public void case5() throws SQLException {
        // 升级数据库
        var migrator = StandardDataSourceMigrator.builder().name("test").target(Version.of("1.0.4")).add(new V1()).add(new V2()).add(new V3()).add(new V4()).add(new V5()).build();
        migrator.upgrade(executor);

        var meta = executor.getMetaManager().getMeta(executor, name -> name.startsWith("XT_"));
        assertNotNull(meta);

        // 测试删除表
        var origin = meta.getTable("XT_NEW");
        assertNull(origin);
    }

    @Test
    public void case6() throws SQLException {
        // 升级数据库
        var migrator = StandardDataSourceMigrator.builder().name("test").target(Version.of("1.0.3")).add(new V1()).add(new V2()).add(new V3()).add(new V4()).add(new V5()).add(new V1_V3()).build();
        migrator.upgrade(executor);

        var meta = executor.getMetaManager().getMeta(executor, name -> name.startsWith("XT_"));
        assertNotNull(meta);

        // 测试重命名
        // 测试表结构
        var origin = meta.getTable("XT_NEW");
        assertNotNull(origin);
        assertEquals(8, origin.getColumns().size());
        assertEquals(3, origin.getIndies().size());

        // 测试删除字段
        var testColumn = origin.getColumn("TEST_COL");
        assertNull(testColumn);
    }
}
