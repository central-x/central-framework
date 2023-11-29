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

package central.sql.impl.standard;

import central.lang.Arrayx;
import central.lang.Assertx;
import central.lang.Stringx;
import central.sql.SqlExecutor;
import central.sql.SqlMetaManager;
import central.sql.annotation.Encrypted;
import central.sql.data.Entity;
import central.sql.meta.annotation.Relation;
import central.sql.meta.annotation.TableRelation;
import central.sql.meta.database.ColumnMeta;
import central.sql.meta.database.DatabaseMeta;
import central.sql.meta.database.IndexMeta;
import central.sql.meta.database.TableMeta;
import central.sql.meta.entity.EntityMeta;
import central.sql.meta.entity.ForeignMeta;
import central.sql.meta.entity.ForeignTableMeta;
import central.sql.meta.entity.PropertyMeta;
import central.util.*;
import central.validation.Label;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.ExtensionMethod;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

/**
 * 默认的元数据管理
 *
 * @author Alan Yeh
 * @since 2022/09/13
 */
@ExtensionMethod({Stringx.class, Listx.class, Mapx.class, Arrayx.class})
public class StandardMetaManager implements SqlMetaManager {
    private final Predicate<String> matcher;

    private final Object lock = new Object();

    public StandardMetaManager(Predicate<String> matcher) {
        this.matcher = matcher;
    }

    public StandardMetaManager() {
        this(null);
    }

    @Override
    public @Nonnull DatabaseMeta getMeta(@Nonnull SqlExecutor executor, @Nullable Predicate<String> matcher) throws SQLException {
        Connection connection = executor.getSource().getConnection();
        try {
            var databaseMeta = connection.getMetaData();
            var meta = new DatabaseMeta();

            // 解析数据库元数据
            meta.setUrl(databaseMeta.getURL());
            meta.setName(databaseMeta.getDatabaseProductName());
            meta.setVersion(databaseMeta.getDatabaseProductVersion());
            meta.setDriverName(databaseMeta.getDriverName());
            meta.setDriverVersion(databaseMeta.getDriverVersion());

            // 解析表、视图列表
            try (var cursor = databaseMeta.getTables(null, null, null, new String[]{"TABLE", "VIEW"})) {
                while (cursor.next()) {
                    var table = new TableMeta();
                    table.setCatalog(TableMetas.CATALOG.getString(cursor));
                    table.setSchema(TableMetas.SCHEME.getString(cursor));
                    table.setName(TableMetas.NAME.getString(cursor));
                    table.setRemarks(TableMetas.REMARKS.getString(cursor));

                    if (matcher != null && matcher.test(table.getName())) {
                        meta.getTables().put(table.getName(), table);
                        continue;
                    }

                    if (this.matcher == null || this.matcher.test(table.getName())) {
                        meta.getTables().put(table.getName(), table);
                    }
                }
            }
            // 解析表、视图的字段信息、索引信息
            for (var table : meta.getTables().values()) {
                // 获取主键
                var primaryKeys = Listx.newArrayList();
                try (ResultSet cursor = databaseMeta.getPrimaryKeys(table.getCatalog(), table.getSchema(), table.getName())) {
                    while (cursor.next()) {
                        primaryKeys.add(ColumnMetas.NAME.getString(cursor));
                    }
                }

                // 获取字段
                try (ResultSet cursor = databaseMeta.getColumns(table.getCatalog(), table.getSchema(), table.getName(), "%")) {
                    while (cursor.next()) {
                        var column = new ColumnMeta();
                        column.setName(ColumnMetas.NAME.getString(cursor));
                        column.setType(ColumnMetas.DATA_TYPE.getInt(cursor));
                        column.setSize(ColumnMetas.SIZE.getInt(cursor));
                        column.setRemarks(ColumnMetas.REMARKS.getString(cursor));
                        // 判断其是不是主键
                        column.setPrimary(primaryKeys.contains(column.getName()));
                        table.getColumns().put(column.getName(), column);
                    }
                }

                // 获取索引
                try (ResultSet cursor = databaseMeta.getIndexInfo(table.getCatalog(), table.getSchema(), table.getName(), false, false)) {
                    while (cursor.next()) {
                        var index = new IndexMeta();
                        index.setName(IndexMetas.NAME.getString(cursor));
                        index.setColumn(IndexMetas.COLUMN.getString(cursor));
                        index.setUnique(!IndexMetas.UNIQUE.getBoolean(cursor));
                        table.getIndies().put(index.getName(), index);
                    }
                }
            }

            return meta;
        } finally {
            executor.getSource().returnConnection(connection);
        }
    }

    @Getter
    @RequiredArgsConstructor
    private enum TableMetas {
        CATALOG("Catalog", "TABLE_CAT"),
        SCHEME("Scheme", "TABLE_SCHEM"),
        NAME("Table Name", "TABLE_NAME"),
        REMARKS("Remarks", "REMARKS");

        private final String name;
        private final String column;

        public String getString(ResultSet cursor) throws SQLException {
            return cursor.getString(this.column);
//            return Optional.ofNullable(cursor.getString(this.column)).map(String::toUpperCase).orElse(null);
        }
    }

    @Getter
    @RequiredArgsConstructor
    private enum ColumnMetas {
        NAME("Column Name", "COLUMN_NAME"),
        DATA_TYPE("Data Type", "DATA_TYPE"),
        SIZE("Column Size", "COLUMN_SIZE"),
        REMARKS("Remarks", "REMARKS");

        private final String name;
        private final String column;

        public String getString(ResultSet cursor) throws SQLException {
            return cursor.getString(this.column);
//            return Optional.ofNullable(cursor.getString(this.column)).map(String::toUpperCase).orElse(null);
        }

        public int getInt(ResultSet cursor) throws SQLException {
            return cursor.getInt(column);
        }
    }

    @Getter
    @RequiredArgsConstructor
    private enum IndexMetas {
        NAME("Index Name", "INDEX_NAME"),
        COLUMN("Column Name", "COLUMN_NAME"),
        UNIQUE("Unique", "NON_UNIQUE");

        private final String name;
        private final String column;

        public String getString(ResultSet cursor) throws SQLException {
            return cursor.getString(this.column);
//            return Optional.ofNullable(cursor.getString(this.column)).map(String::toUpperCase).orElse(null);
        }

        public boolean getBoolean(ResultSet cursor) throws SQLException {
            boolean result = cursor.getBoolean(this.column);
            if (cursor.wasNull()) {
                return false;
            } else {
                return result;
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Entity Meta
    private final Map<String, EntityMeta> metas = new ConcurrentHashMap<>();

    @Override
    public @Nonnull EntityMeta getMeta(@Nonnull Class<? extends Entity> entity) {
        synchronized (this.lock) {
            var meta = metas.getOrNull(entity.getName());
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
    private void buildEntity(Class<? extends Entity> entity, EntityMeta meta) {
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
    private void buildProperty(EntityMeta entity, PropertyDescriptor property) {
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
        if (meta.isPrimary()) {
            Assertx.mustNull(entity.getId(), () -> new IllegalStateException(Stringx.format("实体[{}]存在多个主键[{}, {}]", entity.getType().getName(), entity.getId().getName(), meta.getName())));
            entity.setId(meta);
        }

        // 判断是否加密
        var encrypted = property.getReadMethod().getAnnotation(Encrypted.class);
        if (encrypted == null) {
            encrypted = field.getAnnotation(Encrypted.class);
        }
        meta.setEncrypted(encrypted != null);

        entity.getProperties().add(meta);
    }

    private void buildForeign(EntityMeta entity, Relation relation) {
        var foreign = new ForeignMeta();
        foreign.setAlias(relation.alias());

        var property = entity.getProperty(Objectx.getOrDefault(relation.property(), entity.getId().getName()));
        Assertx.mustNotNull(property, "创建实体[{}]的关联关系[{}]失败: 在实体[{}]中没有找到关联属性[{}]", entity.getType().getName(), relation.alias(), entity.getType().getName(), Objectx.getOrDefault(relation.property(), entity.getId().getName()));
        foreign.setProperty(property);

        var target = this.getMeta(relation.target());
        foreign.setTarget(target);

        var referencedProperty = target.getProperty(Objectx.getOrDefault(relation.referencedProperty(), target.getId().getName()));
        Assertx.mustNotNull(referencedProperty, "创建实体[{}]的关联关系[{}]失败: 在实体[{}]中没有找到关联属性[{}]", entity.getType().getName(), relation.alias(), target.getType().getName(), Objectx.getOrDefault(relation.referencedProperty(), target.getId().getName()));
        foreign.setReferencedProperty(referencedProperty);

        entity.getForeigns().add(foreign);
    }

    private void buildForeignTable(EntityMeta entity, TableRelation relation) {
        var foreign = new ForeignTableMeta();
        foreign.setAlias(relation.alias());

        var relationTable = this.getMeta(relation.table());
        foreign.setEntity(relationTable);

        var target = this.getMeta(relation.target());
        foreign.setTarget(target);

        var property = entity.getProperty(Objectx.getOrDefault(relation.property(), entity.getId().getName()));
        Assertx.mustNotNull(property, "创建实体[{}]的关联关系[{}]失败: 在实体[{}]中没有找到关联属性[{}]", entity.getType().getName(), relation.alias(), entity.getType().getName(), Objectx.getOrDefault(relation.property(), entity.getId().getName()));
        foreign.setProperty(property);

        var relationProperty = relationTable.getProperty(relation.relationProperty());
        Assertx.mustNotNull(relationProperty, "创建实体[{}]的关联关系[{}]失败: 在关联实体[{}]中没有找到关联属性[{}]", entity.getType().getName(), relation.alias(), relationTable.getType().getName(), relation.relationProperty());
        foreign.setRelationProperty(relationProperty);

        var targetProperty = target.getProperty(Objectx.getOrDefault(relation.targetProperty(), target.getId().getName()));
        Assertx.mustNotNull(targetProperty, "创建实体[{}]的关联关系[{}]失败: 在实体[{}]中没有找到关联属性[{}]", entity.getType().getName(), relation.alias(), target.getType().getName(), Objectx.getOrDefault(relation.targetProperty(), target.getId().getName()));
        foreign.setTargetProperty(targetProperty);

        var targetRelationProperty = relationTable.getProperty(relation.targetRelationProperty());
        Assertx.mustNotNull(targetRelationProperty, "创建实体[{}]的关联关系[{}]失败: 在关联实体[{}]中没有找到关联属性[{}]", entity.getType().getName(), relation.alias(), relationTable.getType().getName(), relation.targetRelationProperty());
        foreign.setTargetRelationProperty(targetRelationProperty);

        entity.getForeignTables().add(foreign);
    }
}
