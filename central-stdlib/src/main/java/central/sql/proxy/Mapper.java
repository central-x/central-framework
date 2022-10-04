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

package central.sql.proxy;

import central.sql.data.Entity;
import central.bean.Page;
import central.sql.Conditions;
import central.sql.Orders;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * 通用 Mapper
 *
 * @author Alan Yeh
 * @since 2022/07/18
 */
public interface Mapper<E extends Entity> {
    /**
     * 销毁 ORM 实例
     * 注意，本操作会导致整个 ORM 需要重新初始化
     */
    void destroy();

    /**
     * 创建其它 Mapper
     *
     * @param mapper Mapper 类型
     * @return Mapper 实例
     */
    <T extends Mapper<?>> T getMapper(Class<T> mapper);

    /**
     * 插入单条数据
     * {@code INSERT INTO TABLE(COL1, COL2, COL3, ...) VALUES (?, ?, ?, ...)}
     *
     * @param entity 数据实体
     * @return 是否插入成功
     */
    boolean insert(@Nonnull E entity);

    /**
     * 批量插入数据
     * {@code INSERT INTO TABLE(COL1, COL2, COL3, ...) VALUES (?, ?, ?, ...)}
     *
     * @param entities 数据实体
     * @return 已插入的数据数量
     */
    long insertBatch(@Nonnull List<E> entities);

    /**
     * 根据主键批量删除数据
     * {@code DELETE FROM TABLE WHERE ID IN (?, ?, ?, ...)}
     *
     * @param ids 数据主键
     * @return 受影响数据量
     */
    long deleteByIds(List<String> ids);

    /**
     * 根据主键删除数据
     * {@code DELETE FROM TABLE WHERE ID = ?}
     *
     * @param id 数据主键
     * @return 受影响数据量
     */
    long deleteById(@Nonnull String id);

    /**
     * 根据条件删除数据
     *
     * @param conditions 参数条件，如果条件为空，将清空表
     * @return 受影响的数据量
     */
    long deleteBy(@Nonnull Conditions<E> conditions);

    /**
     * 清空表
     *
     * @return 受影响的数据量
     */
    long deleteAll();

    /**
     * 更新数据
     *
     * @param entity 待更新数据实体(主键不能为空)
     * @return 是否更新成功
     */
    boolean update(@Nonnull E entity);

    /**
     * 仅更新字段不为 null 的信息
     *
     * @param entity 待更新数据实体(主键不能为空)
     * @return 是否更新成功
     */
    boolean updateBy(@Nonnull E entity);

    /**
     * 根据条件更新数据
     *
     * @param entity     待更新数据实体（主键为空）
     * @param conditions 条件
     * @return 受影响数据量
     */
    long updateBy(@Nonnull E entity, @Nullable Conditions<E> conditions);

    /**
     * 根据主键查询
     * 如果没有找到，则返回 null
     * {@code SELECT * FROM TABLE WHERE ID = ?}
     *
     * @param id 主键
     * @return 数据实体
     */
    @Nullable
    E findById(@Nullable String id);

    /**
     * 根据主键查询
     * {@code SELECT * FROM TABLE WHERE ID IN (?, ?, ?, ...)}
     *
     * @param ids 主键列表
     * @return 数据实体列表
     */
    @Nonnull
    List<E> findByIds(@Nullable List<String> ids);

    /**
     * 根据条件获取第一条数据
     *
     * @param conditions 查询条件
     * @param orders     排序
     * @return 实体数据
     */
    @Nullable
    E findFirstBy(@Nonnull Conditions<E> conditions, @Nullable Orders<E> orders);

    @Nullable
    E findFirstBy(@Nonnull Conditions<E> conditions);

    /**
     * 根据条件获取实体集合
     *
     * @param first      取前几条数据，如果不为空，则取所有值
     * @param offset     偏移量。如果为空，则不偏移。此值在 first 不为空时才生效
     * @param conditions 过滤条件
     * @param orders     排序
     * @return 实体列表
     */
    @Nonnull
    List<E> findBy(Long first, Long offset, @Nonnull Conditions<E> conditions, @Nullable Orders<E> orders);

    /**
     * 根据条件获取实体集合
     *
     * @param conditions 过滤条件
     * @param orders     排序
     * @return 实体列表
     */
    @Nonnull
    List<E> findBy(@Nonnull Conditions<E> conditions, @Nullable Orders<E> orders);

    /**
     * 根据条件获取实体集合
     *
     * @param conditions 过滤条件
     * @return 实体列表
     */
    @Nonnull
    List<E> findBy(@Nonnull Conditions<E> conditions);

    /**
     * 获取所有数据
     *
     * @param orders 排序
     * @return 实体列表
     */
    @Nonnull
    List<E> findAll(@Nullable Orders<E> orders);

    /**
     * 获取所有数据
     *
     * @return 实体列表
     */
    @Nonnull
    List<E> findAll();

    /**
     * 根据条件分页查询数据
     *
     * @param pageIndex  分页起始（从 1 开始）
     * @param pageSize   分页大小
     * @param conditions 条件参数
     * @param orders     排序
     * @return 分页结果
     */
    @Nonnull
    Page<E> findPageBy(@Nonnull Long pageIndex, @Nonnull Long pageSize, @Nonnull Conditions<E> conditions, @Nullable Orders<E> orders);

    /**
     * 根据条件分页查询数据
     *
     * @param pageIndex  分页起始（从 1 开始）
     * @param pageSize   分页大小
     * @param conditions 条件参数
     * @return 分页结果
     */
    @Nonnull
    Page<E> findPageBy(@Nonnull Long pageIndex, @Nonnull Long pageSize, @Nonnull Conditions<E> conditions);

    /**
     * 询查表的数据量
     * {@code SELECT COUNT(1) FROM TABLE WHERE 1 = 1}
     *
     * @return 数量
     */
    long count();

    /**
     * 根据条件查询数量
     *
     * @param conditions 条件参数
     * @return 数量
     */
    long countBy(@Nullable Conditions<E> conditions);

    /**
     * 根据条件查询数据是否存在
     *
     * @param conditions 条件参数
     * @return 是否存在满足条件的数据
     */
    boolean existsBy(@Nullable Conditions<E> conditions);
}
