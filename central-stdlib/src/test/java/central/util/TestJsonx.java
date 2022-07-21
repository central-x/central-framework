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

package central.util;

import central.lang.reflect.TypeReference;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.HashMap;

/**
 * Jsonx Test Cases
 *
 * @author Alan Yeh
 * @since 2022/07/05
 */
public class TestJsonx {

    @Test
    public void case1() {
        var map = new HashMap<String, Object>();
        map.put("int", 1);
        map.put("string", "str");
        map.put("null", null);

        var json = Jsonx.Default().serialize(map);

        var result = Jsonx.Default().deserialize(json, TypeReference.forMapType(String.class, Object.class));

        Assertions.assertEquals(3, result.size());
        Assertions.assertEquals(1, result.get("int"));
        Assertions.assertEquals("str", result.get("string"));
        Assertions.assertNull(result.get("null"));
    }

    @Test
    public void case2(){
        var map = new HashMap<String, Object>();
        map.put("int", 1);
        map.put("string", "我是中国人");
        map.put("null", null);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Charset charset = Charset.forName("GB2312");
        Jsonx.Default().serialize(output, charset, map, false);

        var result = Jsonx.Default().deserialize(output.toString(charset), TypeReference.forMapType(String.class, Object.class));

        Assertions.assertEquals(3, result.size());
        Assertions.assertEquals(1, result.get("int"));
        Assertions.assertEquals("我是中国人", result.get("string"));
        Assertions.assertNull(result.get("null"));
    }
}
