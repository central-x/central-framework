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
import central.validation.Label;
import central.validation.Validatable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 表结构
 *
 * @author Alan Yeh
 * @since 2022/08/29
 */
public interface Table {
    /**
     * 表名
     */
    String getName();

    /**
     * 备注
     */
    String getRemarks();

    /**
     * 字段信息
     */
    List<Column> getColumns();

    /**
     * 获取字段信息
     *
     * @param name 字段名
     */
    Column getColumn(String name);

    /**
     * 添加字段
     *
     * @param column 字段信息
     */
    Table addColumn(Column column);

    /**
     * 获取索引列表
     */
    List<Index> getIndices();

    /**
     * 获取索引信息
     *
     * @param name 索引名
     */
    Index getIndex(String name);

    /**
     * 添加索引
     *
     * @param index 索引信息
     */
    Table addIndex(Index index);

    /**
     * 删除表
     */
    void drop();

    /**
     * 重命名表
     *
     * @param newName 新表名
     */
    void rename(String newName);

    static Table of(String name, String remarks, List<Column> columns, List<Index> indices) {
        var impl = new Impl();
        impl.setName(name);
        impl.setRemarks(remarks);
        impl.setColumns(columns);
        impl.setIndices(indices);
        impl.validate();
        return impl;
    }

    @Data
    class Impl implements Table, Validatable {

        @NotBlank
        @Size(max = 30)
        @Label("表名")
        private String name;

        @NotBlank
        @Size(max = 30)
        @Label("备注")
        private String remarks;

        @Valid
        @NotEmpty
        @Label("字段")
        private List<Column> columns = new ArrayList<>();

        @Valid
        @Label("索引")
        private List<Index> indices = new ArrayList<>();

        @Override
        public Column getColumn(String name) {
            return columns.stream().filter(it -> it.getName().equals(name)).findFirst().orElse(null);
        }

        @Override
        public Table addColumn(Column column) {
            Assertx.mustNull(this.getColumn(column.getName()), "字段[{}]已存在", column.getName());
            this.columns.add(column);
            return this;
        }

        @Override
        public Index getIndex(String name) {
            return this.indices.stream().filter(it -> it.getName().equals(name)).findFirst().orElse(null);
        }

        @Override
        public Table addIndex(Index index) {
            Assertx.mustNull(this.getIndex(index.getName()), "索引[{}]已存在", index.getName());
            Assertx.mustNotNull(this.getColumn(index.getColumn()), "索引[{}]指定的字段[{}]不存在", index.getName(), index.getColumn());
            this.indices.add(index);
            return this;
        }

        @Override
        public void drop() {
            throw new UnsupportedOperationException("不支持删除表操作");
        }

        @Override
        public void rename(String newName) {
            throw new UnsupportedOperationException("不支持重命名表操作");
        }
    }
}
