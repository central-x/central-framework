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

package central.sql.datasource.migration;

import central.lang.Assertx;
import central.lang.CompareResult;
import central.sql.SqlExecutor;
import central.util.Version;
import central.validation.Label;
import central.validation.Validatable;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.sql.SQLException;

/**
 * 数据库迁移
 *
 * @author Alan Yeh
 * @since 2022/08/23
 */
@Data
public abstract class Migration implements Validatable {
    @Label("开始版本")
    private final Version begin;

    @NotNull
    @Label("结束版本")
    private final Version end;

    public Migration(Version begin, Version end) {
        this.begin = begin;
        this.end = end;
    }

    public Migration(Version end) {
        this.begin = Version.of("0");
        this.end = end;
        Assertx.mustTrue(CompareResult.GT.matches(this.end, this.begin), "结束版本[end]必须比开始版本[start]大");
    }

    /**
     * 表结构迁移
     *
     * @param database 数据库
     */
    public void upgrade(Database database) throws SQLException {

    }

    /**
     * 回滚表结构
     *
     * @param database 数据库
     */
    public void downgrade(Database database) throws SQLException {

    }

    /**
     * 数据移迁
     *
     * @param executor Sql 执行器
     */
    public void upgrade(SqlExecutor executor) throws SQLException {

    }

    /**
     * 回滚数据
     *
     * @param executor Sql 执行器
     */
    public void downgrade(SqlExecutor executor) throws SQLException {

    }
}
