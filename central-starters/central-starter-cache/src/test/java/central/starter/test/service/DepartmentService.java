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

package central.starter.test.service;

import central.sql.Conditions;
import central.sql.Orders;
import central.starter.cache.core.annotation.CacheEvict;
import central.starter.cache.core.annotation.CacheKey;
import central.starter.cache.core.annotation.Cacheable;
import central.starter.test.service.data.Department;
import central.util.Guidx;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/**
 * Department
 *
 * @author Alan Yeh
 * @since 2022/11/15
 */
@Component
public class DepartmentService {

    private final AtomicInteger index = new AtomicInteger();

    @Cacheable(key = "department:id:${args[0]}")
    public Department findById(String id) {
        var department = new Department();
        department.setId(id);
        department.setName("部门 " + index.getAndIncrement());
        department.updateCreator("syssa");
        return department;
    }

    @Cacheable(key = "department:findBy:${sign(args)}", dependencies = "department:id:any")
    public List<Department> findBy(Long first, Long offset, Conditions<Department> conditions, Orders<Department> orders) {
        return IntStream.range(index.incrementAndGet(), index.get() + 10).mapToObj(it -> {
            var department = new Department();
            department.setId(Guidx.nextID());
            department.setName("部门 " + it);
            department.updateCreator("syssa");
            return department;
        }).toList();
    }

    @CacheEvict(keys = @CacheKey(key = "department:id:${it}", it = "args[0]"))
    @CacheEvict(key = "department:id:any")
    public long deleteByIds(List<String> ids) {
        return 1;
    }
}
