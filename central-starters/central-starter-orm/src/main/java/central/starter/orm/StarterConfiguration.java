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

package central.starter.orm;

import central.sql.SqlSource;
import central.sql.SqlExecutor;
import central.sql.datasource.factory.DataSourceFactory;
import central.sql.datasource.factory.druid.DruidDataSourceFactory;
import central.sql.datasource.factory.hikari.HikariDataSourceFactory;
import central.sql.impl.standard.StandardExecutor;
import central.sql.interceptor.LogInterceptor;
import central.starter.orm.properties.MigrationProperties;
import central.starter.orm.properties.OrmProperties;
import central.util.Objectx;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;


/**
 * 配置
 *
 * @author Alan Yeh
 * @since 2022/09/22
 */
@EnableConfigurationProperties({OrmProperties.class, MigrationProperties.class, DataSourceProperties.class})
public class StarterConfiguration {

    /**
     * DataSource Factory
     */
    @Bean
    @ConditionalOnMissingBean(DataSourceFactory.class)
    public DataSourceFactory getDataSourceFactory(Environment environment) {
        return switch (Objectx.get(environment.getProperty("spring.datasource.type"), "com.zaxxer.hikari.HikariDataSource")) {
            case "com.alibaba.druid.pool" -> new DruidDataSourceFactory();
            default -> new HikariDataSourceFactory();
        };
    }

    /**
     * 创建数据源
     */
    @Bean
    @ConditionalOnMissingBean(DataSource.class)
    public DataSource getDataSource(DataSourceFactory factory, Environment environment) {
        var properties = Binder.get(environment).bindOrCreate("spring.datasource", DataSourceProperties.class);
        return factory.build(properties.getDriverClassName(), properties.getUrl(), properties.getUsername(), properties.getPassword());
    }

    /**
     * 创建 Sql 执行器
     */
    @Bean(initMethod = "init")
    public StandardExecutor getExecutor(SqlSource source) {
        return StandardExecutor.builder()
                .source(source)
                .addInterceptor(new LogInterceptor())
                .build();
    }
}
