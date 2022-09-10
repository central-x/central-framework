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

package central.sql.datasource.factory.hikari;

import central.sql.SqlDialect;
import central.sql.datasource.factory.DataSourceFactory;
import central.util.Objectx;
import central.util.Stringx;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.util.Objects;

/**
 * Hikari
 *
 * @author Alan Yeh
 * @since 2022/08/05
 */
public class HikariDataSourceFactory extends HikariProperties implements DataSourceFactory {

//    public HikariDataSourceFactory() {
//        this.properties = new HikariProperties();
//    }
//
//    public HikariDataSourceFactory(HikariProperties properties) {
//        this.properties = properties;
//    }

    @Override
    public DataSource build(String driver, String url, String username, String password) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName(driver);
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        dataSource.setAutoCommit(this.getAutoCommit());
        dataSource.setConnectionTimeout(this.getConnectionTimeout());
        dataSource.setIdleTimeout(this.getIdleTimeout());
        dataSource.setMaxLifetime(this.getMaxLifetime());
        switch (SqlDialect.fromUrl(url)) {
            case MySql -> {
                dataSource.setConnectionTestQuery(Objectx.get(this.getConnectionTestQuery(), "SELECT 1"));
            }
            case Oracle, Kingbase, Oscar, H2, Vastbase, PostgreSql, Dameng -> {
                dataSource.setConnectionTestQuery(Objectx.get(this.getConnectionTestQuery(), "SELECT 1 FROM DUAL"));
            }
            case HighGo -> {
                dataSource.setConnectionTestQuery(Objectx.get(this.getConnectionTestQuery(), "select version()"));
            }
            default -> {
                throw new IllegalArgumentException("不支持的数据库类型");
            }
        }
        if (!Objects.isNull(this.getMinimumIdle())) {
            dataSource.setMinimumIdle(this.getMinimumIdle());
        }
        dataSource.setMaximumPoolSize(this.getMaximumPoolSize());
        if (Stringx.isNotBlank(this.getPoolName())) {
            dataSource.setPoolName(this.getPoolName());
        }

        dataSource.setIsolateInternalQueries(this.getIsolateInternalQueries());
        dataSource.setInitializationFailTimeout(this.getInitializationFailTimeout());

        if (Stringx.isNotBlank(this.getConnectionInitSql())) {
            dataSource.setConnectionInitSql(this.getConnectionInitSql());
        }
        dataSource.setValidationTimeout(this.getValidationTimeout());
        return dataSource;
    }
}
