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

package central.net.http.body.request;

import central.net.http.body.Body;
import central.lang.Stringx;
import org.springframework.http.MediaType;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * String Body
 * 用于传输字符串
 *
 * @author Alan Yeh
 * @since 2022/07/14
 */
public class StringBody implements Body {

    private final byte[] body;

    private final MediaType contentType;

    public StringBody(String body) {
        this(body, MediaType.TEXT_PLAIN);
    }

    public StringBody(String body, MediaType contentType) {
        this.body = body.getBytes(StandardCharsets.UTF_8);
        this.contentType = new MediaType(contentType, StandardCharsets.UTF_8);
    }

    @Override
    public MediaType getContentType() {
        return this.contentType;
    }

    @Override
    public Long getContentLength() {
        return (long) this.body.length;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(this.body);
    }

    @Override
    public String description() {
        return Stringx.format("(String: {})", new String(this.body, StandardCharsets.UTF_8));
    }
}