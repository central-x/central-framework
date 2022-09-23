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
import central.sql.SqlDialect;
import central.sql.datasource.dynamic.lookup.LookupKeyFilter;
import central.sql.impl.standard.StandardDataSourceMigrator;
import central.sql.impl.standard.StandardSource;
import central.starter.orm.data.migration.V1;
import central.util.Version;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

/**
 * 测试应用配置
 *
 * @author Alan Yeh
 * @since 2022/09/23
 */
@Configuration
public class ApplicationConfiguration {


    /**
     * 通过请求头决定数据源
     */
    @Bean
    public FilterRegistrationBean<LookupKeyFilter> lookupKeyFilter() {
        var bean = new FilterRegistrationBean<LookupKeyFilter>();
        bean.setFilter(new LookupKeyFilter());
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }


    /**
     * 初始化主数据源
     */
    @Bean
    public SqlSource getApplicationSource(DataSource dataSource, Environment environment) {
        var dialectDataSource = StandardSource.builder()
                .dataSource(dataSource)
                .dialect(SqlDialect.resolve(environment.getProperty("spring.datasource.url")))
                .migrator(StandardDataSourceMigrator.builder().name(environment.getProperty("spring.application.name")).target(Version.of("1.0.0")).add(new V1()).build())
                .build();

        return new ApplicationSource(dialectDataSource);
    }
}
