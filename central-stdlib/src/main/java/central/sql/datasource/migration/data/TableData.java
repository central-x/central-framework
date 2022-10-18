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

import central.sql.meta.database.TableMeta;
import central.util.Mapx;
import central.validation.Label;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 表信息
 *
 * @author Alan Yeh
 * @since 2022/08/31
 */
@Data
public class TableData implements Serializable {
    @Serial
    private static final long serialVersionUID = -7464498727405062652L;

    @Label("表名")
    private String name;

    @Label("备注")
    private String remarks;

    @Label("字段")
    private Map<String, ColumnData> columns = Mapx.caseInsensitive(new HashMap<>());

    @Label("索引")
    private Map<String, IndexData> indices = Mapx.caseInsensitive(new HashMap<>());

    public static TableData fromMeta(TableMeta meta) {
        var data = new TableData();
        data.setName(meta.getName());
        data.setRemarks(meta.getRemarks());
        meta.getColumns().forEach((key, value) -> data.getColumns().put(key, ColumnData.fromMeta(value)));
        meta.getIndies().forEach((key, value) -> data.getIndices().put(key, IndexData.fromMeta(value)));
        return data;
    }
}
