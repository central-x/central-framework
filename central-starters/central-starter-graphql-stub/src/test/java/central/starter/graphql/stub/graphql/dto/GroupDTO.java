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

package central.starter.graphql.stub.graphql.dto;

import central.sql.query.Conditions;
import central.sql.query.Orders;
import central.starter.graphql.annotation.GraphQLGetter;
import central.starter.graphql.annotation.GraphQLType;
import central.starter.graphql.stub.graphql.entity.GroupEntity;
import central.starter.graphql.stub.graphql.entity.ProjectEntity;
import central.starter.graphql.stub.graphql.query.ProjectQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serial;
import java.util.List;

/**
 * Group
 * 项目组
 *
 * @author Alan Yeh
 * @since 2022/10/04
 */
@Data
@GraphQLType("Group")
@EqualsAndHashCode(callSuper = true)
public class GroupDTO extends GroupEntity implements DTO {
    @Serial
    private static final long serialVersionUID = -653171415398883456L;

    @GraphQLGetter
    public List<ProjectDTO> getProjects(@Autowired ProjectQuery query) {
        return query.findBy(null, null, Conditions.of(ProjectEntity.class).eq(ProjectEntity::getGroupId, this.getId()), Orders.of(ProjectEntity.class).desc(ProjectEntity::getName));
    }
}
