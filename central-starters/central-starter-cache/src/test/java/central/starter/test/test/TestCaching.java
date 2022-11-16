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

package central.starter.test.test;

import central.sql.Conditions;
import central.sql.Orders;
import central.starter.cache.core.CacheStorage;
import central.starter.test.TestApplication;
import central.starter.test.service.AccountService;
import central.starter.test.service.DepartmentService;
import central.starter.test.service.data.Account;
import central.util.Guidx;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Caching Test Cases
 *
 * @author Alan Yeh
 * @since 2022/11/15
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = TestApplication.class)
public class TestCaching {

    @Setter(onMethod_ = @Autowired)
    private CacheStorage storage;

    @Setter(onMethod_ = @Autowired)
    private AccountService accounts;

    @Setter(onMethod_ = @Autowired)
    private DepartmentService departments;

    @Test
    public void case1() {
        var id = Guidx.nextID();
        var account1 = accounts.findById(id);

        assertEquals(id, account1.getId());

        // cached
        // 重复查询，会返回缓存
        var account2 = accounts.findById(id);
        assertEquals(account1.getId(), account2.getId());
        assertEquals(account1.getName(), account2.getName());
        assertEquals(account1.getCreateDate(), account2.getCreateDate());
        assertEquals(account1.getCreatorId(), account2.getCreatorId());
        assertEquals(account1.getModifyDate(), account2.getModifyDate());
        assertEquals(account1.getModifierId(), account2.getCreatorId());

        // 部门因为缓存的关系，部门应该也是一样的
        assertEquals(account1.getDepartmentId(), account2.getDepartmentId());
        assertEquals(account1.getDepartment().getId(), account2.getDepartment().getId());
        assertEquals(account1.getDepartment().getName(), account2.getDepartment().getName());
        assertEquals(account1.getDepartment().getCreateDate(), account2.getDepartment().getCreateDate());
        assertEquals(account1.getDepartment().getCreatorId(), account2.getDepartment().getCreatorId());
        assertEquals(account1.getDepartment().getModifyDate(), account2.getDepartment().getModifyDate());
        assertEquals(account1.getDepartment().getModifierId(), account2.getDepartment().getModifierId());

        // clean other cache
        // 删除其它帐户的主键，不会对当前缓存产生影响
        accounts.deleteByIds(List.of(Guidx.nextID()));

        var account3 = accounts.findById(id);
        assertEquals(account1.getId(), account3.getId());
        assertEquals(account1.getName(), account3.getName());
        assertEquals(account1.getCreateDate(), account3.getCreateDate());
        assertEquals(account1.getCreatorId(), account3.getCreatorId());
        assertEquals(account1.getModifyDate(), account3.getModifyDate());
        assertEquals(account1.getModifierId(), account3.getCreatorId());
        assertEquals(account1.getDepartmentId(), account3.getDepartmentId());
        assertEquals(account1.getDepartment().getId(), account3.getDepartment().getId());
        assertEquals(account1.getDepartment().getName(), account3.getDepartment().getName());
        assertEquals(account1.getDepartment().getCreateDate(), account3.getDepartment().getCreateDate());
        assertEquals(account1.getDepartment().getCreatorId(), account3.getDepartment().getCreatorId());
        assertEquals(account1.getDepartment().getModifyDate(), account3.getDepartment().getModifyDate());
        assertEquals(account1.getDepartment().getModifierId(), account3.getDepartment().getModifierId());

        // clean cache
        // 删除本主键的缓存，则会重新生成新的数据
        // 新生成的数据的 createDate、modifyDate、name是不一样的
        accounts.deleteByIds(List.of(id));

        var account4 = accounts.findById(id);
        assertEquals(account1.getId(), account4.getId());
        assertNotEquals(account1.getName(), account4.getName());
        assertNotEquals(account1.getCreateDate(), account4.getCreateDate());
        assertEquals(account1.getCreatorId(), account4.getCreatorId());
        assertNotEquals(account1.getModifyDate(), account4.getModifyDate());
        assertEquals(account1.getModifierId(), account4.getCreatorId());

        // 由于部门的缓存没删，所以部门的数据应该还是相同的
        assertEquals(account1.getDepartmentId(), account4.getDepartmentId());
        assertEquals(account1.getDepartment().getId(), account4.getDepartment().getId());
        assertEquals(account1.getDepartment().getName(), account4.getDepartment().getName());
        assertEquals(account1.getDepartment().getCreateDate(), account4.getDepartment().getCreateDate());
        assertEquals(account1.getDepartment().getCreatorId(), account4.getDepartment().getCreatorId());
        assertEquals(account1.getDepartment().getModifyDate(), account4.getDepartment().getModifyDate());
        assertEquals(account1.getDepartment().getModifierId(), account4.getDepartment().getModifierId());
    }

    @Test
    public void case2() {
        var id = Guidx.nextID();
        // 查询帐户
        // 帐户又会去查询部门，因此这个帐户的缓存会自动依赖部门的缓存
        var account1 = accounts.findById(id);

        assertEquals(id, account1.getId());

        // cached
        // 重复查询，会返回缓存
        var account2 = accounts.findById(id);
        assertEquals(account1.getId(), account2.getId());
        assertEquals(account1.getName(), account2.getName());
        assertEquals(account1.getCreateDate(), account2.getCreateDate());
        assertEquals(account1.getCreatorId(), account2.getCreatorId());
        assertEquals(account1.getModifyDate(), account2.getModifyDate());
        assertEquals(account1.getModifierId(), account2.getCreatorId());
        assertEquals(account1.getDepartmentId(), account2.getDepartmentId());
        assertEquals(account1.getDepartment().getId(), account2.getDepartment().getId());
        assertEquals(account1.getDepartment().getName(), account2.getDepartment().getName());
        assertEquals(account1.getDepartment().getCreateDate(), account2.getDepartment().getCreateDate());
        assertEquals(account1.getDepartment().getCreatorId(), account2.getDepartment().getCreatorId());
        assertEquals(account1.getDepartment().getModifyDate(), account2.getDepartment().getModifyDate());
        assertEquals(account1.getDepartment().getModifierId(), account2.getDepartment().getModifierId());

        // clean department cache
        // 清除部门的缓存，会自动级联删除帐户缓存
        departments.deleteByIds(List.of(account1.getDepartment().getId()));

        var account3 = accounts.findById(id);
        assertEquals(account1.getId(), account3.getId());
        assertNotEquals(account1.getName(), account3.getName());
        assertNotEquals(account1.getCreateDate(), account3.getCreateDate());
        assertEquals(account1.getCreatorId(), account3.getCreatorId());
        assertNotEquals(account1.getModifyDate(), account3.getModifyDate());
        assertEquals(account1.getModifierId(), account3.getCreatorId());

        // 部门的缓存被清了，因此部门的创建时间也会不一样
        assertEquals(account1.getDepartmentId(), account3.getDepartmentId());
        assertEquals(account1.getDepartment().getId(), account3.getDepartment().getId());
        assertNotEquals(account1.getDepartment().getName(), account3.getDepartment().getName());
        assertNotEquals(account1.getDepartment().getCreateDate(), account3.getDepartment().getCreateDate());
        assertEquals(account1.getDepartment().getCreatorId(), account3.getDepartment().getCreatorId());
        assertNotEquals(account1.getDepartment().getModifyDate(), account3.getDepartment().getModifyDate());
        assertEquals(account1.getDepartment().getModifierId(), account3.getDepartment().getModifierId());
    }

    @Test
    public void case3() {
        // 查找列表
        var list = this.accounts.findBy(5L, 10L, Conditions.of(Account.class).like(Account::getName, "帐号%"), null);

        // 参数不变，因此查询结果相同
        var list2 = this.accounts.findBy(5L, 10L, Conditions.of(Account.class).like(Account::getName, "帐号%"), null);

        assertEquals(list.size(), list2.size());
        for (int i = 0; i < list.size(); i ++){
            var account1 = list.get(i);
            var account2 = list2.get(i);

            assertEquals(account1.getId(), account2.getId());
            assertEquals(account1.getName(), account2.getName());
            assertEquals(account1.getCreateDate(), account2.getCreateDate());
            assertEquals(account1.getCreatorId(), account2.getCreatorId());
            assertEquals(account1.getModifyDate(), account2.getModifyDate());
            assertEquals(account1.getModifierId(), account2.getCreatorId());
            assertEquals(account1.getDepartmentId(), account2.getDepartmentId());
            assertEquals(account1.getDepartment().getId(), account2.getDepartment().getId());
            assertEquals(account1.getDepartment().getName(), account2.getDepartment().getName());
            assertEquals(account1.getDepartment().getCreateDate(), account2.getDepartment().getCreateDate());
            assertEquals(account1.getDepartment().getCreatorId(), account2.getDepartment().getCreatorId());
            assertEquals(account1.getDepartment().getModifyDate(), account2.getDepartment().getModifyDate());
            assertEquals(account1.getDepartment().getModifierId(), account2.getDepartment().getModifierId());
        }

        // 任意一个元数的变更，都有可能对列表产生变更，因此需要清缓存
        this.accounts.deleteByIds(List.of(Guidx.nextID()));

        var list3 = this.accounts.findBy(5L, 10L, Conditions.of(Account.class).like(Account::getName, "帐号%"), null);

        assertEquals(list.size(), list3.size());
        for (int i = 0; i < list.size(); i ++){
            var account1 = list.get(i);
            var account2 = list3.get(i);

            assertNotEquals(account1.getId(), account2.getId());
            assertNotEquals(account1.getName(), account2.getName());
            assertNotEquals(account1.getCreateDate(), account2.getCreateDate());
            assertEquals(account1.getCreatorId(), account2.getCreatorId());
            assertNotEquals(account1.getModifyDate(), account2.getModifyDate());
            assertEquals(account1.getModifierId(), account2.getCreatorId());
            assertNotEquals(account1.getDepartmentId(), account2.getDepartmentId());
            assertNotEquals(account1.getDepartment().getId(), account2.getDepartment().getId());
            assertNotEquals(account1.getDepartment().getName(), account2.getDepartment().getName());
            assertNotEquals(account1.getDepartment().getCreateDate(), account2.getDepartment().getCreateDate());
            assertEquals(account1.getDepartment().getCreatorId(), account2.getDepartment().getCreatorId());
            assertNotEquals(account1.getDepartment().getModifyDate(), account2.getDepartment().getModifyDate());
            assertEquals(account1.getDepartment().getModifierId(), account2.getDepartment().getModifierId());
        }
    }

    @Test
    public void case4() {
        // 查找列表
        var list = this.accounts.findBy(5L, 10L, Conditions.of(Account.class).like(Account::getName, "帐号%"), null);

        // 参数不变，因此查询结果相同
        var list2 = this.accounts.findBy(5L, 10L, Conditions.of(Account.class).like(Account::getName, "帐号%"), null);

        assertEquals(list.size(), list2.size());
        for (int i = 0; i < list.size(); i ++){
            var account1 = list.get(i);
            var account2 = list2.get(i);

            assertEquals(account1.getId(), account2.getId());
            assertEquals(account1.getName(), account2.getName());
            assertEquals(account1.getCreateDate(), account2.getCreateDate());
            assertEquals(account1.getCreatorId(), account2.getCreatorId());
            assertEquals(account1.getModifyDate(), account2.getModifyDate());
            assertEquals(account1.getModifierId(), account2.getCreatorId());
            assertEquals(account1.getDepartmentId(), account2.getDepartmentId());
            assertEquals(account1.getDepartment().getId(), account2.getDepartment().getId());
            assertEquals(account1.getDepartment().getName(), account2.getDepartment().getName());
            assertEquals(account1.getDepartment().getCreateDate(), account2.getDepartment().getCreateDate());
            assertEquals(account1.getDepartment().getCreatorId(), account2.getDepartment().getCreatorId());
            assertEquals(account1.getDepartment().getModifyDate(), account2.getDepartment().getModifyDate());
            assertEquals(account1.getDepartment().getModifierId(), account2.getDepartment().getModifierId());
        }

        // 参数变了，因此返回回来的结果也是不一样
        var list3 = this.accounts.findBy(5L, 10L, Conditions.of(Account.class).like(Account::getName, "帐号%"), Orders.empty());

        assertEquals(list.size(), list3.size());
        for (int i = 0; i < list.size(); i ++){
            var account1 = list.get(i);
            var account2 = list3.get(i);

            assertNotEquals(account1.getId(), account2.getId());
            assertNotEquals(account1.getName(), account2.getName());
            assertNotEquals(account1.getCreateDate(), account2.getCreateDate());
            assertEquals(account1.getCreatorId(), account2.getCreatorId());
            assertNotEquals(account1.getModifyDate(), account2.getModifyDate());
            assertEquals(account1.getModifierId(), account2.getCreatorId());
            assertNotEquals(account1.getDepartmentId(), account2.getDepartmentId());
            assertNotEquals(account1.getDepartment().getId(), account2.getDepartment().getId());
            assertNotEquals(account1.getDepartment().getName(), account2.getDepartment().getName());
            assertNotEquals(account1.getDepartment().getCreateDate(), account2.getDepartment().getCreateDate());
            assertEquals(account1.getDepartment().getCreatorId(), account2.getDepartment().getCreatorId());
            assertNotEquals(account1.getDepartment().getModifyDate(), account2.getDepartment().getModifyDate());
            assertEquals(account1.getDepartment().getModifierId(), account2.getDepartment().getModifierId());
        }

    }
}
