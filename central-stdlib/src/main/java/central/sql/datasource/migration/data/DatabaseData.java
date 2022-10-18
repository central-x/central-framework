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

import central.sql.meta.database.DatabaseMeta;
import central.util.Mapx;
import central.validation.Label;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据库
 *
 * @author Alan Yeh
 * @since 2022/09/08
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DatabaseData {
    @Label("数据库 URL")
    private String url;

    @Label("数据库产品名称")
    private String name;

    @Label("数据库版本")
    private String version;

    @Label("数据库驱动名称")
    private String driverName;

    @Label("数据库驱动版本")
    private String driverVersion;

    @Label("表信息")
    private Map<String, TableData> tables = Mapx.caseInsensitive(new HashMap<>());

    public static DatabaseData fromMeta(DatabaseMeta meta) {
        var data = new DatabaseData();
        data.setUrl(meta.getUrl());
        data.setName(meta.getName());
        data.setVersion(meta.getVersion());
        data.setDriverName(meta.getDriverName());
        data.setDriverVersion(meta.getDriverVersion());
        meta.getTables().forEach((key, value) -> data.getTables().put(key, TableData.fromMeta(value)));
        return data;
    }
}
