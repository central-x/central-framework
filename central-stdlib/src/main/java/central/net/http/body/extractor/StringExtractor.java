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

package central.net.http.body.extractor;

import central.io.IOStreamx;
import central.net.http.body.Body;
import central.net.http.body.BodyExtractor;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * String 序列化
 *
 * @author Alan Yeh
 * @since 2022/07/17
 */
public class StringExtractor implements BodyExtractor<String> {

    private final Charset charset;

    public StringExtractor() {
        this.charset = StandardCharsets.UTF_8;
    }

    public StringExtractor(Charset charset) {
        this.charset = charset;
    }

    public static StringExtractor of() {
        return new StringExtractor();
    }

    public static StringExtractor of(Charset charset) {
        return new StringExtractor(charset);
    }

    @Override
    public String extract(Body body) throws IOException {
        return IOStreamx.readText(body.getInputStream(), this.charset);
    }
}
