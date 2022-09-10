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

package central.sql.meta.annotation;

import central.sql.data.Entity;

import java.lang.annotation.*;

/**
 * 多对多关联
 *
 * @author Alan Yeh
 * @since 2022/08/01
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(TableRelation.List.class)
public @interface TableRelation {

    /**
     * 关联关系表
     */
    Class<? extends Entity> table();

    /**
     * 从表
     */
    Class<? extends Entity> target();

    /**
     * 主表与关联关系表的关联字段
     * 默认使用主表的主键
     */
    String property() default "";

    /**
     * 关联关系表与主键的关联字段
     */
    String relationProperty();

    /**
     * 从表与关联关系表的关联字段
     * 默认使用从表的主键
     */
    String targetProperty() default "";

    /**
     * 关联关系表与从表的关联字段
     */
    String targetRelationProperty();

    /**
     * 别名（关系名）
     */
    String alias();

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        TableRelation[] value();
    }
}
