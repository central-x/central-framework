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

import java.nio.charset.StandardCharsets;

/**
 * ThrowableSupplier Test Cases
 *
 * @author Alan Yeh
 * @since 2022/07/11
 */
public class TestThrowableSupplier {
    @Test
    public void case1() {
        Assertions.assertEquals("test", ThrowableSupplier.of(() -> "test").get());
    }

    @Test
    public void case2() {
        var test = ThrowableSupplier.of(() -> {
                    var str = "test";
                    if (str.getBytes(StandardCharsets.UTF_8).length > 1) {
                        throw new ClassCastException("Class Not Found");
                    }
                    return str;
                })
                .ignoreThrows()
                .get();
        Assertions.assertNull(test);
    }

    @Test
    public void case3() {
        var test = ThrowableSupplier.of(() -> {
                    var str = "test";
                    if (str.getBytes(StandardCharsets.UTF_8).length > 1) {
                        throw new ClassCastException("Class Not Found");
                    }
                    return str;
                })
                .catchThrows(cause -> "fallback")
                .get();
        Assertions.assertEquals("fallback", test);
    }

    @Test
    public void case4() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ThrowableSupplier.of(() -> {
                        var str = "test";
                        if (str.getBytes(StandardCharsets.UTF_8).length > 1) {
                            throw new ClassCastException("Class Not Found");
                        }
                        return str;
                    })
                    .catchThrows(cause -> {
                        throw new IllegalArgumentException(cause);
                    })
                    .get();
        });
    }

    @Test
    public void case5(){
        var result = ThrowableSupplier.of(() -> "1").andThen(Integer::parseInt);

        Assertions.assertEquals(Integer.valueOf(1), result.get());
    }
}
