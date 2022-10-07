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

package central.sql.datasource.factory.druid;

import central.sql.SqlDialect;
import central.sql.datasource.factory.DataSourceFactory;
import central.util.Objectx;
import com.alibaba.druid.pool.DruidDataSource;

import javax.sql.DataSource;

/**
 * Druid
 *
 * @author Alan Yeh
 * @since 2022/08/05
 */
public class DruidDataSourceFactory implements DataSourceFactory {
    private DruidProperties properties;

    public DruidDataSourceFactory() {
        this.properties = new DruidProperties();
    }

    public DruidDataSourceFactory(DruidProperties properties) {
        this.properties = properties;
    }

    @Override
    public DataSource build(String driver, String url, String username, String password) {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(driver);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        // 附加属性
        dataSource.setInitialSize(properties.getInitialSize());
        dataSource.setMaxActive(properties.getMaxActive());
        dataSource.setMinIdle(properties.getMinIdle());
        dataSource.setMaxWait(properties.getMaxWait());
        dataSource.setTimeBetweenEvictionRunsMillis(properties.getTimeBetweenEvictionRunsMillis());
        dataSource.setMinEvictableIdleTimeMillis(properties.getMinEvictableIdleTimeMillis());
        dataSource.setTestOnBorrow(properties.getTestOnBorrow());
        dataSource.setTestOnReturn(properties.getTestOnReturn());
        dataSource.setTestWhileIdle(properties.getTestWhileIdle());
        dataSource.setPoolPreparedStatements(properties.getPoolPreparedStatements());
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(properties.getMaxPoolPreparedStatementPerConnectionSize());
        dataSource.setMaxOpenPreparedStatements(properties.getMaxOpenPreparedStatements());
        switch (SqlDialect.resolve(url)) {
            case MySql, PostgreSql -> {
                dataSource.setValidationQuery(Objectx.getOrDefault(properties.getValidationQuery(), "SELECT 1"));
            }
            case Oracle, Kingbase, Oscar, H2, Vastbase, Dameng -> {
                dataSource.setValidationQuery(Objectx.getOrDefault(properties.getValidationQuery(), "SELECT 1 FROM DUAL"));
            }
            case HighGo -> {
                dataSource.setValidationQuery(Objectx.getOrDefault(properties.getValidationQuery(), "select version()"));
            }
            default -> {
                throw new IllegalArgumentException("不支持的数据库类型");
            }
        }
        return dataSource;
    }
}
