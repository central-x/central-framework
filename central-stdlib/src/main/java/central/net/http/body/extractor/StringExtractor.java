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

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * 将响应体解析成字符串
 *
 * @author Alan Yeh
 * @since 2022/07/17
 */
public class StringExtractor implements BodyExtractor<String> {

    /**
     * 字符集
     */
    private final Charset charset;

    /**
     * 构造函数
     *
     * @param charset 字符集（如果未定指，将使用响应头里指定的字符集）
     */
    public StringExtractor(@Nullable Charset charset) {
        this.charset = charset;
    }

    /**
     * 创建字符串解析器
     */
    public static StringExtractor of() {
        return new StringExtractor(null);
    }

    /**
     * 创建字符串解析器
     *
     * @param charset 字符集（如果未定指，将使用响应头里指定的字符集）
     */
    public static StringExtractor of(@Nullable Charset charset) {
        return new StringExtractor(charset);
    }

    @Override
    public String extract(Body body) throws IOException {
        // 开发者指定的字符串
        var charset = this.charset;

        if (charset == null) {
            // 如果开发者没有指定字符集，则尝试从响应头获取字符集信息
            charset = body.getContentType().getCharset();
        }
        if (charset == null) {
            // 如果响应头没有指定字符集，则默认使用 UTF-8
            charset = StandardCharsets.UTF_8;
        }

        return IOStreamx.readText(body.getInputStream(), charset);
    }
}
