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

package central.starter.graphql.graphql.mutation;

import central.lang.Stringx;
import central.sql.query.Conditions;
import central.starter.graphql.annotation.GraphQLFetcher;
import central.starter.graphql.annotation.GraphQLSchema;
import central.starter.graphql.graphql.dto.DTO;
import central.starter.graphql.graphql.dto.PersonDTO;
import central.starter.graphql.graphql.entity.PersonEntity;
import central.starter.graphql.graphql.mapper.PersonMapper;
import central.starter.graphql.test.input.PersonInput;
import central.util.Listx;
import central.validation.group.Insert;
import central.validation.group.Update;
import jakarta.annotation.Nonnull;
import jakarta.validation.groups.Default;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Person Mutation
 *
 * @author Alan Yeh
 * @since 2022/09/29
 */
@Component
@GraphQLSchema(path = "person")
public class PersonMutation {

    @Setter(onMethod_ = @Autowired)
    private PersonMapper mapper;

    /**
     * 保存数据
     *
     * @param input    数据输入
     * @param operator 操作人
     */
    @GraphQLFetcher
    public @Nonnull PersonDTO insert(@RequestParam @Validated({Insert.class, Default.class}) PersonInput input,
                                     @RequestParam String operator) {
        var entity = new PersonEntity();
        entity.fromInput(input);
        entity.updateCreator(operator);
        this.mapper.insert(entity);

        return DTO.wrap(entity, PersonDTO.class);
    }

    /**
     * 批量保存数据
     *
     * @param inputs   数据输入
     * @param operator 操作人
     */
    @GraphQLFetcher
    public @Nonnull List<PersonDTO> insertBatch(@RequestParam @Validated({Insert.class, Default.class}) List<PersonInput> inputs,
                                                @RequestParam String operator) {
        return Listx.asStream(inputs).map(it -> this.insert(it, operator)).toList();
    }

    /**
     * 更新数据
     *
     * @param input    数据输入
     * @param operator 操作人
     */
    @GraphQLFetcher
    public @Nonnull PersonDTO update(@RequestParam @Validated({Update.class, Default.class}) PersonInput input,
                                     @RequestParam String operator) {
        var entity = this.mapper.findFirstBy(Conditions.of(PersonEntity.class).eq(PersonEntity::getId, input.getId()));
        if (entity == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("数据[id={}]不存在", input.getId()));
        }

        entity.fromInput(input);
        entity.updateModifier(operator);
        this.mapper.update(entity);

        return DTO.wrap(entity, PersonDTO.class);
    }

    /**
     * 批量更新数据
     *
     * @param inputs   数据输入
     * @param operator 操作人
     */
    @GraphQLFetcher
    public @Nonnull List<PersonDTO> updateBatch(@RequestParam @Validated({Update.class, Default.class}) List<PersonInput> inputs,
                                                @RequestParam String operator) {
        return Listx.asStream(inputs).map(it -> this.update(it, operator)).toList();
    }

    /**
     * 根据主键删除数据
     *
     * @param ids 主键
     */
    @GraphQLFetcher
    public long deleteByIds(@RequestParam List<String> ids) {
        if (Listx.isNullOrEmpty(ids)) {
            return 0;
        }

        return this.mapper.deleteBy(Conditions.of(PersonEntity.class).in(PersonEntity::getId, ids));
    }

    /**
     * 根据条件删除数据
     *
     * @param conditions 条件
     */
    @GraphQLFetcher
    public long deleteBy(@RequestParam Conditions<PersonEntity> conditions) {
        return this.mapper.deleteBy(conditions);
    }
}
