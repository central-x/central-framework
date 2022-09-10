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

import central.validation.Label;
import central.validation.Validatable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 索引信息
 *
 * @author Alan Yeh
 * @since 2022/08/29
 */
public interface Index {

    /**
     * 索引名
     */
    String getName();

    /**
     * 字段名
     */
    String getColumn();

    /**
     * 是否唯一
     */
    boolean isUnique();

    /**
     * 删除索引
     */
    void drop();

    static Index of(String name, boolean unique, String column) {
        var impl = new Impl();
        impl.setName(name);
        impl.setUnique(unique);
        impl.setColumn(column);
        impl.validate();
        return impl;
    }

    @Data
    class Impl implements Index, Validatable {
        @NotBlank
        @Size(max = 30)
        @Label("索引名")
        private String name;

        @Label("是否唯一")
        private boolean unique = false;

        @NotBlank
        @Label("字段名")
        private String column;

        @Override
        public void drop() {
            throw new UnsupportedOperationException("不支持删除索引操作");
        }
    }
}
