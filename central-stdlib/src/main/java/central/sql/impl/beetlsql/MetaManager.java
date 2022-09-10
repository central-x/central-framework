///*
// * MIT License
// *
// * Copyright (c) 2022-present Alan Yeh <alan@yeh.cn>
// *
// * Permission is hereby granted, free of charge, to any person obtaining a copy
// * of this software and associated documentation files (the "Software"), to deal
// * in the Software without restriction, including without limitation the rights
// * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// * copies of the Software, and to permit persons to whom the Software is
// * furnished to do so, subject to the following conditions:
// *
// * The above copyright notice and this permission notice shall be included in all
// * copies or substantial portions of the Software.
// *
// * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// * SOFTWARE.
// */
//
//package central.sql.impl.beetlsql;
//
//import central.sql.SqlExecutor;
//import central.sql.data.Entity;
//import central.sql.meta.entity.EntityMeta;
//import jakarta.persistence.Table;
//import lombok.SneakyThrows;
//
//import java.sql.SQLException;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
///**
// * 元数据管理
// *
// * @author Alan Yeh
// * @since 2022/08/05
// */
//public class MetaManager {
//
//    private final Map<String, EntityMeta> metas = new ConcurrentHashMap<>();
//
//    /**
//     * 判断是否存在指定的元数据
//     */
//    public boolean contains(Class<? extends Entity> entity) {
//        return this.metas.containsKey(entity.getName());
//    }
//
//    /**
//     * 获取实体元数据
//     * 如果没有缓存，则重新构建一个新的
//     *
//     * @param executor 执行器
//     * @param entity   实体
//     * @return 实体元数据
//     */
//    public EntityMeta getEntity(SqlExecutor executor, Class<? extends Entity> entity) {
//        synchronized (MetaManager.class) {
//            var meta = metas.get(entity.getName());
//            if (meta == null) {
//                meta = new EntityMeta();
//                // 提前放到 Map 里，不然会出现死锁
//                metas.put(entity.getName(), meta);
//                try {
//                    buildEntity(executor, entity, meta);
//                } catch (Exception ex) {
//                    metas.remove(entity.getName());
//                    throw ex;
//                }
//            }
//            return meta;
//        }
//    }
//
//    /**
//     * 构建实体的元数据
//     *
//     * @param executor Sql 执行器
//     * @param entity   实体
//     * @param meta     实体元数据
//     */
//    @SneakyThrows(SQLException.class)
//    private void buildEntity(SqlExecutor executor, Class<? extends Entity> entity, EntityMeta meta) {
//        meta.setType(entity);
//
//        var table = entity.getAnnotation(Table.class);
//        if (table != null){
//            meta.setTable(table.name());
//        } else {
//            meta.setTable(executor.getConversion().getTableName(entity));
//        }
//
//        try (var connection = executor.getDataSource().getConnection()){
//            var metadata = connection.getMetaData();
//            var resultSet = metadata.getPrimaryKeys()
//        }
//    }
//}
