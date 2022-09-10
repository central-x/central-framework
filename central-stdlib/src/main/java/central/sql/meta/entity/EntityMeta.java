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
import central.sql.data.Entity;
import central.util.Listx;
import central.util.Objectx;
import central.util.Stringx;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 实体元数据
 *
 * @author Alan Yeh
 * @since 2022/08/01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntityMeta {
    /**
     * 实体类
     */
    @Nonnull
    private Class<? extends Entity> type;

    /**
     * 表名
     * 如果为空的话，表示开发者没有指定表名
     */
    @Nullable
    private String tableName;

    /**
     * 获取表名
     * 如果开发者没的指定表名，则根据开发者指定的命名规则将类名转成表名
     *
     * @param conversion 命名规则
     */
    public String getTableName(SqlConversion conversion) {
        return Objectx.get(this.tableName, conversion.getTableName(this.type));
    }

    /**
     * 备注
     */
    @Nullable
    private String remarks;

    /**
     * 主键
     */
    @Nonnull
    private PropertyMeta id;

    /**
     * 属性列表
     */
    @Nonnull
    private List<PropertyMeta> properties = new ArrayList<>();

    /**
     * 获取属性
     *
     * @param property 属性名
     */
    @Nullable
    public PropertyMeta getProperty(String property) {
        return Listx.asStream(this.getProperties())
                .filter(it -> Objects.equals(it.getName(), property))
                .findFirst()
                .orElse(null);
    }

    /**
     * 外键关系（一对一，一对多）
     */
    private List<ForeignMeta> foreigns = new ArrayList<>();

    /**
     * 根据 alias 查询外键关系（一对一，一对多）
     *
     * @param alias 关系别名
     */
    @Nullable
    public ForeignMeta getForeign(String alias) {
        return Listx.asStream(this.getForeigns())
                .filter(it -> Objects.equals(it.getAlias(), alias))
                .findFirst()
                .orElse(null);
    }

    /**
     * 外键关系（多对多）
     */
    private List<ForeignTableMeta> foreignTables = new ArrayList<>();

    /**
     * 根据 alias 查询外键关系（多对多）
     *
     * @param alias 关系别名
     */
    @Nullable
    public ForeignTableMeta getForeignTable(String alias) {
        return Listx.asStream(this.getForeignTables())
                .filter(it -> Objects.equals(it.getAlias(), alias))
                .findFirst()
                .orElse(null);
    }

    @Override
    public String toString() {
        return Stringx.format("EntityMeta(type: {})", this.type.getName());
    }
}
