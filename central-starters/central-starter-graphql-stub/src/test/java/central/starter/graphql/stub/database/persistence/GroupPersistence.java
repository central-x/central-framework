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

package central.starter.graphql.stub.database.persistence;

import central.bean.Page;
import central.lang.Stringx;
import central.sql.query.Columns;
import central.sql.query.Conditions;
import central.sql.query.Orders;
import central.starter.graphql.stub.database.persistence.entity.GroupEntity;
import central.starter.graphql.stub.database.persistence.mapper.GroupMapper;
import central.starter.graphql.stub.test.input.GroupInput;
import central.util.Listx;
import central.validation.group.Insert;
import central.validation.group.Update;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.validation.groups.Default;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

/**
 * Group Persistence
 * 
 * @author Alan Yeh
 * @since 2024/12/30
 */
@Component
public class GroupPersistence {

    @Setter(onMethod_ = @Autowired)
    private GroupMapper mapper;

    /**
     * 根据主键查询数据
     *
     * @param id 主键
     */
    public @Nullable GroupEntity findById(@Nullable String id) {
        if (Stringx.isNullOrBlank(id)) {
            return null;
        }
        return this.mapper.findById(id);
    }

    /**
     * 查询数据
     *
     * @param ids 主键
     */
    public @Nonnull List<GroupEntity> findByIds(@Nullable List<String> ids) {
        if (Listx.isNullOrEmpty(ids)) {
            return new ArrayList<>();
        }
        return this.mapper.findByIds(ids);
    }

    /**
     * 查询数据
     *
     * @param limit      获取前 N 条数据
     * @param offset     偏移量
     * @param conditions 过滤条件
     * @param orders     排序条件
     */
    public @Nonnull List<GroupEntity> findBy(@Nullable Long limit,
                                              @Nullable Long offset,
                                              @Nullable Columns<? extends GroupEntity> columns,
                                              @Nullable Conditions<? extends GroupEntity> conditions,
                                              @Nullable Orders<? extends GroupEntity> orders) {
        return this.mapper.findBy(limit, offset, columns, conditions, orders);
    }

    /**
     * 分页查询数据
     *
     * @param pageIndex  分页下标
     * @param pageSize   分页大小
     * @param columns    字段列表
     * @param conditions 过滤条件
     * @param orders     排序条件
     */
    public @Nonnull Page<GroupEntity> pageBy(@Nonnull Long pageIndex,
                                              @Nonnull Long pageSize,
                                              @Nullable Columns<? extends GroupEntity> columns,
                                              @Nullable Conditions<? extends GroupEntity> conditions,
                                              @Nullable Orders<? extends GroupEntity> orders) {
        return this.mapper.findPageBy(pageIndex, pageSize, columns, conditions, orders);
    }

    /**
     * 查询符合条件的数据数量
     *
     * @param conditions 筛选条件
     */
    public long countBy(@Nullable Conditions<? extends GroupEntity> conditions) {
        return this.mapper.countBy(conditions);
    }


    /**
     * 保存数据
     *
     * @param input    数据输入
     * @param operator 操作帐号
     * @return 保存后的数据
     */
    public GroupEntity insert(@Validated({Insert.class, Default.class}) GroupInput input, @Nonnull String operator) {
        var entity = new GroupEntity();
        entity.fromInput(input);
        entity.updateCreator(operator);
        this.mapper.insert(entity);

        return entity;
    }

    /**
     * 批量保存数据
     *
     * @param inputs   数据输入
     * @param operator 操作人
     */
    public List<GroupEntity> insertBatch(@Validated({Insert.class, Default.class}) List<GroupInput> inputs, @Nonnull String operator) {
        return Listx.asStream(inputs).map(it -> this.insert(it, operator)).toList();
    }

    /**
     * 更新数据
     *
     * @param input    数据输入
     * @param operator 操作人
     */
    public GroupEntity update(@Validated({Update.class, Default.class}) GroupInput input, @Nonnull String operator) {
        var entity = this.mapper.findById(input.getId());
        if (entity == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("数据[id={}]不存在", input.getId()));
        }

        entity.fromInput(input);
        entity.updateModifier(operator);
        this.mapper.update(entity);

        return entity;
    }

    /**
     * 批量更新数据
     *
     * @param inputs   数据输入
     * @param operator 操作人
     */
    public List<GroupEntity> updateBatch(@Validated({Update.class, Default.class}) List<GroupInput> inputs, @Nonnull String operator) {
        return Listx.asStream(inputs).map(it -> this.update(it, operator)).toList();
    }

    /**
     * 根据主键删除数据
     *
     * @param ids 主键
     */
    public long deleteByIds(@Nullable List<String> ids) {
        if (Listx.isNullOrEmpty(ids)) {
            return 0;
        }

        return this.mapper.deleteByIds(ids);
    }

    /**
     * 根据条件删除数据
     *
     * @param conditions 条件
     */
    public long deleteBy(@Nullable Conditions<? extends GroupEntity> conditions) {
        return this.mapper.deleteBy(conditions);
    }
}
