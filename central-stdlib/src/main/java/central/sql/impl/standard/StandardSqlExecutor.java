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

import central.bean.OptionalEnum;
import central.lang.Assertx;
import central.lang.reflect.TypeReference;
import central.security.Cipherx;
import central.sql.*;
import central.sql.conversion.UnderlineConversion;
import central.sql.data.Entity;
import central.sql.datasource.DialectDataSource;
import central.sql.datasource.migration.DataSourceMigrator;
import central.sql.meta.EntityMetaBuilder;
import central.sql.meta.entity.EntityMeta;
import central.util.Converterx;
import central.util.Mapx;
import central.util.Stringx;
import central.validation.Label;
import central.validation.Validatorx;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.groups.Default;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.ExtensionMethod;

import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Date;

/**
 * 标准 Sql 执行器
 *
 * @author Alan Yeh
 * @since 2022/08/03
 */
@ExtensionMethod({Mapx.class, Stringx.class})
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class StandardSqlExecutor implements SqlExecutor {

    @Getter
    @Label("数据源")
    private final DataSource dataSource;

    @Getter
    @Label("类型转换器")
    private final Converterx converter;

    @Getter
    @Label("数据库方言")
    private final SqlDialect dialect;

    @Getter
    @Label("加密器")
    private final SqlCipher cipher;

    @Getter
    @Label("命名规则")
    private final SqlConversion conversion;

    @Getter
    @Label("数据库迁移")
    private final DataSourceMigrator migrator;

    /**
     * 初始化
     */
    public void initialize() throws SQLException {
        if (this.migrator != null) {
            this.migrator.migrate(this);
        }
    }

    @Override
    public EntityMeta getMeta(Class<? extends Entity> type) throws SQLException {
        return EntityMetaBuilder.build(type);
    }

    @Override
    public Connection getConnection() throws SQLException{
        return this.dataSource.getConnection();
    }

    @Override
    public void returnConnection(Connection connection) throws SQLException {
        connection.close();
    }

    @Override
    public SqlBuilder getBuilder() {
        return this.dialect.getBuilder();
    }

    @Override
    public <T> T selectSingle(SqlScript script, Class<T> type) throws SQLException {
        Connection connection = this.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(script.getSql())) {
            this.bind(statement, script.getArgs());

            var cursor = statement.executeQuery();

            if (cursor.next()) {
                if (Map.class.isAssignableFrom(type)) {
                    // 如果需要返回 Map，则将结果解析为 Map<String, Object>
                    return (T) this.toMap(cursor, type);
                } else {
                    return this.toBean(cursor, type);
                }
            } else {
                return null;
            }
        } finally {
            this.returnConnection(connection);
        }
    }

    @Override
    public <T> List<T> select(SqlScript script, Class<T> type) throws SQLException {
        Connection connection = this.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(script.getSql())) {
            this.bind(statement, script.getArgs());
            var cursor = statement.executeQuery();

            var data = new ArrayList<T>();
            while (cursor.next()) {
                if (Map.class.isAssignableFrom(type)) {
                    // 如果需要返回 Map，则将结果解析为 Map<String, Object>
                    data.add((T) this.toMap(cursor, type));
                } else {
                    data.add(this.toBean(cursor, type));
                }
            }
            return data;
        } finally {
            this.returnConnection(connection);
        }
    }

    @SneakyThrows({NoSuchMethodException.class, InstantiationException.class, IllegalAccessException.class, InvocationTargetException.class})
    private Map<String, Object> toMap(ResultSet cursor, Class<?> type) throws SQLException {
        Map<String, Object> record;
        if (type == Map.class) {
            // 如果对 Map 没有要求，则使用 CaseInsensitiveHashMap
            record = new HashMap<String, Object>().caseInsensitive();
        } else {
            record = (Map<String, Object>) type.getConstructor().newInstance();
        }

        var metadata = cursor.getMetaData();
        for (int i = 1, cols = metadata.getColumnCount(); i <= cols; i++) {
            // 字段名
            var columnName = metadata.getColumnLabel(i);
            if (columnName.isNullOrBlank()) {
                columnName = metadata.getColumnName(i);
            }
            var propertyName = this.conversion.getPropertyName(columnName);

            // 值
            var columnType = metadata.getColumnType(i);
            var sqlType = SqlType.resolve(columnType);
            Assertx.mustNotNull(sqlType, () -> new SQLException(Stringx.format("不支持的数据库类型[{}]", columnType)));
            var value = sqlType.getResolver().resolve(this.dialect, cursor, metadata, i);
            record.put(propertyName, value);
        }
        return record;
    }

    @SneakyThrows({NoSuchMethodException.class, InstantiationException.class, IllegalAccessException.class, InvocationTargetException.class})
    private <T> T toBean(ResultSet cursor, Class<T> type) throws SQLException {
        var bean = type.getConstructor().newInstance();
        var reference = TypeReference.of(type);

        var metadata = cursor.getMetaData();
        for (int i = 1, cols = metadata.getColumnCount(); i <= cols; i++) {
            // 字段名
            var columnName = metadata.getColumnLabel(i);
            if (columnName.isNullOrBlank()) {
                columnName = metadata.getColumnName(i);
            }
            var propertyName = this.conversion.getPropertyName(columnName);
            var property = reference.getProperty(propertyName);
            if (property == null) {
                continue;
            }

            // 值
            var columnType = metadata.getColumnType(i);
            var sqlType = SqlType.resolve(columnType);
            Assertx.mustNotNull(sqlType, () -> new SQLException(Stringx.format("不支持的数据库类型[{}]", columnType)));
            var value = sqlType.getResolver().resolve(this.dialect, cursor, metadata, i);

            if (value == null) {
                continue;
            }
            if (this.getConverter().support(value.getClass(), property.getPropertyType())) {
                value = this.getConverter().convert(value, property.getPropertyType());
            }
            property.getWriteMethod().invoke(bean, value);
        }
        return bean;
    }

    @Override
    public long execute(SqlScript script) throws SQLException {
        Connection connection = this.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(script.getSql())) {
            this.bind(statement, script.getArgs());

            return statement.executeUpdate();
        } finally {
            this.returnConnection(connection);
        }
    }

    @Override
    public long[] executeBatch(SqlBatchScript batchScript) throws SQLException {
        if (batchScript.getArgs().isEmpty()) {
            // 如果参数为空，说明不需要执行批量脚本
            return new long[0];
        }

        Connection connection = this.getConnection();
        try {
            try (PreparedStatement statement = connection.prepareStatement(batchScript.getSql())) {
                for (int i = 0; i < batchScript.getArgs().size(); i++) {
                    List<Object> args = batchScript.getArgs().get(i);

                    this.bind(statement, args);
                    statement.addBatch();
                }

                return Arrays.stream(statement.executeBatch()).mapToLong(it -> (long) it).toArray();
            }
        } finally {
            this.returnConnection(connection);
        }

    }

    private void bind(PreparedStatement statement, List<Object> args) throws SQLException {
        int i = 0;
        try {
            for (; i < args.size(); i++) {
                Object arg = args.get(i);
                if (arg == null) {
                    statement.setObject(i + 1, null);
                } else if (arg instanceof Date date) {
                    statement.setTimestamp(i + 1, new Timestamp(date.getTime()));
                } else if (arg instanceof LocalDateTime time) {
                    statement.setTimestamp(i + 1, Timestamp.valueOf(time));
                } else if (arg instanceof OptionalEnum<?> optional) {
                    statement.setObject(i + 1, optional.getValue());
                } else {
                    statement.setObject(i + 1, arg);
                }
            }
        } catch (SQLException ex) {
            throw new SQLException(Stringx.format("绑定第 {} 个参数时出现异常: " + ex.getLocalizedMessage(), i), ex);
        }
    }

    @Setter
    @Accessors(chain = true, fluent = true)
    public static class Builder {
        @NotNull
        @Label("数据源")
        private DataSource dataSource;

        @NotNull
        @Label("数据库方言")
        private SqlDialect dialect;

        public Builder dataSource(DialectDataSource dataSource) {
            this.dataSource = dataSource;
            this.dialect = dataSource.getDialect();
            return this;
        }

        public Builder dataSource(DataSource dataSource) {
            this.dataSource = dataSource;
            return this;
        }

        @NotNull
        @Label("加密器")
        private SqlCipher cipher = new SqlCipherAdapter(Cipherx.NONE, null, null);

        @NotNull
        @Label("命名规则")
        private SqlConversion conversion = new UnderlineConversion();

        @NotNull
        @Label("类型转换器")
        private Converterx converter = new Converterx();

        @Label("数迁库迁移")
        private DataSourceMigrator migrator;

        public StandardSqlExecutor build() {
            Validatorx.Default().validateBean(this, Default.class);
            return new StandardSqlExecutor(this.dataSource, this.converter, this.dialect, this.cipher, this.conversion, this.migrator);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
