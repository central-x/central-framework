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

import central.sql.data.Account;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Conditions Test Cases
 *
 * @author Alan Yeh
 * @since 2022/08/02
 */
public class TestConditions {

    /**
     * Test Operators
     */
    @Test
    public void case1(){
        var conditions = Conditions.where().eq(Account::getAge, 18);
        assertEquals(conditions.toSql(), "age = 18");

        conditions = Conditions.where().ne(Account::getAge, 18);
        assertEquals(conditions.toSql(), "age <> 18");

        conditions = Conditions.where().gt(Account::getAge, 18);
        assertEquals(conditions.toSql(), "age > 18");

        conditions = Conditions.where().ge(Account::getAge, 18);
        assertEquals(conditions.toSql(), "age >= 18");

        conditions = Conditions.where().lt(Account::getAge, 18);
        assertEquals(conditions.toSql(), "age < 18");

        conditions = Conditions.where().le(Account::getAge, 18);
        assertEquals(conditions.toSql(), "age <= 18");

        conditions = Conditions.where().between(Account::getAge, 18, 28);
        assertEquals(conditions.toSql(), "age BETWEEN 18 AND 28");

        conditions = Conditions.where().notBetween(Account::getAge, 18, 28);
        assertEquals(conditions.toSql(), "age NOT BETWEEN 18 AND 28");

        conditions = Conditions.where().like(Account::getName, "%Yeh");
        assertEquals(conditions.toSql(), "name LIKE %Yeh");

        conditions = Conditions.where().notLike(Account::getName, "%Yeh");
        assertEquals(conditions.toSql(), "name NOT LIKE %Yeh");

        conditions = Conditions.where().isNull(Account::getName);
        assertEquals(conditions.toSql(), "name IS NULL");

        conditions = Conditions.where().isNotNull(Account::getName);
        assertEquals(conditions.toSql(), "name IS NOT NULL");

        conditions = Conditions.where().in(Account::getAge, 18, 19, 20, 21);
        assertEquals(conditions.toSql(), "age IN (" + Stream.of(18, 19, 20, 21).map(String::valueOf).collect(Collectors.joining(", ")) + ")");

        conditions = Conditions.where().notIn(Account::getAge, 18, 19, 20, 21);
        assertEquals(conditions.toSql(), "age NOT IN (" + Stream.of(18, 19, 20, 21).map(String::valueOf).collect(Collectors.joining(", ")) + ")");
    }

    /**
     * Test AND
     */
    @Test
    public void case2(){
        var conditions = Conditions.where().eq(Account::getAge, 18).eq(Account::getName, "张三");

        assertEquals(conditions.toSql(), "age = 18 AND name = 张三");
    }

    /**
     * Test OR
     */
    @Test
    public void case3(){
        var conditions = Conditions.where().eq(Account::getAge, 18).or().eq(Account::getAge, 19);

        assertEquals(conditions.toSql(), "age = 18 OR age = 19");
    }

    /**
     * Test Nest
     */
    @Test
    public void case4(){
        var conditions = Conditions.where().eq(Account::getAge, 18).and(filter -> {
            filter.eq(Account::getName, "张三").or().eq(Account::getName, "李四");
        });

        assertEquals(conditions.toSql(), "age = 18 AND (name = 张三 OR name = 李四)");
    }
}
