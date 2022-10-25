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

package central.starter.graphql.stub;

import central.bean.Page;
import central.sql.Conditions;
import central.sql.Orders;
import central.sql.data.Entity;
import central.validation.group.Insert;
import central.web.XForwardedHeaders;
import jakarta.validation.groups.Default;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestHeader;

import java.io.Serializable;
import java.util.List;

/**
 * 数据代理
 *
 * @author Alan Yeh
 * @since 2022/09/25
 */
public interface Provider<Data extends Entity, Input extends Serializable> {
    /**
     * 查询数据
     *
     * @param id 主键
     * @return 数据
     */
    Data findById(String id);
    Data findById(String id, @RequestHeader(XForwardedHeaders.TENANT) String tenant);

    /**
     * 查询数据
     *
     * @param ids 主键
     * @return 数据
     */
    List<Data> findByIds(List<String> ids);
    List<Data> findByIds(List<String> ids, @RequestHeader(XForwardedHeaders.TENANT) String tenant);

    /**
     * 查询数据
     *
     * @param limit      数据量（不传的话，就返回所有数据）
     * @param offset     偏移量（跳过前 N 条数据）
     * @param conditions 筛选条件
     * @param orders     排序条件
     * @return 数据列表
     */
    List<Data> findBy(Long limit, Long offset, Conditions<Data> conditions, Orders<Data> orders);
    List<Data> findBy(Long limit, Long offset, Conditions<Data> conditions, Orders<Data> orders, @RequestHeader(XForwardedHeaders.TENANT) String tenant);

    /**
     * 分页查询数据
     *
     * @param pageIndex  分页下标（最小值为 1，最大值为 100）
     * @param pageSize   分页大小（最小值为 1，最大值为 100）
     * @param conditions 筛选条件
     * @param orders     排序条件
     * @return 分页数据
     */
    Page<Data> pageBy(long pageIndex, long pageSize, Conditions<Data> conditions, Orders<Data> orders);
    Page<Data> pageBy(long pageIndex, long pageSize, Conditions<Data> conditions, Orders<Data> orders, @RequestHeader(XForwardedHeaders.TENANT) String tenant);

    /**
     * 查询符合条件的数据数量
     *
     * @param conditions 筛选条件
     * @return 数据数量
     */
    Long countBy(Conditions<Data> conditions);
    Long countBy(Conditions<Data> conditions, @RequestHeader(XForwardedHeaders.TENANT) String tenant);

    /**
     * 保存数据
     *
     * @param input    数据输入
     * @param operator 操作人
     * @return 保存后的实体数据
     */
    Data insert(@Validated({Insert.class, Default.class}) Input input, String operator);
    Data insert(@Validated({Insert.class, Default.class}) Input input, String operator, @RequestHeader(XForwardedHeaders.TENANT) String tenant);

    /**
     * 批量保存数据
     *
     * @param inputs   数据输入
     * @param operator 操作人
     * @return 保存后的实体数据
     */
    List<Data> insertBatch(@Validated({Insert.class, Default.class}) List<Input> inputs, String operator);
    List<Data> insertBatch(@Validated({Insert.class, Default.class}) List<Input> inputs, String operator, @RequestHeader(XForwardedHeaders.TENANT) String tenant);

    /**
     * 删除数据
     *
     * @param ids 主键
     * @return 己删除的数据量
     */
    Long deleteByIds(List<String> ids);
    Long deleteByIds(List<String> ids, @RequestHeader(XForwardedHeaders.TENANT) String tenant);

    /**
     * 删除数据
     *
     * @param conditions 筛选条件
     * @return 己删除的数据量
     */
    Long deleteBy(Conditions<Data> conditions);
    Long deleteBy(Conditions<Data> conditions, @RequestHeader(XForwardedHeaders.TENANT) String tenant);
}
