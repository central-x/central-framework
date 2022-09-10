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

import central.util.Stringx;
import lombok.Data;

/**
 * 外键关系元数据
 *
 * @author Alan Yeh
 * @since 2022/08/01
 */
@Data
public class ForeignMeta {
    /**
     * 关系别名
     */
    private String alias;

    /**
     * 关联的属性
     */
    private PropertyMeta property;

    /**
     * 目标实体
     */
    private EntityMeta target;

    /**
     * 目标实体属性
     */
    private PropertyMeta referencedProperty;

    @Override
    public String toString() {
        return Stringx.format("ForeignMeta(alias: {}, target: {})", this.alias, this.target);
    }
}
