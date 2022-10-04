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

package central.starter.graphql.stub.test.stub;

import central.sql.Conditions;
import central.sql.Orders;
import central.starter.graphql.stub.ProviderStub;
import central.starter.graphql.stub.annotation.BodyPath;
import central.starter.graphql.stub.annotation.GraphQLStub;
import central.starter.graphql.stub.test.data.Group;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Group Repository
 *
 * @author Alan Yeh
 * @since 2022/09/25
 */
@Repository
@GraphQLStub(client = "httpClient")
public interface GroupRepository extends ProviderStub {
    /**
     * 查询数据
     *
     * @param limit      数据量（不传的话，就返回所有数据）
     * @param offset     偏移量（跳过前 N 条数据）
     * @param conditions 筛选条件
     * @param orders     排序条件
     * @return 数据列表
     */
    @BodyPath("groups")
    List<Group> findBy(Long limit, Long offset, Conditions<Group> conditions, Orders<Group> orders);
}
