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

package central.sql.meta.entity;

import central.sql.SqlConversion;
import central.util.Objectx;
import central.lang.Stringx;
import lombok.Data;

import javax.annotation.Nullable;
import java.beans.PropertyDescriptor;

/**
 * 属性信息
 *
 * @author Alan Yeh
 * @since 2022/08/01
 */
@Data
public class PropertyMeta {
    /**
     * 属性字段名
     */
    private String name;

    /**
     * 字段名
     * 如果此字段为空的话，说明开发者没有指定字段名
     */
    @Nullable
    private String columnName;

    /**
     * 获取字段名
     * 如果开发者没的指定字段名，则根据开发者指定的命名规则将属性名转成字段名
     *
     * @param conversion 命名规则
     */
    public String getColumnName(SqlConversion conversion) {
        return Objectx.get(this.columnName, conversion.getColumnName(this.name));
    }

    /**
     * 备注
     */
    @Nullable
    private String remarks;

    /**
     * 是否主键
     */
    private boolean primary;

    /**
     * 获取属性描述
     */
    private PropertyDescriptor descriptor;

    /**
     * 字段加密
     */
    private boolean encrypted;

    /**
     * 更新时忽略
     */
    private boolean updatable = true;

    /**
     * 插入时忽略（就是没有这个字段）
     */
    private boolean insertable = true;

    @Override
    public String toString() {
        return Stringx.format("PropertyMeta(name: {})", this.name);
    }
}
