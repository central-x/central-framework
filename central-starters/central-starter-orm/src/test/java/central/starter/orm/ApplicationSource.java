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
import central.sql.datasource.dynamic.DynamicSqlSource;
import central.sql.datasource.dynamic.lookup.LookupKeyHolder;
import jakarta.servlet.http.HttpServletRequest;

import java.sql.SQLException;

/**
 * 动态数据源
 *
 * @author Alan Yeh
 * @since 2022/09/23
 */
public class ApplicationSource extends DynamicSqlSource {

    public ApplicationSource(SqlSource master) {
        super(master);
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
