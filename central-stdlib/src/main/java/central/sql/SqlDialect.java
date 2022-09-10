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

package central.sql;

import central.sql.builder.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 数据库方言
 *
 * @author Alan Yeh
 * @since 2022/08/03
 */
@Getter
@AllArgsConstructor
public enum SqlDialect {
    /**
     * MySql
     */
    MySql("MySql", new MySqlBuilder()),
    /**
     * Oracle
     */
    Oracle("Oracle", new OracleBuilder()),
    /**
     * 达梦
     */
    Dameng("Dameng", new DamengBuilder()),
    /**
     * 人大金仓
     */
    Kingbase("Kingbase", new KingbaseBuilder()),
    /**
     * 神舟
     */
    Oscar("Oscar", new OscarBuilder()),
    /**
     * 翰高
     */
    HighGo("HighGo", new HighGoBuilder()),
    /**
     * H2
     */
    H2("H2", new H2Builder()),
    /**
     * PostgreSql
     */
    PostgreSql("PostgreSql", new PostgreSqlBuilder()),
    /**
     * 海量数据
     */
    Vastbase("Vastbase", new VastbaseBuilder());


    private final String name;

    private final SqlBuilder builder;

    public static SqlDialect fromUrl(String jdbcUrl) {
        if (jdbcUrl == null) {
            return null;
        }
        if (jdbcUrl.startsWith("jdbc:mysql:")) {
            return MySql;
        } else if (jdbcUrl.startsWith("jdbc:oracle:")) {
            return Oracle;
        } else if (jdbcUrl.startsWith("jdbc:dm:")) {
            return Dameng;
        } else if (jdbcUrl.startsWith("jdbc:kingbase8:")) {
            return Kingbase;
        } else if (jdbcUrl.startsWith("jdbc:oscar:")) {
            return Oscar;
        } else if (jdbcUrl.startsWith("jdbc:highgo:")) {
            return HighGo;
        } else if (jdbcUrl.startsWith("jdbc:h2:")) {
            return H2;
        } else if (jdbcUrl.startsWith("jdbc:postgresql:")) {
            return PostgreSql;
        } else if (jdbcUrl.startsWith("jdbc:vastbase:")) {
            return Vastbase;
        } else {
            throw new IllegalArgumentException("不支持的数据库类型: " + jdbcUrl);
        }
    }
}
