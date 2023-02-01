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

import central.lang.Stringx;
import central.sql.query.Conditions;
import central.starter.graphql.annotation.GraphQLFetcher;
import central.starter.graphql.annotation.GraphQLSchema;
import central.starter.graphql.stub.graphql.dto.DTO;
import central.starter.graphql.stub.graphql.dto.ProjectDTO;
import central.starter.graphql.stub.graphql.entity.ProjectEntity;
import central.starter.graphql.stub.graphql.mapper.ProjectMapper;
import central.starter.graphql.stub.test.input.ProjectInput;
import central.util.Listx;
import central.validation.group.Insert;
import central.validation.group.Update;
import jakarta.validation.groups.Default;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Project Mutation
 * 项目修改
 *
 * @author Alan Yeh
 * @since 2022/10/04
 */
@Component
@GraphQLSchema(path = "mutation", types = ProjectDTO.class)
public class ProjectMutation {
    @Setter(onMethod_ = @Autowired)
    private ProjectMapper mapper;

    /**
     * 保存数据
     *
     * @param input    数据输入
     * @param operator 操作人
     */
    @GraphQLFetcher
    public @Nonnull ProjectDTO insert(@RequestParam @Validated({Insert.class, Default.class}) ProjectInput input,
                                      @RequestParam String operator) {
        var entity = new ProjectEntity();
        entity.fromInput(input);
        entity.updateCreator(operator);
        this.mapper.insert(entity);

        return DTO.wrap(entity, ProjectDTO.class);
    }

    /**
     * 批量保存数据
     *
     * @param inputs   数据输入
     * @param operator 操作人
     */
    @GraphQLFetcher
    public @Nonnull List<ProjectDTO> insertBatch(@RequestParam @Validated({Insert.class, Default.class}) List<ProjectInput> inputs,
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
    public @Nonnull ProjectDTO update(@RequestParam @Validated({Update.class, Default.class}) ProjectInput input,
                                      @RequestParam String operator) {
        var entity = this.mapper.findFirstBy(Conditions.of(ProjectEntity.class).eq(ProjectEntity::getId, input.getId()));
        if (entity == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("数据[id={}]不存在", input.getId()));
        }

        entity.fromInput(input);
        entity.updateModifier(operator);
        this.mapper.update(entity);

        return DTO.wrap(entity, ProjectDTO.class);
    }

    /**
     * 批量更新数据
     *
     * @param inputs   数据输入
     * @param operator 操作人
     */
    @GraphQLFetcher
    public @Nonnull List<ProjectDTO> updateBatch(@RequestParam @Validated({Update.class, Default.class}) List<ProjectInput> inputs,
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

        return this.mapper.deleteBy(Conditions.of(ProjectEntity.class).in(ProjectEntity::getId, ids));
    }

    /**
     * 根据条件删除数据
     *
     * @param conditions 条件
     */
    @GraphQLFetcher
    public long deleteBy(@RequestParam Conditions<ProjectEntity> conditions) {
        return this.mapper.deleteBy(conditions);
    }
}
