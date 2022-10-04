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

package central.test.orm.database;

import central.sql.SqlDialect;
import central.sql.SqlSource;
import central.sql.datasource.dynamic.DynamicSqlSource;
import central.sql.datasource.dynamic.lookup.LookupKeyHolder;
import central.sql.impl.standard.StandardDataSourceMigrator;
import central.sql.impl.standard.StandardSource;
import central.test.orm.database.migration.V1;
import central.util.Version;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * 应用数据源
 *
 * @author Alan Yeh
 * @since 2022/09/23
 */
@Component
public class ApplicationSource extends DynamicSqlSource implements InitializingBean, EnvironmentAware {

    @Setter(onMethod_ = @Autowired)
    private Environment environment;

    @Setter(onMethod_ = @Autowired)
    private DataSource dataSource;

    @Getter
    private SqlSource master;

    @Override
    public void afterPropertiesSet() throws Exception {
        this.master = this.buildSource(this.dataSource, SqlDialect.resolve(environment.getProperty("spring.datasource.url")));
    }

    private SqlSource buildSource(DataSource dataSource, SqlDialect dialect) {
        return StandardSource.builder()
                .dataSource(dataSource)
                .dialect(dialect)
                .migrator(StandardDataSourceMigrator.builder().name("spring.application.name").target(Version.of("1.0.0")).add(new V1()).build())
                .build();
    }

    @Override
    protected String determineLookupKey() {
        var request = LookupKeyHolder.getContext().get(HttpServletRequest.class);
        if (request == null) {
            return "master";
        }
        return request.getHeader("X-Central-Tenant");
    }

    @Override
    protected SqlSource getDataSourceByName(String name) throws SQLException {
        return this.getMaster();
    }
}
