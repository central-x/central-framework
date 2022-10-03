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
import central.sql.Conditions;
import central.sql.Orders;
import central.starter.graphql.annotation.GraphQLBatchLoader;
import central.starter.graphql.annotation.GraphQLFetcher;
import central.starter.graphql.annotation.GraphQLSchema;
import central.starter.graphql.stub.graphql.dto.DTO;
import central.starter.graphql.stub.graphql.dto.ProjectDTO;
import central.starter.graphql.stub.graphql.mapper.ProjectMapper;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Project Query
 * 项目查询
 *
 * @author Alan Yeh
 * @since 2022/10/04
 */
@Component
@GraphQLSchema(types = ProjectDTO.class)
public class ProjectQuery {
    @Setter(onMethod_ = @Autowired)
    private ProjectMapper mapper;

    /**
     * 批量查询
     *
     * @param ids 主键
     */
    @GraphQLBatchLoader
    public Map<String, ProjectDTO> batchLoader(@RequestParam List<String> ids) {
        return this.mapper.findByIds(ids).stream()
                .map(it -> DTO.wrap(it, ProjectDTO.class))
                .collect(Collectors.toMap(ProjectDTO::getId, it -> it));
    }

    /**
     * 查询数据
     *
     * @param id 主键
     */
    @GraphQLFetcher
    public ProjectDTO findById(@RequestParam String id) {
        var entity = this.mapper.findById(id);
        return DTO.wrap(entity, ProjectDTO.class);
    }

    /**
     * 查询数据
     *
     * @param ids 主键
     */
    @GraphQLFetcher
    public List<ProjectDTO> findByIds(@RequestParam List<String> ids) {
        var entities = this.mapper.findByIds(ids);
        return DTO.wrap(entities, ProjectDTO.class);
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
    public List<ProjectDTO> findBy(@RequestParam(required = false) Long limit,
                                   @RequestParam(required = false) Long offset,
                                   @RequestParam Conditions conditions,
                                   @RequestParam Orders orders) {
        // 懒得去过滤了
        var entities = this.mapper.findBy(limit, offset, conditions, orders);
        return DTO.wrap(entities, ProjectDTO.class);
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
    public Page<ProjectDTO> pageBy(@RequestParam long pageIndex,
                                   @RequestParam long pageSize,
                                   @RequestParam Conditions conditions,
                                   @RequestParam Orders orders) {
        var page = this.mapper.findPageBy(pageIndex, pageSize, conditions, orders);
        return DTO.wrap(page, ProjectDTO.class);
    }

    /**
     * 查询符合条件的数据数量
     *
     * @param conditions 筛选条件
     */
    @GraphQLFetcher
    public Long countBy(@RequestParam Conditions conditions) {
        return this.mapper.countBy(conditions);
    }
}
