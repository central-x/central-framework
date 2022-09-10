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

import central.lang.Assertx;
import central.sql.annotation.Encrypted;
import central.sql.data.Entity;
import central.sql.meta.annotation.Relation;
import central.sql.meta.annotation.TableRelation;
import central.sql.meta.entity.EntityMeta;
import central.sql.meta.entity.ForeignMeta;
import central.sql.meta.entity.ForeignTableMeta;
import central.sql.meta.entity.PropertyMeta;
import central.util.*;
import central.validation.Label;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.SneakyThrows;
import lombok.experimental.ExtensionMethod;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Entity Meta 构造
 *
 * @author Alan Yeh
 * @since 2022/09/08
 */
@ExtensionMethod({Stringx.class, Listx.class, Mapx.class, Arrayx.class})
public class EntityMetaBuilder {

    private static final Map<String, EntityMeta> metas = new ConcurrentHashMap<>();

    public static EntityMeta build(Class<? extends Entity> entity) {
        synchronized (EntityMetaBuilder.class) {
            var meta = metas.get(entity.getName());
            if (meta == null) {
                meta = new EntityMeta();
                // 提前放到 Map 里，不然会出现死锁
                metas.put(entity.getName(), meta);
                try {
                    buildEntity(entity, meta);
                } catch (Exception ex) {
                    metas.remove(entity.getName());
                    throw ex;
                }
            }
            return meta;
        }
    }

    @SneakyThrows(IntrospectionException.class)
    private static void buildEntity(Class<? extends Entity> entity, EntityMeta meta) {
        meta.setType(entity);

        // 获取表名
        var table = entity.getAnnotation(Table.class);
        if (table != null && table.name().isNotBlank()) {
            meta.setTableName(table.name());
        }

        // 获取备注
        var label = entity.getAnnotation(Label.class);
        if (label != null && label.value().isNotBlank()) {
            meta.setRemarks(label.value());
        }

        // 设置属性
        var info = Introspector.getBeanInfo(entity);
        var properties = info.getPropertyDescriptors();

        for (var descriptor : properties) {
            buildProperty(meta, descriptor);
        }

        // 处理一对一、一对多 外键关系
        var relations = entity.getAnnotationsByType(Relation.class);
        if (relations.isNotEmpty()) {
            for (var relation : relations) {
                buildForeign(meta, relation);
            }
        }

        // 处理多对多外键关系
        var tableRelations = entity.getAnnotationsByType(TableRelation.class);
        if (tableRelations.isNotEmpty()) {
            for (var relation : tableRelations) {
                buildForeignTable(meta, relation);
            }
        }
    }

    @SneakyThrows(NoSuchFieldException.class)
    private static void buildProperty(EntityMeta entity, PropertyDescriptor property) {
        if (property.getWriteMethod() == null || property.getReadMethod() == null) {
            // 读方法和写方法都必不可少
            return;
        }

        var meta = new PropertyMeta();

        var field = property.getWriteMethod().getDeclaringClass().getDeclaredField(property.getName());

        meta.setName(property.getName());
        meta.setDescriptor(property);

        // 获取字段名
        var column = property.getReadMethod().getAnnotation(Column.class);
        if (column == null) {
            column = field.getAnnotation(Column.class);
        }
        if (column != null) {
            if (column.name().isNotBlank()) {
                meta.setColumnName(column.name());
            }
            meta.setUpdatable(column.updatable());
            meta.setInsertable(column.insertable());
        }

        // 获取备注
        var label = property.getReadMethod().getAnnotation(Label.class);
        if (label == null) {
            label = field.getAnnotation(Label.class);
        }
        if (label != null && label.value().isNotBlank()) {
            meta.setRemarks(label.value());
        }

        // 判断是否为主键
        var id = property.getReadMethod().getAnnotation(Id.class);
        if (id == null) {
            id = field.getAnnotation(Id.class);
        }
        meta.setPrimary(id != null);

        // 判断是否加密
        var encrypted = property.getReadMethod().getAnnotation(Encrypted.class);
        if (encrypted == null) {
            encrypted = field.getAnnotation(Encrypted.class);
        }
        meta.setEncrypted(encrypted != null);

        entity.getProperties().add(meta);
    }

    private static void buildForeign(EntityMeta entity, Relation relation) {
        var foreign = new ForeignMeta();
        foreign.setAlias(relation.alias());

        var property = entity.getProperty(Objectx.get(relation.property(), entity.getId().getName()));
        Assertx.mustNotNull(property, "创建实体[{}]的关联关系[{}]失败: 在实体[{}]中没有找到关联属性[{}]", entity.getType().getName(), relation.alias(), entity.getType().getName(), Objectx.get(relation.property(), entity.getId().getName()));
        foreign.setProperty(property);

        var target = build(relation.target());
        foreign.setTarget(target);

        var referencedProperty = target.getProperty(Objectx.get(relation.referencedProperty(), target.getId().getName()));
        Assertx.mustNotNull(referencedProperty, "创建实体[{}]的关联关系[{}]失败: 在实体[{}]中没有找到关联属性[{}]", entity.getType().getName(), relation.alias(), target.getType().getName(), Objectx.get(relation.referencedProperty(), target.getId().getName()));
        foreign.setReferencedProperty(referencedProperty);

        entity.getForeigns().add(foreign);
    }

    private static void buildForeignTable(EntityMeta entity, TableRelation relation) {
        var foreign = new ForeignTableMeta();
        foreign.setAlias(relation.alias());

        var relationTable = build(relation.table());
        foreign.setEntity(relationTable);

        var target = build(relation.target());
        foreign.setTarget(target);

        var property = entity.getProperty(Objectx.get(relation.property(), entity.getId().getName()));
        Assertx.mustNotNull(property, "创建实体[{}]的关联关系[{}]失败: 在实体[{}]中没有找到关联属性[{}]", entity.getType().getName(), relation.alias(), entity.getType().getName(), Objectx.get(relation.property(), entity.getId().getName()));
        foreign.setProperty(property);

        var relationProperty = relationTable.getProperty(relation.relationProperty());
        Assertx.mustNotNull(relationProperty, "创建实体[{}]的关联关系[{}]失败: 在关联实体[{}]中没有找到关联属性[{}]", entity.getType().getName(), relation.alias(), relationTable.getType().getName(), relation.relationProperty());
        foreign.setRelationProperty(relationProperty);

        var targetProperty = target.getProperty(Objectx.get(relation.targetProperty(), target.getId().getName()));
        Assertx.mustNotNull(targetProperty, "创建实体[{}]的关联关系[{}]失败: 在实体[{}]中没有找到关联属性[{}]", entity.getType().getName(), relation.alias(), target.getType().getName(), Objectx.get(relation.targetProperty(), target.getId().getName()));
        foreign.setTargetProperty(targetProperty);

        var targetRelationProperty = relationTable.getProperty(relation.targetRelationProperty());
        Assertx.mustNotNull(targetRelationProperty, "创建实体[{}]的关联关系[{}]失败: 在关联实体[{}]中没有找到关联属性[{}]", entity.getType().getName(), relation.alias(), relationTable.getType().getName(), relation.targetRelationProperty());
        foreign.setTargetRelationProperty(targetRelationProperty);

        entity.getForeignTables().add(foreign);
    }
}
