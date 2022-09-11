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

package central.starter.graphql.data;

import central.starter.graphql.data.entity.AccountEntity;
import central.starter.graphql.data.entity.DeptEntity;
import lombok.Getter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 模拟数据源
 *
 * @author Alan Yeh
 * @since 2022/09/09
 */
@Component
public class Db implements InitializingBean {
    @Getter
    private final List<AccountEntity> accounts = new ArrayList<>();
    @Getter
    private final List<DeptEntity> depts = new ArrayList<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        DeptEntity dept1 = new DeptEntity();
        dept1.setId("dept1");
        dept1.setName("开发一部");
        depts.add(dept1);

        DeptEntity dept2 = new DeptEntity();
        dept2.setId("dept2");
        dept2.setName("开发二部");
        depts.add(dept2);

        AccountEntity account1 = new AccountEntity();
        account1.setId("1");
        account1.setDeptId("dept1");
        account1.setName("刘一");
        accounts.add(account1);

        AccountEntity account2 = new AccountEntity();
        account2.setId("2");
        account2.setDeptId("dept2");
        account2.setName("陈二");
        accounts.add(account2);

        AccountEntity account3 = new AccountEntity();
        account3.setId("3");
        account3.setDeptId("dept1");
        account3.setName("张三");
        accounts.add(account3);

        AccountEntity account4 = new AccountEntity();
        account4.setId("4");
        account4.setDeptId("dept1");
        account4.setName("李四");
        accounts.add(account4);

        AccountEntity account5 = new AccountEntity();
        account5.setId("5");
        account5.setDeptId("dept1");
        account5.setName("王五");
        accounts.add(account5);

        AccountEntity account6 = new AccountEntity();
        account6.setId("6");
        account6.setDeptId("dept2");
        account6.setName("赵六");
        accounts.add(account6);

        AccountEntity account7 = new AccountEntity();
        account7.setId("7");
        account7.setDeptId("dept1");
        account7.setName("孙七");
        accounts.add(account7);

        AccountEntity account8 = new AccountEntity();
        account8.setId("8");
        account8.setDeptId("dept1");
        account8.setName("周八");
        accounts.add(account8);

        AccountEntity account9 = new AccountEntity();
        account9.setId("9");
        account9.setDeptId("dept2");
        account9.setName("吴九");
        accounts.add(account9);

        AccountEntity account10 = new AccountEntity();
        account10.setId("10");
        account10.setDeptId("dept1");
        account10.setName("郑十");
        accounts.add(account10);
    }
}
