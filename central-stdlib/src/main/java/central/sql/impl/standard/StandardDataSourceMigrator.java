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

package central.sql.impl.standard;

import central.lang.Assertx;
import central.lang.CompareResultEnum;
import central.sql.Conditions;
import central.sql.SqlExecutor;
import central.sql.SqlMetaManager;
import central.sql.SqlType;
import central.sql.datasource.migration.*;
import central.sql.datasource.migration.data.MigrationEntity;
import central.sql.datasource.migration.data.MigrationMapper;
import central.sql.datasource.migration.migrator.DatabaseMigrator;
import central.sql.meta.database.DatabaseMeta;
import central.util.Version;
import central.validation.Label;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

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
public class StandardDataSourceMigrator implements DataSourceMigrator {
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
    private final Version target;

    /**
     * 基础版本
     * 跳过指定版本的，只执行指定版本之后的脚本
     */
    @Nullable
    @Getter
    private final Version baseline;

    @Getter
    private final List<Migration> migrations = new ArrayList<>();

    private final SqlMetaManager manager = new StandardMetaManager(name -> name.equals("X_DS_MIGRATION"));

    public StandardDataSourceMigrator(@Nonnull String name, @Nonnull Version target, @Nullable Version baseline) {
        this.name = Assertx.requireNotBlank(name, "应用名[name]必须不为空");
        this.target = Assertx.requireNotNull(target, "目标版本[target]必须不为空");
        this.baseline = baseline;
    }

    public StandardDataSourceMigrator(@Nonnull String name, @Nonnull Version target) {
        this(name, target, null);
    }

    @Override
    public void addMigration(Migration migration) {
        this.migrations.add(migration);
    }

    private void init(SqlExecutor executor) throws SQLException {
        DatabaseMeta meta = manager.getMeta(executor);
        // 构建迁移表
        var migrator = new DatabaseMigrator(meta);
        INITIALIZATION.upgrade(migrator);
        migrator.migrate(executor);
        INITIALIZATION.upgrade(executor);
    }

    /**
     * 升级数据源
     *
     * @param executor Sql 执行器
     */
    public void upgrade(SqlExecutor executor) throws SQLException {
        this.init(executor);

        // 查询当前的版本信息
        var mapper = executor.getMapper(MigrationMapper.class);
        var migration = mapper.findFirstBy(Conditions.where().eq(MigrationEntity::getCode, this.getName()));

        var versions = this.migrations;
        if (migration != null) {
            // 筛选掉那些开始版本在当前版本之前的迁移
            versions = versions.stream()
                    // 如果初始化版本为空，则
                    .filter(it -> it.getBegin() != null)
                    .filter(it -> CompareResultEnum.GE.matches(it.getBegin(), Version.of(migration.getVersion())))
                    .toList();
        }

        // 筛选掉 target 版本之后的迁移
        versions = versions.stream()
                .filter(it -> CompareResultEnum.LE.matches(it.getEnd(), this.getTarget()))
                .toList();

        if (versions.isEmpty()) {
            // 没有需要执行的迁移
            return;
        }

        // 构建升级路径
        versions = this.buildUpgradeMigration(versions);

        // 依次执行版本升级脚本
        for (var it : versions) {
            var meta = executor.getMetaManager().getMeta(executor);
            var migrator = new DatabaseMigrator(meta);
            // 表迁移
            it.upgrade(migrator);
            migrator.migrate(executor);
            // 数据迁移
            it.upgrade(executor);
        }

        // 保存版本信息
        if (migration == null) {
            var entity = MigrationEntity.builder().code(this.name).version(this.target.toString()).build();
            entity.updateCreator("x.orm");
            mapper.insert(entity);
        }
        if (migration != null) {
            if (!migration.getVersion().equals(this.target.toString())) {
                migration.setVersion(this.target.toString());
                migration.updateModifier("x.orm");
                mapper.update(migration);
            }
        }
    }

    /**
     * 降级数据源
     *
     * @param executor Sql 执行器
     */
    @Override
    public void downgrade(SqlExecutor executor) throws SQLException {
        this.init(executor);
        // 查询当前的版本信息
        var mapper = executor.getMapper(MigrationMapper.class);
        var migration = mapper.findFirstBy(Conditions.where().eq(MigrationEntity::getCode, this.getName()));

        var versions = this.migrations;
        if (migration == null || CompareResultEnum.LE.matches(Version.of(migration.getVersion()), Version.of("0"))) {
            // 如果当前的 migration 为空，则表示当前暂没有迁移过，因此可以跳过降级的过程
            return;
        } else {
            // 筛选掉结束版本在当前版本之前的
            versions = versions.stream()
                    .filter(it -> CompareResultEnum.LE.matches(it.getEnd(), Version.of(migration.getVersion())))
                    .toList();
        }

        // 筛选掉开始版本在目标版本之后的
        versions = versions.stream()
                .filter(it -> CompareResultEnum.GE.matches(it.getBegin(), this.getTarget()))
                .toList();

        if (versions.isEmpty()) {
            // 没有需要执行的迁移
            return;
        }

        versions = buildDowngradeMigration(versions);

        // 依次执行版本降级脚本
        for (var it : versions) {
            var meta = executor.getMetaManager().getMeta(executor);
            var migrator = new DatabaseMigrator(meta);
            // 表迁移
            it.downgrade(migrator);
            migrator.migrate(executor);
            // 数据迁移
            it.downgrade(executor);
        }

        // 保存版本信息
        if (CompareResultEnum.EQUALS.matches(Version.of("0"), this.getTarget())) {
            // 如果降级到 0，则表示初始化为空
            mapper.deleteBy(Conditions.where().eq(MigrationEntity::getCode, this.name));
        } else {
            migration.setVersion(this.getTarget().toString());
            migration.updateModifier("x.orm");
            mapper.update(migration);
        }
    }

    /**
     * 构建升级路线
     */
    private List<Migration> buildUpgradeMigration(List<Migration> migrations) {
        migrations = new ArrayList<>(migrations);
        // 根据起始版本号排序
        migrations.sort(Comparator.comparing(Migration::getBegin));

        var result = new ArrayList<Migration>();

        Migration last = null;
        for (var migration : migrations) {
            if (last == null) {
                last = migration;
                continue;
            }

            if (Objects.equals(migration.getBegin(), last.getBegin())) {
                // 如果起始版本号相同，结束版本号也相同的话，就没办法使用哪个版本号来执行升级
                Assertx.mustNotEquals(migration.getEnd(), last.getEnd(), "存在相同版本号的");

                // 如果结束版本不相同，则使用跨度大的版本号
                if (CompareResultEnum.GT.matches(migration.getEnd(), last.getEnd())) {
                    last = migration;
                }
            } else {
                // 如果起始版本不一致，则检查起始版本是不是在 last 版本的 end 版本之后（有可能被跨过了）
                if (CompareResultEnum.LE.matches(last.getEnd(), migration.getBegin())) {
                    // 如果上一版本的结束版本小于等于当前版本的开始版本，说明这个版本号还没被跳过
                    // 检查版本号是否连贯
                    Assertx.mustEquals(migration.getBegin(), last.getEnd(), "版本[{}]无法升级到[{}]: 版本升级不连贯", last.getEnd(), migration.getEnd());

                    // 如果连贯，则将上一个加入版本升级列表
                    result.add(last);
                    last = migration;
                }
            }
        }

        if (last != null) {
            result.add(last);
        }

        return result;
    }

    /**
     * 构建降级路线
     */
    private List<Migration> buildDowngradeMigration(List<Migration> migrations) {
        migrations = new ArrayList<>(migrations);

        // 根据结束版本号倒序排序
        migrations.sort(Comparator.comparing(Migration::getEnd).reversed());

        var result = new ArrayList<Migration>();

        Migration last = null;
        for (var migration : migrations) {
            if (last == null) {
                last = migration;
                continue;
            }

            if (Objects.equals(migration.getEnd(), last.getEnd())) {
                // 如是结束版本相同，开始版本也相同的话，就没办法使用哪个版本号来执行升级了
                Assertx.mustNotEquals(migration.getBegin(), last.getBegin(), "存在相同版本号");

                // 如果开始版本不相同，则使用跨度大的版本号
                if (CompareResultEnum.LT.matches(migration.getBegin(), last.getBegin())) {
                    last = migration;
                }
            } else {
                // 如果结束版本不一致，则检查起始版本是不是在 last 版本的 begin 版本之前（有可能被跨过了）
                if (CompareResultEnum.GE.matches(last.getBegin(), migration.getEnd())) {
                    // 如果上一版本的开始版本大于等于当前版本的结束版本，说明这个版本号还没被跳过
                    // 检查版本号是否连贯
                    Assertx.mustEquals(migration.getEnd(), last.getBegin(), "版本号[{}]无法降级到[{}]: 版本升级不连贯", last.getBegin(), migration.getBegin());

                    // 如果连贯，则将上一个加入版本降级列表
                    result.add(last);
                    last = migration;
                }
            }
        }

        if (last != null) {
            result.add(last);
        }

        return result;
    }

    private static final Migration INITIALIZATION = new Migration(Version.of("1.0.0")) {
        @Override
        public void upgrade(Database database) {
            var table = database.getTable("X_DS_MIGRATION");
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

                database.addTable(Table.of("X_DS_MIGRATION", "数据迁移表", columns, indices));
            }

            // 如果后面要修改迁移表的结构，就在这里加就好了
            // 注意，迁移表没办法记录表的版本号，因此需要注意做好表结构的判断
        }
    };

    public static Builder builder() {
        return new Builder();
    }

    @Setter
    @Accessors(chain = true, fluent = true)
    public static class Builder {

        @NotBlank
        @Label("迁移名称")
        private String name;

        @NotNull
        @Label("基线版本")
        private Version baseline = Version.of("0");

        @NotNull
        @Label("目标版本")
        private Version target;

        @Label("迁移作动")
        private final List<Migration> migrations = new ArrayList<>();

        public Builder add(Migration migration) {
            this.migrations.add(migration);
            return this;
        }

        public Builder addAll(List<Migration> migrations) {
            this.migrations.addAll(migrations);
            return this;
        }

        public DataSourceMigrator build() {
            var migrator = new StandardDataSourceMigrator(this.name, this.target, this.baseline);
            this.migrations.forEach(migrator::addMigration);
            return migrator;
        }
    }
}
