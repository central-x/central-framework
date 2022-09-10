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

package central.sql.datasource.migration.data;

import central.sql.SqlType;
import central.sql.meta.database.ColumnMeta;
import central.validation.Label;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 字段信息
 *
 * @author Alan Yeh
 * @since 2022/08/31
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ColumnData implements Serializable {
    @Serial
    private static final long serialVersionUID = -7999928835281624993L;

    @Label("字段名")
    private String name;

    @Label("是否主键")
    private boolean primary = false;

    @Label("字段类型")
    private SqlType type;

    @Label("字段长度")
    private Integer size;

    @Label("备注")
    private String remarks;

    public static ColumnData fromMeta(ColumnMeta meta) {
        var data = new ColumnData();
        data.setName(meta.getName());
        data.setPrimary(meta.isPrimary());
        data.setType(SqlType.resolve(meta.getType()));
        data.setRemarks(meta.getRemarks());
        return data;
    }
}
