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

package central.lang.reflect;

import central.sql.data.Entity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * MethodSignature Test Cases
 *
 * @author Alan Yeh
 * @since 2023/05/01
 */
public class TestMethodSignature {

    @Test
    public void case1() throws Exception {
        var method = this.getClass().getMethod("test", int.class, String.class);
        var signature = new MethodSignature(method);
        assertEquals("java.lang.String test(int, java.lang.String)", signature.getSignature());
    }

    @Test
    public void case2() throws Exception {
        var signature = new MethodSignature(this.getClass().getMethod("test", int.class, String.class));

        var signature2 = new MethodSignature(TestClass.class.getMethod("test", int.class, String.class));
        assertEquals(signature, signature2);
    }

    @Test
    public void case3() throws Exception {
        var signature = new MethodSignature(this.getClass().getMethod("findById", String.class));
        assertEquals("central.sql.data.Entity findById(java.lang.String)", signature.getSignature());

        var signature2 = new MethodSignature(TestClass.class.getMethod("findById", String.class));
        assertEquals("central.sql.data.Entity findById(java.lang.String)", signature2.getSignature());
    }

    public String test(int arg1, String arg2) {
        return null;
    }

    public <E extends Entity> E findById(String id) {
        return null;
    }

    public static class TestClass<E extends Entity> {
        public String test(int arg1, String arg2) {
            return null;
        }

        public E findById(String id) {
            return null;
        }
    }
}
