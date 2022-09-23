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

package central.sql;

import central.sql.data.*;
import central.sql.datasource.factory.hikari.HikariDataSourceFactory;
import central.sql.impl.standard.StandardDataSourceMigrator;
import central.sql.impl.standard.StandardSource;
import central.sql.impl.standard.StandardExecutor;
import central.sql.interceptor.LogInterceptor;
import central.sql.mapper.AccountMapper;
import central.sql.impl.standard.StandardMetaManager;
import central.sql.mapper.DeptMapper;
import central.sql.mapper.RoleAccountMapper;
import central.sql.mapper.RoleMapper;
import central.sql.migration.V1;
import central.sql.proxy.Mapper;
import central.util.Guidx;
import central.lang.Stringx;
import central.util.Version;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Mapper Test Cases
 *
 * @author Alan Yeh
 * @since 2022/09/13
 */
public class TestMapper {
    private AccountMapper accountMapper;

    private DeptMapper deptMapper;

    private RoleMapper roleMapper;

    private RoleAccountMapper relMapper;

    private SqlSource source;

    @BeforeEach
    public void before() throws Exception {
        // H2
        var dataSource = new HikariDataSourceFactory().build("org.h2.Driver", "jdbc:h2:mem:test-mapper", "centralx", "central.x");
        // Mysql
//        var dataSource = new HikariDataSourceFactory().buildDialect("com.mysql.jdbc.Driver", "jdbc:mysql://10.10.20.20:3306/centralx?useUnicode=true&characterEncoding=utf8&useSSL=false", "root", "root");
        // Oracle
//        var dataSource = new HikariDataSourceFactory().buildDialect("oracle.jdbc.OracleDriver", "jdbc:oracle:thin:@10.10.20.20:1521:orcl", "centralx", "123456");
        // Postgresql
//        var dataSource = new HikariDataSourceFactory().buildDialect("org.postgresql.Driver", "jdbc:postgresql://10.10.20.20:5432/postgres", "postgres", "root");

        this.source = StandardSource.builder()
                .dataSource(dataSource)
                .dialect(SqlDialect.H2)
                .migrator(StandardDataSourceMigrator.builder().name("test").target(Version.of("1.0.1")).add(new V1()).build())
                .build();

        var executor = StandardExecutor.builder()
                .source(this.source)
                .metaManager(new StandardMetaManager(name -> name.startsWith("XT_")))
                .addInterceptor(new LogInterceptor())
                .build();
        executor.init();

        this.accountMapper = executor.getMapper(AccountMapper.class);
        this.deptMapper = executor.getMapper(DeptMapper.class);
        this.roleMapper = executor.getMapper(RoleMapper.class);
        this.relMapper = executor.getMapper(RoleAccountMapper.class);

        this.accountMapper.deleteAll();
        this.deptMapper.deleteAll();
        this.roleMapper.deleteAll();
        this.relMapper.deleteAll();
    }

    @AfterEach
    public void after() throws SQLException {
        this.accountMapper.deleteAll();
        this.deptMapper.deleteAll();
        this.roleMapper.deleteAll();
        this.relMapper.deleteAll();

    }

    /**
     * @see Mapper#getMapper
     */
    @Test
    public void case1() {
        var deptMapper = this.accountMapper.getMapper(DeptMapper.class);
        assertNotNull(deptMapper);
        assertInstanceOf(DeptMapper.class, deptMapper);
    }

    /**
     * @see Mapper#insert
     * @see Mapper#findById
     */
    @Test
    public void case2() {
        var entity = new AccountEntity();
        entity.setUsername("zhangs");
        entity.setName("张三");
        entity.setAge(18);
        entity.setDeptId("");
        entity.setEnabled(true);
        entity.setAvatar(Guidx.nextID().getBytes(StandardCharsets.UTF_8));
        entity.setSalary(BigDecimal.valueOf(10086.18d));
        entity.setHiredate(System.currentTimeMillis());
        entity.updateCreator("sa");
        this.accountMapper.insert(entity);

        // 自动生成 ID
        assertNotNull(entity.getId());

        var record = accountMapper.findById(entity.getId());
        assertNotNull(record);
        assertEquals(entity.getId(), record.getId());
        assertEquals(entity.getUsername(), record.getUsername());
        assertEquals(entity.getAge(), record.getAge());
        assertEquals(entity.getDeptId(), record.getDeptId());
        assertEquals(entity.getEnabled(), record.getEnabled());
        assertArrayEquals(entity.getAvatar(), record.getAvatar());
        assertEquals(entity.getSalary(), record.getSalary());
        assertEquals(entity.getHiredate(), record.getHiredate());
        assertEquals(entity.getCreatorId(), record.getCreatorId());
        assertEquals(entity.getCreateDate(), record.getCreateDate());
        assertEquals(entity.getModifierId(), record.getModifierId());
        assertEquals(entity.getModifyDate(), record.getModifyDate());
    }

    /**
     * @see Mapper#insertBatch
     * @see Mapper#findByIds
     */
    @Test
    public void case3() {
        var entities = new ArrayList<AccountEntity>();
        var zhangs = new AccountEntity();
        zhangs.setUsername("zhangs");
        zhangs.setName("张三");
        zhangs.setAge(18);
        zhangs.setDeptId("");
        zhangs.setEnabled(true);
        zhangs.setAvatar(Guidx.nextID().getBytes(StandardCharsets.UTF_8));
        zhangs.setSalary(BigDecimal.valueOf(10086.18d));
        zhangs.setHiredate(System.currentTimeMillis());
        zhangs.updateCreator("sa");
        entities.add(zhangs);

        var lis = new AccountEntity();
        lis.setUsername("lis");
        lis.setName("李四");
        lis.setAge(19);
        lis.setDeptId("");
        lis.setEnabled(false);
        lis.setAvatar(Guidx.nextID().getBytes(StandardCharsets.UTF_8));
        lis.setSalary(BigDecimal.valueOf(10086.18d));
        lis.setHiredate(System.currentTimeMillis());
        lis.updateCreator("sa");
        entities.add(lis);

        this.accountMapper.insertBatch(entities);
        // 自动生成 ID
        assertTrue(entities.stream().noneMatch(it -> Stringx.isNullOrEmpty(it.getId())));

        var records = accountMapper.findByIds(List.of(zhangs.getId(), lis.getId()));
        assertEquals(records.size(), 2);
        var zhangsR = records.stream().filter(it -> Objects.equals(zhangs.getId(), it.getId())).findFirst().orElse(null);
        assertNotNull(zhangsR);
        assertEquals(zhangs.getId(), zhangsR.getId());
        assertEquals(zhangs.getUsername(), zhangsR.getUsername());
        assertEquals(zhangs.getAge(), zhangsR.getAge());
        assertEquals(zhangs.getDeptId(), zhangsR.getDeptId());
        assertEquals(zhangs.getEnabled(), zhangsR.getEnabled());
        assertArrayEquals(zhangs.getAvatar(), zhangsR.getAvatar());
        assertEquals(zhangs.getSalary(), zhangsR.getSalary());
        assertEquals(zhangs.getHiredate(), zhangsR.getHiredate());
        assertEquals(zhangs.getCreatorId(), zhangsR.getCreatorId());
        assertEquals(zhangs.getCreateDate(), zhangsR.getCreateDate());
        assertEquals(zhangs.getModifierId(), zhangsR.getModifierId());
        assertEquals(zhangs.getModifyDate(), zhangsR.getModifyDate());


        var lisR = records.stream().filter(it -> Objects.equals(lis.getId(), it.getId())).findFirst().orElse(null);
        assertNotNull(lisR);
        assertEquals(lis.getId(), lisR.getId());
        assertEquals(lis.getUsername(), lisR.getUsername());
        assertEquals(lis.getAge(), lisR.getAge());
        assertEquals(lis.getDeptId(), lisR.getDeptId());
        assertEquals(lis.getEnabled(), lisR.getEnabled());
        assertArrayEquals(lis.getAvatar(), lisR.getAvatar());
        assertEquals(lis.getSalary(), lisR.getSalary());
        assertEquals(lis.getHiredate(), lisR.getHiredate());
        assertEquals(lis.getCreatorId(), lisR.getCreatorId());
        assertEquals(lis.getCreateDate(), lisR.getCreateDate());
        assertEquals(lis.getModifierId(), lisR.getModifierId());
        assertEquals(lis.getModifyDate(), lisR.getModifyDate());
    }


    /**
     * @see Mapper#insertBatch
     * @see Mapper#deleteByIds
     * @see Mapper#deleteBy
     * @see Mapper#deleteAll
     * @see Mapper#count
     * @see Mapper#countBy
     */
    @Test
    public void case4() {
        var entities = new ArrayList<AccountEntity>();
        var zhangs = new AccountEntity();
        zhangs.setUsername("zhangs");
        zhangs.setName("张三");
        zhangs.setAge(18);
        zhangs.setDeptId("");
        zhangs.setEnabled(true);
        zhangs.setAvatar(Guidx.nextID().getBytes(StandardCharsets.UTF_8));
        zhangs.setSalary(BigDecimal.valueOf(10086.18d));
        zhangs.setHiredate(System.currentTimeMillis());
        zhangs.updateCreator("sa");
        entities.add(zhangs);

        var lis = new AccountEntity();
        lis.setUsername("lis");
        lis.setName("李四");
        lis.setAge(19);
        lis.setDeptId("");
        lis.setEnabled(false);
        lis.setAvatar(Guidx.nextID().getBytes(StandardCharsets.UTF_8));
        lis.setSalary(BigDecimal.valueOf(10086.18d));
        lis.setHiredate(System.currentTimeMillis());
        lis.updateCreator("sa");
        entities.add(lis);

        this.accountMapper.insertBatch(entities);
        // 自动生成 ID
        assertTrue(entities.stream().noneMatch(it -> Stringx.isNullOrEmpty(it.getId())));

        var effected = this.accountMapper.deleteByIds(entities.stream().map(Entity::getId).toList());
        assertEquals(2, effected);
        var count = this.accountMapper.count();
        assertEquals(0, count);

        this.accountMapper.insertBatch(entities);
        effected = this.accountMapper.deleteBy(Conditions.where().in(Entity::getId, entities.stream().map(Entity::getId).toList()));
        assertEquals(2, effected);
        count = this.accountMapper.count();
        assertEquals(0, count);

        this.accountMapper.insertBatch(entities);
        effected = this.accountMapper.deleteById(zhangs.getId());
        assertEquals(1, effected);
        count = this.accountMapper.count();
        assertEquals(1, count);

        this.accountMapper.deleteAll();
        this.accountMapper.insertBatch(entities);
        count = this.accountMapper.count();
        assertEquals(2, count);
        count = this.accountMapper.countBy(Conditions.where().eq(AccountEntity::getAge, 19));
        assertEquals(1, count);
    }

    /**
     * @see Mapper#update
     * @see Mapper#updateBy
     */
    @Test
    public void case5() {
        var zhangs = new AccountEntity();
        zhangs.setUsername("zhangs");
        zhangs.setName("张三");
        zhangs.setAge(18);
        zhangs.setDeptId("");
        zhangs.setEnabled(true);
        zhangs.setAvatar(Guidx.nextID().getBytes(StandardCharsets.UTF_8));
        zhangs.setSalary(BigDecimal.valueOf(10086.18d));
        zhangs.setHiredate(System.currentTimeMillis());
        zhangs.updateCreator("sa");

        var inserted = this.accountMapper.insert(zhangs);
        assertTrue(inserted);

        zhangs.setName("张叁");
        var updated = this.accountMapper.update(zhangs);
        assertTrue(updated);

        var record = this.accountMapper.findById(zhangs.getId());
        assertNotNull(record);
        assertEquals("张叁", record.getName());

        var updateBy = new AccountEntity();
        updateBy.setId(zhangs.getId());
        updateBy.setName("张三");
        updateBy.updateModifier("sa");
        updated = this.accountMapper.updateBy(updateBy);
        assertTrue(updated);

        record = this.accountMapper.findById(zhangs.getId());
        assertNotNull(record);
        assertEquals(zhangs.getId(), record.getId());
        assertEquals(updateBy.getName(), record.getName());
        assertEquals(zhangs.getAge(), record.getAge());
        assertEquals(zhangs.getDeptId(), record.getDeptId());
        assertEquals(zhangs.getCreatorId(), record.getCreatorId());
        assertEquals(zhangs.getCreateDate(), record.getCreateDate());
        assertEquals(zhangs.getModifierId(), record.getModifierId());
        assertNotEquals(zhangs.getModifyDate(), record.getModifyDate());
    }

    /**
     * @see Mapper#findFirstBy
     * @see Mapper#findBy
     * @see Mapper#findAll
     * @see Mapper#existsBy
     */
    @Test
    public void case6() {
        var entities = new ArrayList<AccountEntity>();
        var zhangs = new AccountEntity();
        zhangs.setUsername("zhangs");
        zhangs.setName("张三");
        zhangs.setAge(18);
        zhangs.setDeptId("");
        zhangs.setEnabled(true);
        zhangs.setAvatar(Guidx.nextID().getBytes(StandardCharsets.UTF_8));
        zhangs.setSalary(BigDecimal.valueOf(10086.18d));
        zhangs.setHiredate(System.currentTimeMillis());
        zhangs.updateCreator("sa");
        entities.add(zhangs);

        var lis = new AccountEntity();
        lis.setUsername("lis");
        lis.setName("李三");
        lis.setAge(19);
        lis.setDeptId("");
        lis.setEnabled(false);
        lis.setAvatar(Guidx.nextID().getBytes(StandardCharsets.UTF_8));
        lis.setSalary(BigDecimal.valueOf(10086.18d));
        lis.setHiredate(System.currentTimeMillis());
        lis.updateCreator("sa");
        entities.add(lis);

        this.accountMapper.insertBatch(entities);

        var record = this.accountMapper.findFirstBy(Conditions.where().like(AccountEntity::getName, "%三"), Orders.order().asc(AccountEntity::getAge));
        assertNotNull(record);
        assertEquals(zhangs.getId(), record.getId());

        var records = this.accountMapper.findBy(Conditions.where().like(AccountEntity::getName, "%三"), Orders.order().asc(AccountEntity::getAge));
        assertFalse(records.isEmpty());
        assertEquals(2, records.size());

        records = this.accountMapper.findBy(1L, 0L, Conditions.where().like(AccountEntity::getName, "%三"), Orders.order().asc(AccountEntity::getAge));
        assertFalse(records.isEmpty());
        assertEquals(1, records.size());

        records = this.accountMapper.findAll();
        assertFalse(records.isEmpty());
        assertEquals(2, records.size());

        records = this.accountMapper.findAll(Orders.order().desc(AccountEntity::getAge));
        assertFalse(records.isEmpty());
        assertEquals(2, records.size());

        var exists = this.accountMapper.existsBy(Conditions.where().like(AccountEntity::getName, "%三"));
        assertTrue(exists);
    }

    /**
     * @see Mapper#findPageBy
     */
    @Test
    public void case7() {
        var entities = new ArrayList<AccountEntity>();
        for (int i = 1; i <= 100; i++) {
            var account = new AccountEntity();
            account.setUsername("zhang" + i);
            account.setName("张" + i);
            account.setAge(i);
            account.setDeptId("");
            account.setEnabled(false);
            account.setAvatar(Guidx.nextID().getBytes(StandardCharsets.UTF_8));
            account.setSalary(BigDecimal.valueOf(10086.18d));
            account.setHiredate(System.currentTimeMillis());
            account.updateCreator("sa");
            entities.add(account);
        }
        this.accountMapper.insertBatch(entities);

        var page = this.accountMapper.findPageBy(1L, 20L, Conditions.where().like(AccountEntity::getName, "张%"), Orders.order().asc(AccountEntity::getAge));
        assertNotNull(page);
        assertEquals(1L, page.getPager().getPageIndex());
        assertEquals(20L, page.getPager().getPageSize());
        assertEquals(5L, page.getPager().getPageCount());
        assertEquals(100L, page.getPager().getItemCount());
        assertNotNull(page.getData());
        assertEquals(20, page.getData().size());
        assertEquals(page.getData().get(0).getName(), "张1");

        page = this.accountMapper.findPageBy(2L, 15L, Conditions.where().like(AccountEntity::getName, "张%"), Orders.order().asc(AccountEntity::getAge));
        assertNotNull(page);
        assertEquals(2L, page.getPager().getPageIndex());
        assertEquals(15L, page.getPager().getPageSize());
        assertEquals((long) Math.ceil(100.0 / 15), page.getPager().getPageCount());
        assertEquals(100L, page.getPager().getItemCount());
        assertNotNull(page.getData());
        assertEquals(15, page.getData().size());
        assertEquals(page.getData().get(0).getName(), "张16");
    }

    /**
     * @see central.sql.meta.annotation.Relation
     */
    @Test
    public void case8() {
        // 添加部门
        var depts = new ArrayList<DeptEntity>();
        var hr = new DeptEntity();
        hr.setCode("hr");
        hr.setName("人力部");
        hr.updateCreator("sa");
        depts.add(hr);

        var sale = new DeptEntity();
        sale.setCode("sale");
        sale.setName("销售部");
        sale.updateCreator("sa");
        depts.add(sale);
        this.deptMapper.insertBatch(depts);

        // 添加帐户
        var accounts = new ArrayList<AccountEntity>();
        var zhangs = new AccountEntity();
        zhangs.setUsername("zhangs");
        zhangs.setName("张三");
        zhangs.setAge(18);
        zhangs.setDeptId(hr.getId());
        zhangs.setEnabled(true);
        zhangs.setAvatar(Guidx.nextID().getBytes(StandardCharsets.UTF_8));
        zhangs.setSalary(BigDecimal.valueOf(10086.18d));
        zhangs.setHiredate(System.currentTimeMillis());
        zhangs.updateCreator("sa");
        accounts.add(zhangs);

        var lis = new AccountEntity();
        lis.setUsername("lis");
        lis.setName("李四");
        lis.setAge(19);
        lis.setDeptId(hr.getId());
        lis.setEnabled(false);
        lis.setAvatar(Guidx.nextID().getBytes(StandardCharsets.UTF_8));
        lis.setSalary(BigDecimal.valueOf(10086.18d));
        lis.setHiredate(System.currentTimeMillis());
        lis.updateCreator("sa");
        accounts.add(lis);

        var wangw = new AccountEntity();
        wangw.setUsername("wangw");
        wangw.setName("王五");
        wangw.setAge(20);
        wangw.setDeptId(sale.getId());
        wangw.setEnabled(false);
        wangw.setAvatar(Guidx.nextID().getBytes(StandardCharsets.UTF_8));
        wangw.setSalary(BigDecimal.valueOf(10086.18d));
        wangw.setHiredate(System.currentTimeMillis());
        wangw.updateCreator("sa");
        accounts.add(wangw);

        this.accountMapper.insertBatch(accounts);

        // 添加角色
        var roles = new ArrayList<RoleEntity>();
        var role = new RoleEntity();
        role.setName("收件人");
        role.setCode("receiver");
        role.updateCreator("sa");
        roles.add(role);
        this.roleMapper.insertBatch(roles);

        // 添加角色与帐户的关联
        var rels = new ArrayList<RoleAccountEntity>();
        var rel1 = new RoleAccountEntity();
        rel1.setRoleId(role.getId());
        rel1.setAccountId(lis.getId());
        rel1.updateCreator("sa");
        rels.add(rel1);

        var rel2 = new RoleAccountEntity();
        rel2.setRoleId(role.getId());
        rel2.setAccountId(wangw.getId());
        rel2.updateCreator("sa");
        rels.add(rel2);

        this.relMapper.insertBatch(rels);

        // 测试一对一、一对多关联查询
        var hrs = this.accountMapper.findBy(Conditions.where().eq("dept.code", "hr"));
        assertEquals(2, hrs.size());
        assertTrue(hrs.stream().anyMatch(it -> it.getId().equals(zhangs.getId())));
        assertTrue(hrs.stream().anyMatch(it -> it.getId().equals(lis.getId())));

        var sales = this.accountMapper.findBy(Conditions.where().eq("dept.code", "sale"));
        assertEquals(1, sales.size());
        assertTrue(sales.stream().anyMatch(it -> it.getId().equals(wangw.getId())));

        // 测试一对多关联查询
        var receives = this.accountMapper.findBy(Conditions.where().eq("role.code", "receiver"));
        assertEquals(2, receives.size());
        assertTrue(receives.stream().anyMatch(it -> it.getId().equals(lis.getId())));
        assertTrue(receives.stream().anyMatch(it -> it.getId().equals(wangw.getId())));
    }
}
