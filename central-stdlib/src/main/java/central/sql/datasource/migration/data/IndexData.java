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

import central.sql.meta.database.IndexMeta;
import central.validation.Label;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 索引信息
 *
 * @author Alan Yeh
 * @since 2022/08/31
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IndexData implements Serializable {
    @Serial
    private static final long serialVersionUID = 2335606619951492957L;

    @Label("索引名")
    private String name;

    @Label("是否唯一")
    private boolean unique = false;

    @Label("字段名")
    private String column;

    public static IndexData fromMeta(IndexMeta meta) {
        var data = new IndexData();
        data.setName(meta.getName());
        data.setUnique(meta.isUnique());
        data.setColumn(meta.getColumn());
        return data;
    }
}
