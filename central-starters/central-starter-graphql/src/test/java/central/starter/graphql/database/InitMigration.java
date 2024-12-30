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

package central.starter.graphql.database;

import central.sql.SqlExecutor;
import central.sql.SqlType;
import central.sql.datasource.migration.Column;
import central.sql.datasource.migration.Database;
import central.sql.datasource.migration.Migration;
import central.sql.datasource.migration.Table;
import central.starter.graphql.database.persistence.entity.PersonEntity;
import central.starter.graphql.database.persistence.entity.PetEntity;
import central.starter.graphql.database.persistence.mapper.PersonMapper;
import central.starter.graphql.database.persistence.mapper.PetMapper;
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
                    Column.of("NAME", SqlType.STRING, 50, "姓名"),
                    Column.of("CREATOR_ID", SqlType.STRING, 36, "创建人主键"),
                    Column.of("CREATE_DATE", SqlType.DATETIME, "创建时间"),
                    Column.of("MODIFIER_ID", SqlType.STRING, 36, "更新人主键"),
                    Column.of("MODIFY_DATE", SqlType.DATETIME, "更新时间")
            );

            var table = Table.of("XT_PERSON", "人员", columns);
            database.addTable(table);
        }

        {
            var columns = List.of(
                    Column.of("ID", true, SqlType.STRING, 32, "主键"),
                    Column.of("MASTER_ID", SqlType.STRING, 32, "主人主键"),
                    Column.of("NAME", SqlType.STRING, 50, "名称"),
                    Column.of("CREATOR_ID", SqlType.STRING, 36, "创建人主键"),
                    Column.of("CREATE_DATE", SqlType.DATETIME, "创建时间"),
                    Column.of("MODIFIER_ID", SqlType.STRING, 36, "更新人主键"),
                    Column.of("MODIFY_DATE", SqlType.DATETIME, "更新时间")
            );

            var table = Table.of("XT_PET", "宠物", columns);
            database.addTable(table);
        }
    }

    @Override
    public void downgrade(Database database) throws SQLException {
        var person = database.getTable("XT_PERSON");
        if (person != null) {
            person.drop();
        }

        var pet = database.getTable("XT_PET");
        if (pet != null) {
            pet.drop();
        }
    }

    @Override
    public void upgrade(SqlExecutor executor) throws SQLException {
        var personMapper = executor.getMapper(PersonMapper.class);
        var petMapper = executor.getMapper(PetMapper.class);

        // Init Person
        var alan = new PersonEntity("alan");
        alan.updateCreator("syssa");

        var yan = new PersonEntity("yan");
        yan.updateCreator("syssa");

        personMapper.insertBatch(List.of(alan, yan));

        // Init Pet
        var beibei = new PetEntity(alan.getId(), "贝贝");
        beibei.updateCreator("syssa");

        var python = new PetEntity(yan.getId(), "派森");
        python.updateCreator("syssa");

        var wangcai = new PetEntity(alan.getId(), "旺财");
        wangcai.updateCreator("syssa");

        petMapper.insertBatch(List.of(beibei, python, wangcai));
    }
}
