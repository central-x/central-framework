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

package central.sql.datasource.dynamic;

import central.lang.Stringx;
import central.sql.SqlSource;
import central.sql.datasource.dynamic.lookup.LookupKeyHolder;
import central.sql.exception.DataSourceNotFoundException;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.Delegate;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * 动态数据源
 *
 * @author Alan Yeh
 * @since 2022/09/23
 */
public abstract class DynamicSqlSource implements SqlSource {

    /**
     * 主数据源
     */
    public abstract SqlSource getMaster();

    @Delegate
    @SneakyThrows(SQLException.class)
    protected SqlSource determineDataSource() {
        var lookupKey = this.determineLookupKey();

        if (Stringx.isNotBlank(LookupKeyHolder.getLookupKey())) {
            // 强制切换数据源
            lookupKey = LookupKeyHolder.getLookupKey();
        }

        return this.getDataSourceByName(lookupKey);
    }

    /**
     * 决定当前数据源
     */
    protected String determineLookupKey() {
        return "master";
    }

    protected List<String> getDataSourceNames() {
        return Collections.singletonList("master");
    }

    /**
     * 根据数据源名称获取数据源
     *
     * @param name 数据源名称
     * @return 数据源
     */
    protected SqlSource getDataSourceByName(String name) throws SQLException {
        if ("master".equals(name)) {
            return this.getMaster();
        }

        throw new DataSourceNotFoundException(Stringx.format("数据源[{}]不存在", name));
    }
}
