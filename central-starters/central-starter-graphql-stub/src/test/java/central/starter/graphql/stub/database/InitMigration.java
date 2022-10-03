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

package central.starter.graphql.stub.database;

import central.sql.SqlExecutor;
import central.sql.SqlType;
import central.sql.datasource.migration.Column;
import central.sql.datasource.migration.Database;
import central.sql.datasource.migration.Migration;
import central.sql.datasource.migration.Table;
import central.starter.graphql.stub.graphql.entity.GroupEntity;
import central.starter.graphql.stub.graphql.entity.ProjectEntity;
import central.starter.graphql.stub.graphql.mapper.GroupMapper;
import central.starter.graphql.stub.graphql.mapper.ProjectMapper;
import central.util.Version;

import java.sql.SQLException;
import java.util.List;

/**
 * 初始化数据结构
 *
 * @author Alan Yeh
 * @since 2022/09/28
 */
public class InitMigration extends Migration {
    public InitMigration() {
        super(Version.of("1.0.0"));
    }

    @Override
    public void upgrade(Database database) throws SQLException {
        {
            var columns = List.of(
                    Column.of("ID", true, SqlType.STRING, 32, "主键"),
                    Column.of("NAME", SqlType.STRING, 50, "名称"),
                    Column.of("CREATOR_ID", SqlType.STRING, 36, "创建人主键"),
                    Column.of("CREATE_DATE", SqlType.DATETIME, "创建时间"),
                    Column.of("MODIFIER_ID", SqlType.STRING, 36, "更新人主键"),
                    Column.of("MODIFY_DATE", SqlType.DATETIME, "更新时间")
            );

            var table = Table.of("XT_GROUP", "项目组", columns);
            database.addTable(table);
        }

        {
            var columns = List.of(
                    Column.of("ID", true, SqlType.STRING, 32, "主键"),
                    Column.of("GROUP_ID", SqlType.STRING, 32, "项目组主键"),
                    Column.of("NAME", SqlType.STRING, 50, "名称"),
                    Column.of("CREATOR_ID", SqlType.STRING, 36, "创建人主键"),
                    Column.of("CREATE_DATE", SqlType.DATETIME, "创建时间"),
                    Column.of("MODIFIER_ID", SqlType.STRING, 36, "更新人主键"),
                    Column.of("MODIFY_DATE", SqlType.DATETIME, "更新时间")
            );

            var table = Table.of("XT_PROJECT", "项目", columns);
            database.addTable(table);
        }
    }

    @Override
    public void downgrade(Database database) throws SQLException {
        var group = database.getTable("XT_GROUP");
        if (group != null) {
            group.drop();
        }

        var project = database.getTable("XT_PROJECT");
        if (project != null) {
            project.drop();
        }
    }

    @Override
    public void upgrade(SqlExecutor executor) throws SQLException {
        var groupMapper = executor.getMapper(GroupMapper.class);
        var projectMapper = executor.getMapper(ProjectMapper.class);

        // Init Groups
        var centralx = new GroupEntity("centralx");
        centralx.updateCreator("syssa");

        var spring = new GroupEntity("spring");
        spring.updateCreator("syssa");

        groupMapper.insertBatch(List.of(centralx, spring));

        // Init Projects
        var central_framework = ProjectEntity.builder().groupId(centralx.getId()).name("central-framework").build();
        central_framework.updateCreator("syssa");

        var central_studio = ProjectEntity.builder().groupId(centralx.getId()).name("central-studio").build();
        central_studio.updateCreator("syssa");

        var spring_framework = ProjectEntity.builder().groupId(spring.getId()).name("spring-framework").build();
        spring_framework.updateCreator("syssa");

        var spring_boot = ProjectEntity.builder().groupId(spring.getId()).name("spring-boot").build();
        spring_boot.updateCreator("syssa");

        var spring_cloud = ProjectEntity.builder().groupId(spring.getId()).name("spring-cloud").build();
        spring_cloud.updateCreator("syssa");

        projectMapper.insertBatch(List.of(central_framework, central_studio, spring_framework, spring_boot, spring_cloud));
    }
}
