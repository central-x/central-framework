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

package central.sql.resolver;

import central.io.IOStreamx;
import central.sql.SqlDialect;
import central.sql.SqlTypeResolver;
import lombok.Cleanup;

import java.io.IOException;
import java.io.Reader;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

/**
 * String 解析
 *
 * @author Alan Yeh
 * @since 2022/08/10
 */
public class StringTypeResolver implements SqlTypeResolver {
    @Override
    public Object resolve(SqlDialect dialect, ResultSet cursor, ResultSetMetaData meta, int index) throws SQLException {
        if (SqlDialect.Oracle == dialect) {
            var type = meta.getColumnType(index);
            switch (type) {
                case Types.CLOB -> {
                    var clob = cursor.getClob(index);
                    if (clob == null) {
                        return null;
                    }
                    return this.read(clob.getCharacterStream());
                }
                case Types.NCLOB -> {
                    var nclob = cursor.getNClob(index);
                    if (nclob == null) {
                        return null;
                    }
                    return this.read(nclob.getCharacterStream());
                }
                default -> {
                    return cursor.getString(index);
                }
            }
        } else {
            // 其它数据库直接通过以下方式获取字符串
            return cursor.getString(index);
        }
    }

    private String read(Reader reader) throws SQLException {
        try {
            var result = new StringBuilder();
            int length;
            char[] buffer = new char[IOStreamx.BUFFER_SIZE];
            while ((length = reader.read(buffer, 0, IOStreamx.BUFFER_SIZE)) != -1) {
                result.append(buffer, 0, length);
            }
            return result.toString();
        } catch (IOException ex) {
            throw new SQLException(ex);
        } finally {
            try {
                reader.close();
            } catch (IOException ex) {
                throw new SQLException(ex);
            }
        }
    }
}
