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

package central.sql.datasource.factory;

import central.sql.SqlDialect;
import central.sql.SqlSource;
import central.sql.impl.standard.StandardSource;

import javax.sql.DataSource;

/**
 * 数据源构建
 *
 * @author Alan Yeh
 * @since 2022/08/05
 */
public interface DataSourceFactory {
    /**
     * 构建数据源
     *
     * @param driver   驱动
     * @param url      数据库连接
     * @param username 用户名
     * @param password 密码
     */
    DataSource build(String driver, String url, String username, String password);

    /**
     * 构建方言数据源
     * 通过 url 识别数据库方言
     *
     * @param driver   驱动
     * @param url      数据库连接
     * @param username 用户名
     * @param password 密码
     * @return 方言数据库
     */
    default SqlSource buildDialect(String driver, String url, String username, String password) {
        return StandardSource.builder()
                .dataSource(this.build(driver, url, username, password))
                .dialect(SqlDialect.resolve(url))
                .build();
    }
}
