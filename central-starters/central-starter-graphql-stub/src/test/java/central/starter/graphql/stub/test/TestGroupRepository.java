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

package central.starter.graphql.stub.test;

import central.sql.Conditions;
import central.starter.graphql.stub.TestApplication;
import central.starter.graphql.stub.test.data.Group;
import central.starter.graphql.stub.test.stub.GroupRepository;
import central.util.Listx;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Repository Stub Test Cases
 *
 * @author Alan Yeh
 * @since 2022/09/25
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = TestApplication.class)
public class TestGroupRepository {

    @Setter(onMethod_ = @Autowired)
    private GroupRepository repository;

    @Test
    public void case1() {
        var result = this.repository.findBy(null, null, Conditions.where().eq(Group::getName, "spring"), null);

        assertNotNull(result);
        assertTrue(Listx.isNotEmpty(result));
        assertEquals(1, result.size());

        var group = result.get(0);
        assertInstanceOf(Group.class, group);
        assertEquals("spring", group.getName());
        assertTrue(Listx.isNotEmpty(group.getProjects()));
        assertEquals(3, group.getProjects().size());
        assertTrue(group.getProjects().stream().anyMatch(it -> "spring-framework".equals(it.getName())));
        assertTrue(group.getProjects().stream().anyMatch(it -> "spring-boot".equals(it.getName())));
        assertTrue(group.getProjects().stream().anyMatch(it -> "spring-cloud".equals(it.getName())));
    }
}
