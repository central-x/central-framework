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
 * 数据库元数据
 *
 * @author Alan Yeh
 * @since 2022/09/01
 */
@Data
@ExtensionMethod(Mapx.class)
public class DatabaseMeta {
    /**
     * 数据库 URL
     */
    private String url;
    /**
     * 数据库产品名称
     */
    private String name;
    /**
     * 数据库版本
     */
    private String version;
    /**
     * 数据库驱动名称
     */
    private String driverName;
    /**
     * 数据库驱动版本
     */
    private String driverVersion;
    /**
     * 表信息
     */
    private final Map<String, TableMeta> tables = new HashMap<String, TableMeta>().caseInsensitive().threadSafe();

    /**
     * 获取表元数据
     *
     * @param name 表名
     */
    public TableMeta getTable(String name) {
        return this.tables.getOrNull(name);
    }

    @Override
    public String toString() {
        return Stringx.format("DatabaseMeta(name: {}, version: {}, driverName: {}, url: {})", this.name, this.version, this.driverName, this.url);
    }
}
