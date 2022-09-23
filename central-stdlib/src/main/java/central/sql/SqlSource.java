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

import central.sql.datasource.migration.DataSourceMigrator;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 数据源
 *
 * @author Alan Yeh
 * @since 2022/08/03
 */
public interface SqlSource {
    /**
     * 数据源方言
     */
    SqlDialect getDialect();

    /**
     * 数据库命名规则
     */
    SqlConversion getConversion();

    /**
     * 数据源迁移
     */
    DataSourceMigrator getMigrator();

    /**
     * 获取数据源
     */
    DataSource getDataSource();

    /**
     * 获取数据库连接
     * 获取出来的数据库连接不允许关闭，应在使用完后通过 #returnConnection 归还
     */
    Connection getConnection() throws SQLException;

    /**
     * 归还数据库连接
     *
     * @param connection 待归还数据库连接
     */
    void returnConnection(Connection connection) throws SQLException;

}
