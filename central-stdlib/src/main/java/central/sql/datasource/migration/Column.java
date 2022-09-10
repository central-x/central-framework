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

import central.sql.SqlType;
import central.validation.Label;
import central.validation.Validatable;
import lombok.Data;

/**
 * 字段
 *
 * @author Alan Yeh
 * @since 2022/08/29
 */
public interface Column {
    /**
     * 字段名
     */
    String getName();

    /**
     * 是否主键
     */
    boolean isPrimary();

    /**
     * 字段类型
     */
    SqlType getType();

    /**
     * 字段长度
     */
    Integer getSize();

    /**
     * 备注
     */
    String getRemarks();

    /**
     * 删除字段
     */
    void drop();

    /**
     * 重命名字段
     *
     * @param newName 新的字段名
     */
    void rename(String newName);

    static Column of(String name, SqlType type, String remarks) {
        return of(name, false, type, null, remarks);
    }

    static Column of(String name, SqlType type, Integer size, String remarks) {
        return of(name, false, type, size, remarks);
    }

    static Column of(String name, boolean primary, SqlType type, Integer size, String remarks) {
        var impl = new Impl();
        impl.setName(name);
        impl.setPrimary(primary);
        impl.setType(type);
        impl.setSize(size);
        impl.setRemarks(remarks);
        impl.validate();
        return impl;
    }

    @Data
    class Impl implements Column, Validatable {
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

        @Override
        public void drop() {
            throw new UnsupportedOperationException("不支持删除字段操作");
        }

        @Override
        public void rename(String newName) {
            throw new UnsupportedOperationException("不支持重命名字段操作");
        }
    }
}
