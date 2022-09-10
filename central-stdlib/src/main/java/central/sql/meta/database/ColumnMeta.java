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

import central.util.Stringx;
import lombok.Data;

import java.sql.Types;

/**
 * 字段元数据
 *
 * @author Alan Yeh
 * @since 2022/08/06
 */
@Data
public class ColumnMeta {
    /**
     * 字段名
     */
    private String name;
    /**
     * 是否主键
     */
    private boolean primary;
    /**
     * 字段类型
     *
     * @see Types
     */
    private int type;
    /**
     * 字段长度
     */
    private Integer size;
    /**
     * 小数位数
     */
//    private Integer digit;
    /**
     * 备注
     */
    private String remarks;

    @Override
    public String toString() {
        return Stringx.format("ColumnMeta(name: {}, primary: {}, type: {}, remarks: {})", this.name, this.primary, this.type, this.remarks);
    }
}
