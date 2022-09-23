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

package central.sql.impl.standard;

import central.sql.SqlSource;
import central.sql.SqlConversion;
import central.sql.SqlDialect;
import central.sql.conversion.UnderlineConversion;
import central.sql.datasource.migration.DataSourceMigrator;
import central.validation.Label;
import central.validation.Validatex;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 标准方言数据源
 *
 * @author Alan Yeh
 * @since 2022/09/23
 */
public class StandardSource implements SqlSource {
    @Getter
    private final DataSource dataSource;

    /**
     * 数据源方言
     */
    @Getter
    private final SqlDialect dialect;

    /**
     * 数据库命名规则
     */
    @Getter
    private final SqlConversion conversion;

    /**
     * 数据库迁移
     */
    @Getter
    @Setter
    private DataSourceMigrator migrator;

    private StandardSource(DataSource dataSource, SqlDialect dialect, SqlConversion conversion, DataSourceMigrator migrator) {
        this.dataSource = dataSource;
        this.dialect = dialect;
        this.conversion = conversion;
        this.migrator = migrator;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return this.getDataSource().getConnection();
    }

    @Override
    public void returnConnection(Connection connection) throws SQLException {
        connection.close();
    }

    public static Builder builder() {
        return new Builder();
    }

    @Setter
    @Accessors(chain = true, fluent = true)
    public static class Builder {

        @NotNull
        @Label("数据源")
        private DataSource dataSource;

        @NotNull
        @Label("数据源方言")
        private SqlDialect dialect;

        @NotNull
        @Label("数据库命名规则")
        private SqlConversion conversion = new UnderlineConversion();

        @Label("数据源迁移工具")
        private DataSourceMigrator migrator;

        public SqlSource build() {
            Validatex.Default().validate(this);
            return new StandardSource(this.dataSource, this.dialect, this.conversion, this.migrator);
        }
    }
}
