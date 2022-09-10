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

package central.sql.datasource.migration;

import central.lang.CompareResultEnum;
import central.sql.Conditions;
import central.sql.SqlExecutor;
import central.sql.SqlType;
import central.sql.datasource.migration.data.MigrationEntity;
import central.sql.datasource.migration.data.MigrationMapper;
import central.sql.datasource.migration.migrator.DatabaseMigrator;
import central.sql.meta.DatabaseMetaBuilder;
import central.sql.meta.database.DatabaseMeta;
import central.util.Version;
import lombok.Getter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 数据源迁移（版本管理）
 *
 * @author Alan Yeh
 * @since 2022/08/09
 */
public class DataSourceMigrator {
    /**
     * 应用名
     */
    @Nonnull
    @Getter
    private final String name;

    /**
     * 目标版本
     * 如果目标版本比当前版本高，那么就会升级该版本
     */
    @Nonnull
    @Getter
    private final Version version;

    /**
     * 基本版本
     */
    @Nullable
    @Getter
    private final Version baseline;

    @Getter
    private final List<Migration> migrations = new ArrayList<>();

    public DataSourceMigrator(@Nonnull String name, @Nonnull Version version, @Nullable Version baseline) {
        this.name = Objects.requireNonNull(name);
        this.version = Objects.requireNonNull(version);
        this.baseline = baseline;
    }

    public DataSourceMigrator(@Nonnull String name, @Nonnull Version version) {
        this(name, version, null);
    }

    public DataSourceMigrator add(Migration migration) {
        this.migrations.add(migration);
        return this;
    }

    public DataSourceMigrator add(List<Migration> migrations) {
        this.migrations.addAll(migrations);
        return this;
    }

    /**
     * 迁移数据源
     *
     * @param executor Sql 执行器
     */
    public void migrate(SqlExecutor executor) throws SQLException {
        DatabaseMeta meta = DatabaseMetaBuilder.build(executor, "X_DS_MIGRATION");
        // 构建迁移表
        var migrator = new DatabaseMigrator(meta);
        MIGRATION_INIT.migrate(migrator);
        migrator.migrate(executor);
        MIGRATION_INIT.migrate(executor);

        var mapper = executor.getMapper(MigrationMapper.class);
        // 查询当前的版本信息
        var migration = mapper.findFirstBy(Conditions.where().eq(MigrationEntity::getCode, this.getName()));

        var versions = this.migrations;
        if (migration != null) {
            // 筛选掉那些开始版本在当前版本之前的迁移
            versions = this.migrations.stream()
                    .filter(it -> CompareResultEnum.GREATER.matches(it.getBegin(), Version.of(migration.getVersion())))
                    .sorted(Comparator.comparing(Migration::getBegin))
                    .toList();
        }

        // 依次执行版本升级脚本
        for (var it : versions) {
            meta = DatabaseMetaBuilder.build(executor, "X_");
            migrator = new DatabaseMigrator(meta);
            // 表迁移
            it.migrate(migrator);
            migrator.migrate(executor);
            // 数据迁移
            it.migrate(executor);
        }

        // 保存版本信息
        if (migration == null){
            var entity = MigrationEntity.builder().code(this.name).version(this.version.toString()).build();
            entity.updateCreator("x.orm");
            mapper.insert(entity);
        }
        if (migration != null){
            if (migration.getVersion().equals(this.version.toString())){
                migration.setVersion(this.version.toString());
                migration.updateModifier("x.orm");
                mapper.update(migration);
            }
        }
    }

    private static final Migration MIGRATION_INIT = new Migration(Version.of("1.0.0")) {
        @Override
        public void migrate(Migrator migrator) {
            var table = migrator.getTable("X_DS_MIGRATION");
            if (table == null) {
                // 创建用于记用迁移版本的表
                var columns = List.of(
                        Column.of("ID", true, SqlType.STRING, 36, "主键"),
                        Column.of("CODE", SqlType.STRING, 128, "应用标识"),
                        Column.of("VERSION", SqlType.STRING, 36, "版本号"),
                        Column.of("CREATOR_ID", SqlType.STRING, 36, "创建人主键"),
                        Column.of("CREATE_DATE", SqlType.DATETIME, "创建时间"),
                        Column.of("MODIFIER_ID", SqlType.STRING, 36, "更新人主键"),
                        Column.of("MODIFY_DATE", SqlType.DATETIME, "更新时间")
                );

                var indices = List.of(
                        Index.of("X_DM_CODE", true, "CODE")
                );

                migrator.addTable(Table.of("X_DS_MIGRATION", "数据迁移表", columns, indices));
            }

            // 如果后面要修改迁移表的结构，就在这里加就好了
            // 注意，迁移表没办法记录表的版本号，因此需要注意做好表结构的判断
        }
    };
}
