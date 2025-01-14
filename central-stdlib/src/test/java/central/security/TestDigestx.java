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

package central.security;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

/**
 * Digestx Test Cases
 *
 * @author Alan Yeh
 * @since 2022/07/05
 */
public class TestDigestx {

    /**
     * @see <a href="https://www.sojson.com/hash.html">sojson</a>
     */
    @Test
    public void case1() {
        var license = """
                Apache License
                Version 2.0, January 2004
                https://www.apache.org/licenses/
                """.trim();
        var md5 = Digestx.MD5.digest(license, StandardCharsets.UTF_8);
        Assertions.assertEquals("f47b4a6c4dfad9403f3887ee23bc92a5", md5);
    }

    /**
     * @see <a href="https://www.sojson.com/hash.html">sojson</a>
     */
    @Test
    public void case2() {
        var license = """
                Apache License
                Version 2.0, January 2004
                https://www.apache.org/licenses/
                """.trim();
        var sha1 = Digestx.SHA1.digest(license, StandardCharsets.UTF_8);
        Assertions.assertEquals("bb58cd273ae5891b8cdf8489beccc4ecea74e69d", sha1);
    }

    /**
     * @see <a href="https://www.sojson.com/hash.html">sojson</a>
     */
    @Test
    public void case3() {
        var license = """
                Apache License
                Version 2.0, January 2004
                https://www.apache.org/licenses/
                """.trim();
        var sha224 = Digestx.SHA224.digest(license, StandardCharsets.UTF_8);
        Assertions.assertEquals("dc9431186457bafb669ea65b64e7d940b9bfa18e63d86f6916c8a999", sha224);
    }

    /**
     * @see <a href="https://www.sojson.com/hash.html">sojson</a>
     */
    @Test
    public void case4() {
        var license = """
                Apache License
                Version 2.0, January 2004
                https://www.apache.org/licenses/
                """.trim();
        var sha256 = Digestx.SHA256.digest(license, StandardCharsets.UTF_8);
        Assertions.assertEquals("3a51db0133a4765c48cafd109dad00187fa5066d5a9fd8ca6fc81a2fc6c18f20", sha256);
    }

    /**
     * @see <a href="https://www.sojson.com/hash.html">sojson</a>
     */
    @Test
    public void case5() {
        var license = """
                Apache License
                Version 2.0, January 2004
                https://www.apache.org/licenses/
                """.trim();
        var sha384 = Digestx.SHA384.digest(license, StandardCharsets.UTF_8);
        Assertions.assertEquals("7ee3b84c3b10e3e7c00820c39e9b0facaf252af202b5e6c9ddbb77298c9d1a7daebefb2096198d208d21151de347208f", sha384);
    }

    /**
     * @see <a href="https://www.sojson.com/hash.html">sojson</a>
     */
    @Test
    public void case6() {
        var license = """
                Apache License
                Version 2.0, January 2004
                https://www.apache.org/licenses/
                """.trim();
        var sha512 = Digestx.SHA512.digest(license, StandardCharsets.UTF_8);
        Assertions.assertEquals("5028e08da96818a4fabb774e47af78251c7eced6115a5657fffcb83df606db9e1bfccb91a8450c1fc9b4bec527faebe11927b458c1349c130f4e545471302a3b", sha512);
    }

    @Test
    public void case7() {
        var license = """
                Apache License
                Version 2.0, January 2004
                https://www.apache.org/licenses/
                """.trim();
        var sm3 = Digestx.SM3.digest(license, StandardCharsets.UTF_8);
        Assertions.assertEquals("5979c256bcdfa4b3764ffb70afa22298ac514cb426a2852151120309ee78c4a5", sm3);
    }
}
