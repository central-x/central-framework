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

import central.util.Listx;
import lombok.Data;

import java.util.List;

/**
 * Sql Script
 *
 * @author Alan Yeh
 * @since 2022/08/01
 */
@Data
public class SqlScript {
    /**
     * Sql
     */
    private String sql;

    /**
     * Sql 参数
     */
    private List<Object> args;

    public SqlScript(String sql, List<Object> args) {
        this.sql = sql;
        if (Listx.isNullOrEmpty(args)) {
            this.args = Listx.newArrayList();
        } else {
            this.args = args;
        }
    }

    public SqlScript(String sql, Object... args) {
        this.sql = sql;
        this.args = Listx.newArrayList(args);
    }

    public static SqlScript of(String sql, Object... args) {
        return new SqlScript(sql, args);
    }
}
