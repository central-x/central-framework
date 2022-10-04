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

package central.starter.graphql.graphql.dto;

import central.sql.Conditions;
import central.starter.graphql.annotation.GraphQLGetter;
import central.starter.graphql.annotation.GraphQLType;
import central.starter.graphql.graphql.entity.PersonEntity;
import central.starter.graphql.graphql.entity.PetEntity;
import central.starter.graphql.graphql.query.PetQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serial;
import java.util.List;

/**
 * Person
 *
 * @author Alan Yeh
 * @since 2022/09/09
 */
@Data
@GraphQLType("Person")
@EqualsAndHashCode(callSuper = true)
public class PersonDTO extends PersonEntity implements DTO {
    @Serial
    private static final long serialVersionUID = 5206987080662110335L;

    @GraphQLGetter
    public List<PetDTO> getPets(@Autowired PetQuery query) {
        return query.findBy(null, null, Conditions.of(PetEntity.class).eq(PetEntity::getMasterId, this.getId()), null);
    }
}
