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

package central.starter.graphql.stub.graphql.query;

import central.bean.Page;
import central.sql.query.Conditions;
import central.sql.query.Orders;
import central.starter.graphql.annotation.GraphQLBatchLoader;
import central.starter.graphql.annotation.GraphQLFetcher;
import central.starter.graphql.annotation.GraphQLSchema;
import central.starter.graphql.stub.graphql.dto.DTO;
import central.starter.graphql.stub.graphql.dto.GroupDTO;
import central.starter.graphql.stub.graphql.entity.GroupEntity;
import central.starter.graphql.stub.graphql.mapper.GroupMapper;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Group Query
 * 项目组查询
 *
 * @author Alan Yeh
 * @since 2022/10/04
 */
@Component
@GraphQLSchema(path = "query", types = GroupDTO.class)
public class GroupQuery {

    @Setter(onMethod_ = @Autowired)
    private GroupMapper mapper;

    /**
     * 批量数据加载器
     *
     * @param ids 主键
     */
    @GraphQLBatchLoader
    public @Nonnull Map<String, GroupDTO> batchLoader(@RequestParam List<String> ids) {
        return this.mapper.findBy(Conditions.of(GroupEntity.class).in(GroupEntity::getId, ids))
                .stream()
                .map(it -> DTO.wrap(it, GroupDTO.class))
                .collect(Collectors.toMap(GroupDTO::getId, it -> it));
    }

    /**
     * 根据主键查询数据
     *
     * @param id 主键
     */
    @GraphQLFetcher
    public @Nullable GroupDTO findById(@RequestParam String id) {
        var entity = this.mapper.findFirstBy(Conditions.of(GroupEntity.class).eq(GroupEntity::getId, id));
        return DTO.wrap(entity, GroupDTO.class);
    }


    /**
     * 查询数据
     *
     * @param ids 主键
     */
    @GraphQLFetcher
    public @Nonnull List<GroupDTO> findByIds(@RequestParam List<String> ids) {
        var entities = this.mapper.findBy(Conditions.of(GroupEntity.class).in(GroupEntity::getId, ids));

        return DTO.wrap(entities, GroupDTO.class);
    }

    /**
     * 查询数据
     *
     * @param limit      获取前 N 条数据
     * @param offset     偏移量
     * @param conditions 过滤条件
     * @param orders     排序条件
     */
    @GraphQLFetcher
    public @Nonnull List<GroupDTO> findBy(@RequestParam(required = false) Long limit,
                                           @RequestParam(required = false) Long offset,
                                           @RequestParam Conditions<GroupEntity> conditions,
                                           @RequestParam Orders<GroupEntity> orders) {
        var list = this.mapper.findBy(limit, offset, conditions, orders);
        return DTO.wrap(list, GroupDTO.class);
    }

    /**
     * 分页查询数据
     *
     * @param pageIndex  分页下标
     * @param pageSize   分页大小
     * @param conditions 过滤条件
     * @param orders     排序条件
     */
    @GraphQLFetcher
    public @Nonnull Page<GroupDTO> pageBy(@RequestParam long pageIndex,
                                           @RequestParam long pageSize,
                                           @RequestParam Conditions<GroupEntity> conditions,
                                           @RequestParam Orders<GroupEntity> orders) {
        var page = this.mapper.findPageBy(pageIndex, pageSize, conditions, orders);
        return DTO.wrap(page, GroupDTO.class);
    }

    /**
     * 查询符合条件的数据数量
     *
     * @param conditions 筛选条件
     */
    @GraphQLFetcher
    public Long countBy(@RequestParam Conditions<GroupEntity> conditions) {
        return this.mapper.countBy(conditions);
    }
}
