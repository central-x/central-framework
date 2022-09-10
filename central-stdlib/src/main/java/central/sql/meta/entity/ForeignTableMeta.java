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
 * 多对多关系
 *
 * @author Alan Yeh
 * @since 2022/08/01
 */
@Data
public class ForeignTableMeta {
    /**
     * 关系别名
     */
    private String alias;

    /**
     * 关联表实体信息
     */
    private EntityMeta entity;

    /**
     * 目标实体信息
     */
    private EntityMeta target;

    /**
     * 主表关联字段
     */
    private PropertyMeta property;

    /**
     * 关联实体与主表关联字段
     */
    private PropertyMeta relationProperty;

    /**
     * 目标实体的关联字段
     */
    private PropertyMeta targetProperty;

    /**
     * 关联实体与目标实体关联字段
     */
    private PropertyMeta targetRelationProperty;

    @Override
    public String toString() {
        return Stringx.format("ForeignTableMeta(alias: {}, target: {})", this.alias, this.target);
    }
}
