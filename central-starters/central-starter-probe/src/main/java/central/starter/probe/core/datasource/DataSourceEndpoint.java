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

package central.starter.probe.core.datasource;

import central.lang.Assertx;
import central.lang.Stringx;
import central.sql.SqlDialect;
import central.sql.SqlType;
import central.starter.probe.core.Endpoint;
import central.starter.probe.core.ProbeException;
import central.util.Logx;
import central.util.Mapx;
import central.validation.Label;
import central.validation.Validatex;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Setter;
import lombok.experimental.ExtensionMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 数据源探针
 *
 * @author Alan Yeh
 * @since 2023/12/29
 */
@Slf4j
@ExtensionMethod(Logx.class)
public class DataSourceEndpoint implements Endpoint, InitializingBean, BeanNameAware {

    @Setter
    private String beanName;

    /**
     * 数据库驱动
     * <p>
     * 如果为空时，通过 url 进行推理
     */
    @Setter
    @NotBlank
    @Size(max = 512)
    @Label("数据库驱动")
    private String driver;

    /**
     * 数据库连接字符串
     */
    @Setter
    @NotBlank
    @Size(max = 4096)
    @Label("数据库连接字符串")
    private String url;

    /**
     * 用户名
     */
    @Setter
    @NotBlank
    @Size(max = 128)
    @Label("用户名")
    private String username;

    /**
     * 密码
     */
    @Setter
    @NotBlank
    @Size(max = 256)
    @Label("密码")
    private String password;

    /**
     * 查询测试
     * <p>
     * 如果为空时，则根据数据库类型自动选择对应的 sql
     */
    @Setter
    @Size(max = 4096)
    @Label("查询测试")
    private String query;

    private SqlDialect dialect;

    @Override
    public void afterPropertiesSet() throws Exception {
        Validatex.Default().validate(this);

        // 数据库方言
        this.dialect = Assertx.requireNotNull(SqlDialect.resolve(this.url), IllegalArgumentException::new, Stringx.format("不支持的数据库类型: {}", this.url));

        // 如果未设定查询测试语句，则根据方言设定默认值
        if (Stringx.isNullOrBlank(this.query)) {
            switch (this.dialect) {
                case MySql, PostgreSql -> this.query = "SELECT 1";
                case Oracle, Kingbase, Oscar, H2, Vastbase, Dameng -> this.query = "SELECT 1 FROM DUAL";
                case HighGo -> this.query = "select version()";
                default -> throw new IllegalArgumentException("不支持的数据库类型");
            }
        }
    }

    private final ThreadLocal<SimpleDateFormat> formatter = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"));

    @Override
    public void perform() throws ProbeException {
        ProbeException error = null;

        try {
            // 加载驱动
            Class.forName(this.driver);
        } catch (ClassNotFoundException cause) {
            error = new ProbeException(Stringx.format("无法加载数据库驱动[{}]: {}", this.driver, cause.getLocalizedMessage()), cause);
        }

        Map<String, String> metadata = null;
        Map<String, String> data = null;
        // 建立数据库边接
        try (Connection connection = DriverManager.getConnection(this.url, this.username, this.password)) {
            // 获取数据库元数据
            metadata = this.queryMetadata(connection);

            // 执行查询
            data = this.queryData(connection);
        } catch (SQLException cause) {
            error = new ProbeException(Stringx.format("数据库探测异常: " + cause.getLocalizedMessage()), cause);
        }

        // 输出探测信息
        var builder = new StringBuilder("\n").append("┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ ".wrap(Logx.Color.WHITE)).append("Probe Endpoint".wrap(Logx.Color.PURPLE)).append(" ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━".wrap(Logx.Color.WHITE)).append("\n");
        builder.append("┣ ".wrap(Logx.Color.WHITE)).append("Endpoint".wrap(Logx.Color.BLUE)).append(": ").append(this.beanName).append("\n");
        builder.append("┣ ".wrap(Logx.Color.WHITE)).append("Type".wrap(Logx.Color.BLUE)).append(": ").append("DataSource\n");
        builder.append("┣ ".wrap(Logx.Color.WHITE)).append("Params".wrap(Logx.Color.BLUE)).append(": ").append("\n");
        builder.append("┃ ".wrap(Logx.Color.WHITE)).append("- driver: ").append(this.driver).append("\n");
        builder.append("┃ ".wrap(Logx.Color.WHITE)).append("- url: ").append(this.url).append("\n");
        if (Stringx.isNotBlank(this.username)) {
            builder.append("┃ ".wrap(Logx.Color.WHITE)).append("- username: ").append(this.username).append("\n");
        }
        if (Stringx.isNotBlank(this.password)) {
            builder.append("┃ ".wrap(Logx.Color.WHITE)).append("- password: ").append(Stringx.paddingLeft("", this.password.length(), '*')).append("\n");
        }
        builder.append("┃ ".wrap(Logx.Color.WHITE)).append("- query: ").append(this.query).append("\n");
        builder.append("┣━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━".wrap(Logx.Color.WHITE)).append("\n");
        builder.append("┣ ".wrap(Logx.Color.WHITE)).append("Probe Status".wrap(Logx.Color.BLUE)).append(": ").append(error == null ? "SUCCESS".wrap(Logx.Color.GREEN) : "ERROR".wrap(Logx.Color.RED)).append("\n");
        if (error != null) {
            // 探测失败
            builder.append("┣ ".wrap(Logx.Color.WHITE)).append("Error Message".wrap(Logx.Color.BLUE)).append(": ").append(error.getCause().getLocalizedMessage().replace("\n", "\n┃ ")).append("\n");
        } else {
            // 探测成功
            if (Mapx.isNotEmpty(metadata)) {
                builder.append("┣ ".wrap(Logx.Color.WHITE)).append("Database Metadata".wrap(Logx.Color.BLUE)).append(":\n");
                for (var entry : metadata.entrySet()) {
                    builder.append("┃ ".wrap(Logx.Color.WHITE)).append("- ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
                }
            }
            builder.append("┣ ".wrap(Logx.Color.WHITE)).append("Query Result".wrap(Logx.Color.BLUE)).append(":\n");

            // 打印第一行结果
            var title = new StringBuilder("|");
            var content = new StringBuilder("|");
            for (var entry : data.entrySet()) {
                var length = Math.max(entry.getKey().length(), entry.getValue().length()) + 2;
                title.append(Stringx.paddingBoth(entry.getKey(), length, ' ')).append("|");
                content.append(Stringx.paddingBoth(entry.getValue(), length, ' ')).append("|");
            }
            builder.append("┣ ".wrap(Logx.Color.WHITE)).append(title).append("\n");
            builder.append("┣ ".wrap(Logx.Color.WHITE)).append(Stringx.paddingLeft("", title.length(), '-')).append("\n");
            builder.append("┣ ".wrap(Logx.Color.WHITE)).append(content).append("\n");
        }

        builder.append("┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━".wrap(Logx.Color.WHITE));
        if (error != null) {
            log.error(builder.toString());
        } else {
            log.info(builder.toString());
        }
        if (error != null) {
            throw error;
        }
    }

    /**
     * 获取数据库元数据
     */
    private Map<String, String> queryMetadata(Connection connection) throws SQLException {
        var databaseMeta = connection.getMetaData();

        var metadata = new LinkedHashMap<String, String>();
        metadata.put("Product", Stringx.format("{} {}", databaseMeta.getDatabaseProductName(), databaseMeta.getDatabaseProductVersion()));
        metadata.put("Driver", Stringx.format("{} {}", databaseMeta.getDriverName(), databaseMeta.getDriverVersion()));
        return metadata;
    }

    /**
     * 获取数据库测试查询结果
     */
    private Map<String, String> queryData(Connection connection) throws SQLException {
        try (var statement = connection.prepareStatement(this.query)) {
            try (var resultSet = statement.executeQuery()) {
                var metadata = resultSet.getMetaData();

                // 只取第一行结果
                if (!resultSet.next()) {
                    throw new ProbeException(Stringx.format("执行 Sql [{}] 时没有返回结果", this.query));
                }
                var data = new LinkedHashMap<String, String>(metadata.getColumnCount());

                // 取第一行结果
                for (int i = 1, cols = metadata.getColumnCount(); i <= cols; i++) {
                    // 字段名
                    var columnName = metadata.getColumnLabel(i);
                    if (Stringx.isNullOrBlank(columnName)) {
                        columnName = metadata.getColumnName(i);
                    }

                    // 值
                    var columnType = metadata.getColumnType(i);
                    var sqlType = Assertx.requireNotNull(SqlType.resolve(columnType), SQLException::new, Stringx.format("不支持的数据库字段类型[{}]", columnType));

                    switch (sqlType) {
                        case BLOB -> data.put(columnName, "<blob>");
                        case CLOB -> data.put(columnName, "<clob>");
                        case LONG -> {
                            var value = (Long) sqlType.getResolver().resolve(this.dialect, resultSet, metadata, i);
                            data.put(columnName, value.toString());
                        }
                        case BOOLEAN -> {
                            var value = (Boolean) sqlType.getResolver().resolve(this.dialect, resultSet, metadata, i);
                            data.put(columnName, value.toString());
                        }
                        case INTEGER -> {
                            var value = (Integer) sqlType.getResolver().resolve(this.dialect, resultSet, metadata, i);
                            data.put(columnName, value.toString());
                        }
                        case DATETIME -> {
                            var value = (Timestamp) sqlType.getResolver().resolve(this.dialect, resultSet, metadata, i);
                            data.put(columnName, formatter.get().format(value));
                        }
                        case BIG_DECIMAL -> {
                            var value = (BigDecimal) sqlType.getResolver().resolve(this.dialect, resultSet, metadata, i);
                            data.put(columnName, value.toString());
                        }
                        case STRING -> {
                            var value = (String) sqlType.getResolver().resolve(this.dialect, resultSet, metadata, i);
                            data.put(columnName, value);
                        }
                        case UNKNOWN -> {
                            var value = sqlType.getResolver().resolve(this.dialect, resultSet, metadata, i);
                            data.put(columnName, value.toString());
                        }
                    }
                }

                return data;
            }
        }
    }
}
