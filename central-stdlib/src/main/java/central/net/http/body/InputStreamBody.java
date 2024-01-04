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

package central.net.http.body;

import lombok.Getter;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.io.InputStream;

/**
 * InputStream Body
 * 主要用于传输数据流
 *
 * @author Alan Yeh
 * @since 2022/07/14
 */
public class InputStreamBody implements Body {

    @Getter
    private final Long contentLength;

    @Getter
    private final MediaType contentType;

    private final InputStream body;

    public InputStreamBody(InputStream body) {
        this(body, -1, MediaType.APPLICATION_OCTET_STREAM);
    }

    public InputStreamBody(InputStream body, long contentLength, MediaType contentType) {
        this.body = body;
        this.contentLength = contentLength;
        this.contentType = contentType;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return this.body;
    }

    @Override
    public String description() {
        return "<InputStream>";
    }

    @Override
    public void close() throws Exception {
        this.body.close();
    }
}
