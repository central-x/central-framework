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
                https://www.apache.org/licenses/""";
        var md5 = Digestx.MD5.digest(license, StandardCharsets.UTF_8);
        Assertions.assertEquals("e559488e7e6921b99a332eccdc1df651", md5);
    }

    /**
     * @see <a href="https://www.sojson.com/hash.html">sojson</a>
     */
    @Test
    public void case2() {
        var license = """
                Apache License
                Version 2.0, January 2004
                https://www.apache.org/licenses/""";
        var sha1 = Digestx.SHA1.digest(license, StandardCharsets.UTF_8);
        Assertions.assertEquals("579a853498e568818965f24db5ca8eed437e65a8", sha1);
    }

    /**
     * @see <a href="https://www.sojson.com/hash.html">sojson</a>
     */
    @Test
    public void case3() {
        var license = """
                Apache License
                Version 2.0, January 2004
                https://www.apache.org/licenses/""";
        var sha224 = Digestx.SHA224.digest(license, StandardCharsets.UTF_8);
        Assertions.assertEquals("2f6cf8007e537319b85bec812da0724bbdc414fb8b1eaec610fa851d", sha224);
    }

    /**
     * @see <a href="https://www.sojson.com/hash.html">sojson</a>
     */
    @Test
    public void case4() {
        var license = """
                Apache License
                Version 2.0, January 2004
                https://www.apache.org/licenses/""";
        var sha256 = Digestx.SHA256.digest(license, StandardCharsets.UTF_8);
        Assertions.assertEquals("b8f382ba452e7a924f0eb9b8a48ed63065b5e6305fa88b89a93763c8e6a76ea5", sha256);
    }

    /**
     * @see <a href="https://www.sojson.com/hash.html">sojson</a>
     */
    @Test
    public void case5() {
        var license = """
                Apache License
                Version 2.0, January 2004
                https://www.apache.org/licenses/""";
        var sha384 = Digestx.SHA384.digest(license, StandardCharsets.UTF_8);
        Assertions.assertEquals("6cf14672cb9d55841a2a650c3f7c86aa0c39ed7d5ed1e58126f787dffdce7efa46cd0f685df35d8184365a42365cc7a9", sha384);
    }

    /**
     * @see <a href="https://www.sojson.com/hash.html">sojson</a>
     */
    @Test
    public void case6() {
        var license = """
                Apache License
                Version 2.0, January 2004
                https://www.apache.org/licenses/""";
        var sha512 = Digestx.SHA512.digest(license, StandardCharsets.UTF_8);
        Assertions.assertEquals("1bddeb510f5db83fea436371999f265c625faa1c554270bc22ed5e27f1a3ea753ae8849c24454c82786dc481a65736f535de16becc68d9bcb44b560ad227cfac", sha512);
    }

    @Test
    public void case7() {
        var license = """
                Apache License
                Version 2.0, January 2004
                https://www.apache.org/licenses/""";
        var sm3 = Digestx.SM3.digest(license, StandardCharsets.UTF_8);
        Assertions.assertEquals("2213e92e1a7d3e11f6c267127f1d72f844414eedd8709ad50aca0520295562da", sm3);
    }
}
