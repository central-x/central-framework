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

package central.starter.graphql.graphql.query;

import central.bean.Page;
import central.sql.Conditions;
import central.sql.Orders;
import central.starter.graphql.annotation.GraphQLBatchLoader;
import central.starter.graphql.annotation.GraphQLFetcher;
import central.starter.graphql.annotation.GraphQLSchema;
import central.starter.graphql.graphql.dto.DTO;
import central.starter.graphql.graphql.dto.PersonDTO;
import central.starter.graphql.graphql.mapper.PersonMapper;
import graphql.schema.DataFetchingEnvironment;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Person Query
 *
 * @author Alan Yeh
 * @since 2022/09/29
 */
@Component
@GraphQLSchema(path = "person", types = PersonDTO.class)
public class PersonQuery {

    @Setter(onMethod_ = @Autowired)
    private PersonMapper mapper;

    /**
     * 批量查询
     *
     * @param ids 主键
     */
    @GraphQLBatchLoader
    public Map<String, PersonDTO> batchLoader(@RequestParam List<String> ids) {
        return this.mapper.findByIds(ids).stream()
                .map(it -> DTO.wrap(it, PersonDTO.class))
                .collect(Collectors.toMap(PersonDTO::getId, it -> it));
    }

    /**
     * 查询数据
     *
     * @param id 主键
     */
    @GraphQLFetcher
    public PersonDTO findById(@RequestParam String id, DataFetchingEnvironment environment) {
        var entity = this.mapper.findById(id);
        return DTO.wrap(entity, PersonDTO.class);
    }

    /**
     * 查询数据
     *
     * @param ids 主键
     */
    @GraphQLFetcher
    public List<PersonDTO> findByIds(@RequestParam List<String> ids, DataFetchingEnvironment environment) {
        var entities = this.mapper.findByIds(ids);
        return DTO.wrap(entities, PersonDTO.class);
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
    public List<PersonDTO> findBy(@RequestParam(required = false) Long limit,
                                  @RequestParam(required = false) Long offset,
                                  @RequestParam Conditions conditions,
                                  @RequestParam Orders orders,
                                  @Autowired PetQuery petQuery,
                                  @Autowired ApplicationContext context,
                                  @Autowired Environment environment,
                                  DataFetchingEnvironment loaderEnvironment,
                                  HttpServletRequest request,
                                  ServletRequest servletRequest,
                                  HttpServletResponse response,
                                  ServletResponse servletResponse) {
        // 懒得去过滤了
        var entities = this.mapper.findBy(limit, offset, conditions, orders);
        return DTO.wrap(entities, PersonDTO.class);
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
    public Page<PersonDTO> pageBy(@RequestParam long pageIndex,
                                  @RequestParam long pageSize,
                                  @RequestParam Conditions conditions,
                                  @RequestParam Orders orders) {
        var page = this.mapper.findPageBy(pageIndex, pageSize, conditions, orders);
        return DTO.wrap(page, PersonDTO.class);
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
