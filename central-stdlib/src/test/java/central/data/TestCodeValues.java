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

package central.data;

import central.bean.CodeValues;
import central.util.Setx;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * CodeValues Test Cases
 *
 * @author Alan Yeh
 * @since 2022/07/05
 */
public class TestCodeValues {
    @Test
    public void case1() {
        var data = new CodeValues<String>();
        data.put("code1", "value1");
        data.put("code2", "value2");
        data.put("code3", "value3");
        data.put("code2", "value4");

        Assertions.assertEquals(3, data.size());
        Assertions.assertEquals(Setx.newHashSet("code1", "code3", "code2"), data.codeSet());
    }

    @Test
    public void case2(){
        var data = new CodeValues<String>();
        data.put("code1", "value1");
        data.put("code2", "value2");
        data.put("code1", "value3");

        Assertions.assertEquals(2, data.size());
        Assertions.assertEquals("value3", data.get("code1"));
    }

    @Test
    public void case3(){
        var data = new CodeValues<String>();
        data.put("code1", "value1");
        data.put("code2", "value2");
        data.put("code1", "value3");
        data.remove("code1");

        Assertions.assertEquals(1, data.size());
        Assertions.assertNull(data.get("code1"));
    }
}
