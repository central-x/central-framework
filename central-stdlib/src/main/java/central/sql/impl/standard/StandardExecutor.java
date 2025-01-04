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

import central.bean.Nonnull;
import central.bean.OptionalEnum;
import central.lang.Assertx;
import central.security.Cipherx;
import central.sql.*;
import central.sql.SqlSource;
import central.util.Mapx;
import central.lang.Stringx;
import central.validation.Label;
import central.validation.Validatex;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.ExtensionMethod;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Date;
import java.util.List;

/**
 * 标准 Sql 执行器
 *
 * @author Alan Yeh
 * @since 2022/08/03
 */
@ExtensionMethod({Mapx.class, Stringx.class})
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class StandardExecutor implements SqlExecutor {

    @Getter
    @Label("配置信息")
    private final Properties properties;

    @Getter
    @Label("数据源")
    private final SqlSource source;

    @Getter
    @Label("类型转换器")
    private final SqlConverter converter;

    @Getter
    @Label("元数据管理")
    private final SqlMetaManager metaManager;

    @Getter
    @Label("加密器")
    private final SqlCipher cipher;

    @Getter
    @Label("实体转换器")
    private final SqlTransformer transformer;

    @Label("拦载器")
    private final List<SqlInterceptor> interceptors = new ArrayList<>();

    @Override
    public void addInterceptor(SqlInterceptor interceptor) {
        this.interceptors.add(interceptor);
    }

    /**
     * 初始化
     */
    public void init() throws Exception {
        if (this.getSource().getMigrator() != null) {
            this.getSource().getMigrator().upgrade(this);
        }
    }

    @Override
    public <T> T selectSingle(SqlScript script, Class<T> type) throws SQLException {
        var context = new StandardSqlContext(this, script.getSql(), script.getArgs());
        this.interceptors.forEach(it -> it.before(context));

        Connection connection = this.getSource().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(script.getSql())) {
            this.bind(statement, script.getArgs());

            var cursor = statement.executeQuery();

            T result = null;
            if (cursor.next()) {
                var data = this.wrap(cursor);
                result = this.getTransformer().transform(this, data, type);
            }

            context.setResult(result);
            this.interceptors.forEach(it -> it.after(context));
            return result;
        } catch (Throwable throwable) {
            this.interceptors.forEach(it -> it.error(context, throwable));
            throw throwable;
        } finally {
            this.getSource().returnConnection(connection);
        }
    }

    @Override
    public <T> List<T> select(SqlScript script, Class<T> type) throws SQLException {
        var context = new StandardSqlContext(this, script.getSql(), script.getArgs());
        this.interceptors.forEach(it -> it.before(context));

        Connection connection = this.getSource().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(script.getSql())) {
            this.bind(statement, script.getArgs());
            var cursor = statement.executeQuery();

            var result = new ArrayList<T>();
            while (cursor.next()) {
                var data = this.wrap(cursor);
                result.add(this.getTransformer().transform(this, data, type));
            }

            context.setResult(result);
            this.interceptors.forEach(it -> it.after(context));

            return result;
        } catch (Throwable throwable) {
            this.interceptors.forEach(it -> it.error(context, throwable));
            throw throwable;
        } finally {
            this.getSource().returnConnection(connection);
        }
    }

    private Map<String, Object> wrap(ResultSet cursor) throws SQLException {
        var metadata = cursor.getMetaData();
        var data = new LinkedHashMap<String, Object>(metadata.getColumnCount());
        for (int i = 1, cols = metadata.getColumnCount(); i <= cols; i++) {
            // 字段名
            var columnName = metadata.getColumnLabel(i);
            if (columnName.isNullOrBlank()) {
                columnName = metadata.getColumnName(i);
            }
            // 将字段名转成属性名
            var propertyName = this.getSource().getConversion().getPropertyName(columnName);

            // 值
            var columnType = metadata.getColumnType(i);
            var sqlType = Assertx.requireNotNull(SqlType.resolve(columnType), SQLException::new, Stringx.format("不支持的数据库字段类型[{}]", columnType));
            data.put(propertyName, sqlType.getResolver().resolve(this.getSource().getDialect(), cursor, metadata, i));
        }
        return data;
    }

    @Override
    public long execute(SqlScript script) throws SQLException {
        var context = new StandardSqlContext(this, script.getSql(), script.getArgs());
        this.interceptors.forEach(it -> it.before(context));

        Connection connection = this.getSource().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(script.getSql())) {
            this.bind(statement, script.getArgs());

            var effected = statement.executeUpdate();

            context.setResult(effected);
            this.interceptors.forEach(it -> it.after(context));

            return effected;
        } catch (Throwable throwable) {
            this.interceptors.forEach(it -> it.error(context, throwable));
            throw throwable;
        } finally {
            this.getSource().returnConnection(connection);
        }
    }

    @Override
    public long[] executeBatch(SqlBatchScript batchScript) throws SQLException {
        if (batchScript.getArgs().isEmpty()) {
            // 如果参数为空，说明不需要执行批量脚本
            return new long[0];
        }

        var context = new StandardSqlContext(this, batchScript.getSql(), new ArrayList<>(batchScript.getArgs()));
        this.interceptors.forEach(it -> it.before(context));

        Connection connection = this.getSource().getConnection();
        try {
            try (PreparedStatement statement = connection.prepareStatement(batchScript.getSql())) {
                for (int i = 0; i < batchScript.getArgs().size(); i++) {
                    List<Object> args = batchScript.getArgs().get(i);

                    this.bind(statement, args);
                    statement.addBatch();
                }

                var result = Arrays.stream(statement.executeBatch()).mapToLong(it -> (long) it).toArray();

                context.setResult(result);
                this.interceptors.forEach(it -> it.after(context));

                return result;
            }
        } catch (Throwable throwable) {
            this.interceptors.forEach(it -> it.error(context, throwable));
            throw throwable;
        } finally {
            this.getSource().returnConnection(connection);
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

    public static Builder builder() {
        return new Builder();
    }

    @Setter
    @Accessors(chain = true, fluent = true)
    public static class Builder {
        @Nonnull
        @Label("配置信息")
        private Properties properties = new Properties();

        @NotNull
        @Label("数据源")
        private SqlSource source;

        @NotNull
        @Label("加密器")
        private SqlCipher cipher = new StandardCipher(Cipherx.NONE, null, null);

        @NotNull
        @Label("类型转换器")
        private SqlConverter converter = new StandardConverter();

        @Getter
        @Label("元数据管理")
        private SqlMetaManager metaManager = new StandardMetaManager();

        @NotNull
        @Label("实体转换器")
        private SqlTransformer transformer = new StandardTransformer();

        private List<SqlInterceptor> interceptors = new ArrayList<>();

        public Builder addInterceptor(SqlInterceptor interceptor) {
            this.interceptors.add(interceptor);
            return this;
        }

        public StandardExecutor build() {
            Validatex.Default().validateBean(this);
            var executor = new StandardExecutor(this.properties, this.source, this.converter, this.metaManager, this.cipher, this.transformer);
            executor.interceptors.addAll(this.interceptors);
            return executor;
        }
    }
}
