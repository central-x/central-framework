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

package central.util.function;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Throwable Function Test Cases
 *
 * @author Alan Yeh
 * @since 2022/07/11
 */
public class TestThrowableFunction {
    /**
     * o
     */
    @Test
    public void case1() {
        var function = ThrowableFunction.of((String str) -> Integer.parseInt(str)).ignoreThrows();

        Assertions.assertEquals(Integer.valueOf(1), function.apply("1"));
        Assertions.assertNull(function.apply("test"));
    }

    @Test
    public void case2() {
        var function = ThrowableFunction.of((String str) -> Integer.parseInt(str))
                .catchThrows(cause -> Integer.valueOf(2));

        Assertions.assertEquals(Integer.valueOf(1), function.apply("1"));
        Assertions.assertEquals(Integer.valueOf(2), function.apply("test"));
    }

    @Test
    public void case3() {
        var function = ThrowableFunction.of((String str) -> Integer.parseInt(str))
                .catchThrows(cause -> {
                    throw new IllegalArgumentException("Illegal string");
                });

        Assertions.assertEquals(Integer.valueOf(1), function.apply("1"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> function.apply("test"));
    }

    @Test
    public void test4() {
        var function = ThrowableFunction.of((String str) -> Integer.parseInt(str))
                .andThen((Integer value) -> {
                    Assertions.assertTrue(value instanceof Integer);
                    return value.toString();
                })
                .andThen((String value) -> {
                    Assertions.assertTrue(value instanceof String);
                    return Long.valueOf(value);
                })
                .andThen((Long value) -> {
                    Assertions.assertTrue(value instanceof Long);
                    return value.doubleValue();
                });

        Assertions.assertEquals(Double.valueOf(2), function.apply("2"));
        Assertions.assertThrows(NumberFormatException.class, () -> function.apply("test"));
    }

    @Test
    public void test5(){
        AtomicInteger countDown = new AtomicInteger(1);

        var function = ThrowableFunction.of((String str) -> Integer.parseInt(str))
                        .andThen((var value) -> {
                            Assertions.assertEquals(Integer.valueOf(1), value);
                            countDown.decrementAndGet();
                        }).ignoreThrows();

        function.accept("1");

        Assertions.assertEquals(0, countDown.get());
    }
}
