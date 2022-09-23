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

package central.starter.orm.test;

import central.starter.orm.TestApplication;
import central.starter.orm.data.entity.AccountEntity;
import central.starter.orm.data.mapper.AccountMapper;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 数据库测试
 * 其它的功能测试在 stdlib 里面已经测试过了，不重复测试
 *
 * @author Alan Yeh
 * @since 2022/09/22
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = TestApplication.class)
public class TestAccountMapper {

    @Setter(onMethod_ = @Autowired)
    private AccountMapper mapper;

    @Test
    public void case1() {
        var entity = new AccountEntity();
        entity.setUsername("test");
        entity.setName("测试帐户");
        entity.setAge(18);
        entity.updateCreator("sa");

        mapper.insert(entity);
        assertNotNull(entity.getId());

        var record = mapper.findById(entity.getId());
        assertNotNull(record);
        assertEquals(entity.getId(), record.getId());
        assertEquals(entity.getUsername(), record.getUsername());
        assertEquals(entity.getName(), record.getName());
        assertEquals(entity.getAge(), record.getAge());
    }
}
