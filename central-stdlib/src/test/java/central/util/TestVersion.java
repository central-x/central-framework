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

import central.lang.CompareResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Version Test Cases
 *
 * @author Alan Yeh
 * @since 2022/07/13
 */
public class TestVersion {
    /**
     * 测试解析版本
     */
    @Test
    public void case1() {
        var version = new Version("1.2.3.45678");

        Assertions.assertEquals(1, version.getMajor());
        Assertions.assertEquals(2, version.getMinor());
        Assertions.assertEquals(3, version.getRevision());
        Assertions.assertEquals(45678, version.getBuild());
    }

    /**
     * 测试版本号对比
     */
    @Test
    public void case2() {
        var first = new Version("1.0.0");
        var second = new Version("1.0.2");

        Assertions.assertTrue(CompareResult.LT.isCompatibleWith(first.compareTo(second)));
    }
}
