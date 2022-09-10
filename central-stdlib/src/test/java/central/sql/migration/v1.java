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

package central.sql.migration;

import central.sql.SqlExecutor;
import central.sql.SqlType;
import central.sql.data.ModifiableEntity;
import central.sql.datasource.migration.*;
import central.sql.proxy.Mapper;
import central.util.Version;
import central.validation.Label;
import central.validation.Validatable;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Alan Yeh
 * @since 2022/09/09
 */
public class v1 extends Migration {
    public v1() {
        super(Version.of("1.0.0"));
    }

    @Data
    @jakarta.persistence.Table(name = "X_TEST_ACCOUNT")
    @EqualsAndHashCode(callSuper = true)
    public static class AccountEntity extends ModifiableEntity implements Validatable {

        @Serial
        private static final long serialVersionUID = -8990226100651038866L;

        @Id
        @Override
        public String getId() {
            return super.getId();
        }

        @NotBlank
        @Label("姓名")
        private String name;

        @NotBlank
        @Label("用户名")
        private String username;

        @Label("年龄")
        private Long age;
    }

    public interface AccountMapper extends Mapper<AccountEntity> {

    }

    @Override
    public void migrate(Migrator migrator) throws SQLException {
        var columns = List.of(
                Column.of("ID", true, SqlType.STRING, 36, "主键"),
                Column.of("USERNAME", SqlType.STRING, 36, "用户名"),
                Column.of("NAME", SqlType.STRING, 50, "姓名"),
                Column.of("AGE", SqlType.INTEGER, "年龄"),
                Column.of("CREATOR_ID", SqlType.STRING, 36, "创建人主键"),
                Column.of("CREATE_DATE", SqlType.DATETIME, "创建时间"),
                Column.of("MODIFIER_ID", SqlType.STRING, 36, "更新人主键"),
                Column.of("MODIFY_DATE", SqlType.DATETIME, "更新时间")
        );

        var indies = List.of(
                Index.of("UNI_USERNAME", true, "USERNAME")
        );

        var table = Table.of("X_TEST_ACCOUNT", "帐户信息", columns, indies);

        migrator.addTable(table);
    }

    @Override
    public void migrate(SqlExecutor executor) throws SQLException {
        var mapper = executor.getMapper(AccountMapper.class);
        var admin = new AccountEntity();
        admin.setId("RRtQTVtnlVpzoVlBq9g");
        admin.setName("系统管理员");
        admin.setUsername("admin");
        admin.setAge(28L);
        admin.updateCreator("syssa");
        mapper.insert(admin);
    }
}
