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

package central.starter.graphql.stub.graphql.mutation;

import central.sql.query.Conditions;
import central.starter.graphql.annotation.GraphQLFetcher;
import central.starter.graphql.annotation.GraphQLSchema;
import central.starter.graphql.stub.database.persistence.GroupPersistence;
import central.starter.graphql.stub.database.persistence.entity.GroupEntity;
import central.starter.graphql.stub.graphql.dto.DTO;
import central.starter.graphql.stub.graphql.dto.GroupDTO;
import central.starter.graphql.stub.test.input.GroupInput;
import central.validation.group.Insert;
import central.validation.group.Update;
import jakarta.annotation.Nonnull;
import jakarta.validation.groups.Default;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Group Mutation
 * <p>
 * 项目组修改
 *
 * @author Alan Yeh
 * @since 2022/10/04
 */
@Component
@GraphQLSchema(path = "mutation", types = GroupDTO.class)
public class GroupMutation {

    @Setter(onMethod_ = @Autowired)
    private GroupPersistence persistence;

    /**
     * 保存数据
     *
     * @param input    数据输入
     * @param operator 操作人
     */
    @GraphQLFetcher
    public @Nonnull GroupDTO insert(@RequestParam @Validated({Insert.class, Default.class}) GroupInput input,
                                    @RequestParam String operator) {
        var data = this.persistence.insert(input, operator);
        return DTO.wrap(data, GroupDTO.class);
    }

    /**
     * 批量保存数据
     *
     * @param inputs   数据输入
     * @param operator 操作人
     */
    @GraphQLFetcher
    public @Nonnull List<GroupDTO> insertBatch(@RequestParam @Validated({Insert.class, Default.class}) List<GroupInput> inputs,
                                               @RequestParam String operator) {
        var data = this.persistence.insertBatch(inputs, operator);
        return DTO.wrap(data, GroupDTO.class);
    }

    /**
     * 更新数据
     *
     * @param input    数据输入
     * @param operator 操作人
     */
    @GraphQLFetcher
    public @Nonnull GroupDTO update(@RequestParam @Validated({Update.class, Default.class}) GroupInput input,
                                    @RequestParam String operator) {
        var data = this.persistence.update(input, operator);
        return DTO.wrap(data, GroupDTO.class);
    }

    /**
     * 批量更新数据
     *
     * @param inputs   数据输入
     * @param operator 操作人
     */
    @GraphQLFetcher
    public @Nonnull List<GroupDTO> updateBatch(@RequestParam @Validated({Update.class, Default.class}) List<GroupInput> inputs,
                                               @RequestParam String operator) {
        var data = this.persistence.updateBatch(inputs, operator);
        return DTO.wrap(data, GroupDTO.class);
    }

    /**
     * 根据主键删除数据
     *
     * @param ids 主键
     */
    @GraphQLFetcher
    public long deleteByIds(@RequestParam List<String> ids) {
        return this.persistence.deleteByIds(ids);
    }

    /**
     * 根据条件删除数据
     *
     * @param conditions 条件
     */
    @GraphQLFetcher
    public long deleteBy(@RequestParam Conditions<GroupEntity> conditions) {
        return this.persistence.deleteBy(conditions);
    }
}
