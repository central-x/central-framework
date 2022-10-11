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

package central.sql.meta.database;

import central.util.Mapx;
import central.lang.Stringx;
import lombok.Data;
import lombok.experimental.ExtensionMethod;

import java.util.HashMap;
import java.util.Map;

/**
 * 表元数据
 *
 * @author Alan Yeh
 * @since 2022/08/06
 */
@Data
@ExtensionMethod(Mapx.class)
public class TableMeta {
    /**
     * 表所在的 Catalog
     */
    private String catalog;
    /**
     * 表所在的 Schema
     */
    private String schema;
    /**
     * 表名
     */
    private String name;
    /**
     * 注释
     */
    private String remarks;
    /**
     * 字段
     */
    private Map<String, ColumnMeta> columns = new HashMap<String, ColumnMeta>().caseInsensitive().threadSafe();
    /**
     * 索引
     */
    private Map<String, IndexMeta> indies = new HashMap<String, IndexMeta>().caseInsensitive().threadSafe();

    /**
     * 获取字段元数据
     *
     * @param name 字段名
     */
    public ColumnMeta getColumn(String name) {
        return this.columns.getOrNull(name);
    }

    /**
     * 获取索引元数据
     *
     * @param name 索引名
     */
    public IndexMeta getIndex(String name) {
        return this.indies.getOrNull(name);
    }

    @Override
    public String toString() {
        return Stringx.format("TableMeta(catalog: {}, schema: {}, name: {}, remarks: {})", this.catalog, this.schema, this.name, this.remarks);
    }
}
