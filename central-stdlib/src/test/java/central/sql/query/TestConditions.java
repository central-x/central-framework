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

package central.sql.query;

import central.sql.data.AccountEntity;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        var conditions = Conditions.of(AccountEntity.class).eq(AccountEntity::getAge, 18);
        assertEquals("age = 18", conditions.toSql());

        conditions = Conditions.of(AccountEntity.class).ne(AccountEntity::getAge, 18);
        assertEquals("age <> 18", conditions.toSql());

        conditions = Conditions.of(AccountEntity.class).gt(AccountEntity::getAge, 18);
        assertEquals("age > 18", conditions.toSql());

        conditions = Conditions.of(AccountEntity.class).ge(AccountEntity::getAge, 18);
        assertEquals("age >= 18", conditions.toSql());

        conditions = Conditions.of(AccountEntity.class).lt(AccountEntity::getAge, 18);
        assertEquals("age < 18", conditions.toSql());

        conditions = Conditions.of(AccountEntity.class).le(AccountEntity::getAge, 18);
        assertEquals("age <= 18", conditions.toSql());

        conditions = Conditions.of(AccountEntity.class).between(AccountEntity::getAge, 18, 28);
        assertEquals("age BETWEEN 18 AND 28", conditions.toSql());

        conditions = Conditions.of(AccountEntity.class).notBetween(AccountEntity::getAge, 18, 28);
        assertEquals("age NOT BETWEEN 18 AND 28", conditions.toSql());

        conditions = Conditions.of(AccountEntity.class).like(AccountEntity::getName, "%Yeh");
        assertEquals("name LIKE %Yeh", conditions.toSql());

        conditions = Conditions.of(AccountEntity.class).notLike(AccountEntity::getName, "%Yeh");
        assertEquals("name NOT LIKE %Yeh", conditions.toSql());

        conditions = Conditions.of(AccountEntity.class).isNull(AccountEntity::getName);
        assertEquals("name IS NULL", conditions.toSql());

        conditions = Conditions.of(AccountEntity.class).isNotNull(AccountEntity::getName);
        assertEquals("name IS NOT NULL", conditions.toSql());

        conditions = Conditions.of(AccountEntity.class).in(AccountEntity::getAge, 18, 19, 20, 21);
        assertEquals(conditions.toSql(), "age IN (" + Stream.of(18, 19, 20, 21).map(String::valueOf).collect(Collectors.joining(", ")) + ")");

        conditions = Conditions.of(AccountEntity.class).notIn(AccountEntity::getAge, 18, 19, 20, 21);
        assertEquals(conditions.toSql(), "age NOT IN (" + Stream.of(18, 19, 20, 21).map(String::valueOf).collect(Collectors.joining(", ")) + ")");
    }

    /**
     * Test AND
     */
    @Test
    public void case2(){
        var conditions = Conditions.of(AccountEntity.class).eq(AccountEntity::getAge, 18).eq(AccountEntity::getName, "张三");

        assertEquals("age = 18 AND name = 张三", conditions.toSql());
    }

    /**
     * Test OR
     */
    @Test
    public void case3(){
        var conditions = Conditions.of(AccountEntity.class).eq(AccountEntity::getAge, 18).or().eq(AccountEntity::getAge, 19);

        assertEquals("age = 18 OR age = 19", conditions.toSql());
    }

    /**
     * Test Nest
     */
    @Test
    public void case4(){
        var conditions = Conditions.of(AccountEntity.class).eq(AccountEntity::getAge, 18).and(filter -> {
            filter.eq(AccountEntity::getName, "张三").or().eq(AccountEntity::getName, "李四");
        });

        assertEquals("age = 18 AND (name = 张三 OR name = 李四)", conditions.toSql());
    }
}
