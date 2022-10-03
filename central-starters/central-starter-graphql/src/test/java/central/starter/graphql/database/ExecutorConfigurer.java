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

package central.starter.graphql.database;

import central.sql.SqlDialect;
import central.sql.SqlInterceptor;
import central.sql.SqlSource;
import central.sql.impl.standard.StandardDataSourceMigrator;
import central.sql.impl.standard.StandardSource;
import central.sql.interceptor.LogInterceptor;
import central.starter.orm.SqlExecutorConfigurer;
import central.util.Version;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;

/**
 * 应用 Sql 执行器配置
 *
 * @author Alan Yeh
 * @since 2022/09/28
 */
@Component
public class ExecutorConfigurer implements SqlExecutorConfigurer {

    @Setter(onMethod_ = @Autowired)
    private DataSource dataSource;

    @Override
    public SqlSource getSource() {
        return StandardSource.builder().dataSource(dataSource).dialect(SqlDialect.H2)
                .migrator(StandardDataSourceMigrator.builder().name("central-starter-graphql").target(Version.of("1.0.0")).add(new InitMigration()).build())
                .build();
    }

    @Override
    public List<SqlInterceptor> getInterceptors() {
        return List.of(new LogInterceptor());
    }
}
