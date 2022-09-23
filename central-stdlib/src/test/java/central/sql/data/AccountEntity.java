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

package central.sql.data;

import central.bean.Available;
import central.sql.SqlType;
import central.sql.meta.annotation.Relation;
import central.sql.meta.annotation.TableRelation;
import central.validation.Label;
import central.validation.Validatable;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.math.BigDecimal;

/**
 * 帐户
 *
 * @author Alan Yeh
 * @since 2022/08/02
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "XT_ACCOUNT")
@Relation(target = DeptEntity.class, alias = "dept", property = "deptId")
@TableRelation(target = RoleEntity.class, table = RoleAccountEntity.class, alias = "role", relationProperty = "accountId", targetRelationProperty = "roleId")
@EqualsAndHashCode(callSuper = true)
public class AccountEntity extends ModifiableEntity implements Available, Validatable {

    @Serial
    private static final long serialVersionUID = -8990226100651038866L;

    @Id
    @Override
    public String getId() {
        return super.getId();
    }

    /**
     * @see SqlType#STRING
     */
    @NotBlank
    @Label("用户名")
    private String username;

    /**
     * @see SqlType#STRING
     */
    @NotBlank
    @Label("姓名")
    private String name;

    /**
     * @see SqlType#INTEGER
     */
    @Label("年龄")
    private Integer age;

    /**
     * @see SqlType#STRING
     */
    @Label("部门主键")
    private String deptId;

    /**
     * @see SqlType#BOOLEAN
     */
    @Label("启用/禁用")
    private Boolean enabled;

    /**
     * @see SqlType#BLOB
     */
    @Label("头像")
    private byte[] avatar;

    /**
     * @see SqlType#BIG_DECIMAL
     */
    @Label("工资")
    private BigDecimal salary;

    /**
     * @see SqlType#LONG
     */
    @Label("入职时间")
    private Long hiredate;
}
