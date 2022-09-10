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

package central.sql.meta;

import central.sql.SqlExecutor;
import central.sql.meta.database.ColumnMeta;
import central.sql.meta.database.DatabaseMeta;
import central.sql.meta.database.IndexMeta;
import central.sql.meta.database.TableMeta;
import central.util.Arrayx;
import central.util.Listx;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Optional;

/**
 * Database Meta 构造
 *
 * @author Alan Yeh
 * @since 2022/09/08
 */
public class DatabaseMetaBuilder {
    public static DatabaseMeta build(SqlExecutor executor, String... prefixes) throws SQLException {
        Connection connection = executor.getConnection();
        try {
            prefixes = Arrayx.asStream(prefixes).map(String::toUpperCase).toList().toArray(new String[0]);

            var databaseMeta = connection.getMetaData();
            var meta = new DatabaseMeta();

            // 解析数据库元数据
            meta.setUrl(databaseMeta.getURL());
            meta.setName(databaseMeta.getDatabaseProductName());
            meta.setVersion(databaseMeta.getDatabaseProductVersion());
            meta.setDriverName(databaseMeta.getDriverName());
            meta.setDriverVersion(databaseMeta.getDriverVersion());

            // 解析表、视图列表
            try (var cursor = databaseMeta.getTables(null, null, null, new String[]{"TABLE", "VIEW"})) {
                while (cursor.next()) {
                    var table = new TableMeta();
                    table.setCatalog(TableMetas.CATALOG.getString(cursor));
                    table.setSchema(TableMetas.SCHEME.getString(cursor));
                    table.setName(TableMetas.NAME.getString(cursor));
                    table.setRemarks(TableMetas.REMARKS.getString(cursor));

                    if (Arrayx.isNotEmpty(prefixes)) {
                        // 只加载指定前缀的表
                        if (Arrays.stream(prefixes).noneMatch(it -> table.getName().startsWith(it))) {
                            continue;
                        }
                    }

                    meta.getTables().put(table.getName(), table);
                }
            }
            // 解析表、视图的字段信息、索引信息
            for (var table : meta.getTables().values()) {
                // 获取主键
                var primaryKeys = Listx.newArrayList();
                try (ResultSet cursor = databaseMeta.getPrimaryKeys(table.getCatalog(), table.getSchema(), table.getName())) {
                    while (cursor.next()) {
                        primaryKeys.add(ColumnMetas.NAME.getString(cursor));
                    }
                }

                // 获取字段
                try (ResultSet cursor = databaseMeta.getColumns(table.getCatalog(), table.getSchema(), table.getName(), "%")) {
                    while (cursor.next()) {
                        var column = new ColumnMeta();
                        column.setName(ColumnMetas.NAME.getString(cursor));
                        column.setType(ColumnMetas.DATA_TYPE.getInt(cursor));
                        column.setSize(ColumnMetas.SIZE.getInt(cursor));
                        column.setRemarks(ColumnMetas.REMARKS.getString(cursor));
                        // 判断其是不是主键
                        column.setPrimary(primaryKeys.contains(column.getName()));
                        table.getColumns().put(column.getName(), column);
                    }
                }

                // 获取索引
                try (ResultSet cursor = databaseMeta.getIndexInfo(table.getCatalog(), table.getSchema(), table.getName(), false, false)) {
                    while (cursor.next()) {
                        var index = new IndexMeta();
                        index.setName(IndexMetas.NAME.getString(cursor));
                        index.setColumn(IndexMetas.COLUMN.getString(cursor));
                        index.setUnique(IndexMetas.UNIQUE.getBoolean(cursor));
                        table.getIndies().put(index.getName(), index);
                    }
                }
            }

            return meta;
        } finally {
            executor.returnConnection(connection);
        }
    }

    @Getter
    @RequiredArgsConstructor
    private enum TableMetas {
        CATALOG("Catalog", "TABLE_CAT"),
        SCHEME("Scheme", "TABLE_SCHEM"),
        NAME("Table Name", "TABLE_NAME"),
        REMARKS("Remarks", "REMARKS");

        private final String name;
        private final String column;

        public String getString(ResultSet cursor) throws SQLException {
            return Optional.ofNullable(cursor.getString(this.column)).map(String::toUpperCase).orElse(null);
        }
    }

    @Getter
    @RequiredArgsConstructor
    private enum ColumnMetas {
        NAME("Column Name", "COLUMN_NAME"),
        DATA_TYPE("Data Type", "DATA_TYPE"),
        SIZE("Column Size", "COLUMN_SIZE"),
        REMARKS("Remarks", "REMARKS");

        private final String name;
        private final String column;

        public String getString(ResultSet cursor) throws SQLException {
            return Optional.ofNullable(cursor.getString(this.column)).map(String::toUpperCase).orElse(null);
        }

        public int getInt(ResultSet cursor) throws SQLException {
            return cursor.getInt(column);
        }
    }

    @Getter
    @RequiredArgsConstructor
    private enum IndexMetas {
        NAME("Index Name", "INDEX_NAME"),
        COLUMN("Column Name", "COLUMN_NAME"),
        UNIQUE("Unique", "NON_UNIQUE");

        private final String name;
        private final String column;

        public String getString(ResultSet cursor) throws SQLException {
            return Optional.ofNullable(cursor.getString(this.column)).map(String::toUpperCase).orElse(null);
        }

        public boolean getBoolean(ResultSet cursor) throws SQLException {
            return cursor.getBoolean(this.column);
        }
    }
}
