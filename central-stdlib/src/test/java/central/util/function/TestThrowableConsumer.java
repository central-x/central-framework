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
 * ThrowableConsumer Test Cases
 *
 * @author Alan Yeh
 * @since 2022/07/11
 */
public class TestThrowableConsumer {
    @Test
    public void case1() {
        var lambda = ThrowableConsumer.of((String str) -> {
            if ("throws".equals(str)) {
                throw new IllegalArgumentException();
            } else {
                Assertions.assertEquals("test", str);
            }
        });

        lambda.accept("test");
        Assertions.assertThrows(IllegalArgumentException.class, () -> lambda.accept("throws"));
    }

    @Test
    public void case2() {
        AtomicInteger countDown = new AtomicInteger(1);

        ThrowableConsumer.of((String str) -> {
            countDown.decrementAndGet();
            Assertions.assertEquals("test", str);
        }).accept("test");
        Assertions.assertEquals(0, countDown.get());
    }
}
