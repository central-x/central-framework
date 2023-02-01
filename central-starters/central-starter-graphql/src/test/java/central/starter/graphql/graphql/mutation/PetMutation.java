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
import central.starter.graphql.graphql.dto.PetDTO;
import central.starter.graphql.graphql.entity.PetEntity;
import central.starter.graphql.graphql.mapper.PetMapper;
import central.starter.graphql.test.input.PetInput;
import central.web.XForwardedHeaders;
import central.util.Listx;
import central.validation.group.Insert;
import central.validation.group.Update;
import jakarta.validation.groups.Default;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Pet Mutation
 *
 * @author Alan Yeh
 * @since 2022/09/29
 */
@Component
@GraphQLSchema(path = "pet")
public class PetMutation {

    @Setter(onMethod_ = @Autowired)
    private PetMapper mapper;

    /**
     * 保存数据
     *
     * @param input    数据输入
     * @param operator 操作人
     */
    @GraphQLFetcher
    public @Nonnull PetDTO insert(@RequestParam @Validated({Insert.class, Default.class}) PetInput input,
                                  @RequestParam String operator) {
        var entity = new PetEntity();
        entity.fromInput(input);
        entity.updateCreator(operator);
        this.mapper.insert(entity);

        return DTO.wrap(entity, PetDTO.class);
    }

    /**
     * 批量保存数据
     *
     * @param inputs   数据输入
     * @param operator 操作人
     */
    @GraphQLFetcher
    public @Nonnull List<PetDTO> insertBatch(@RequestParam @Validated({Insert.class, Default.class}) List<PetInput> inputs,
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
    public @Nonnull PetDTO update(@RequestParam @Validated({Update.class, Default.class}) PetInput input,
                                  @RequestParam String operator) {
        var entity = this.mapper.findFirstBy(Conditions.of(PetEntity.class).eq(PetEntity::getId, input.getId()));
        if (entity == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("数据[id={}]不存在", input.getId()));
        }

        entity.fromInput(input);
        entity.updateModifier(operator);
        this.mapper.update(entity);

        return DTO.wrap(entity, PetDTO.class);
    }

    /**
     * 批量更新数据
     *
     * @param inputs   数据输入
     * @param operator 操作人
     */
    @GraphQLFetcher
    public @Nonnull List<PetDTO> updateBatch(@RequestParam @Validated({Update.class, Default.class}) List<PetInput> inputs,
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

        return this.mapper.deleteBy(Conditions.of(PetEntity.class).in(PetEntity::getId, ids));
    }

    /**
     * 根据条件删除数据
     *
     * @param conditions 条件
     * @param tenant     租户标识
     */
    @GraphQLFetcher
    public long deleteBy(@RequestParam Conditions<PetEntity> conditions,
                         @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return this.mapper.deleteBy(conditions);
    }
}
