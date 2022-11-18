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

import central.io.IOStreamx;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPOutputStream;

/**
 * 压缩请求体
 *
 * @author Alan Yeh
 * @since 2022/07/14
 */
@RequiredArgsConstructor
public class CompressedBody implements Body {
    private final Body delegate;

    /**
     * 压缩类型
     */
    private final CompressType type;

    @Override
    public HttpHeaders getHeaders() {
        // 修改请求头
        HttpHeaders headers = delegate.getHeaders();
        headers.remove(HttpHeaders.CONTENT_LENGTH);
        headers.remove(HttpHeaders.CONTENT_ENCODING);

        headers.set(HttpHeaders.CONTENT_ENCODING, this.type.getName());
        return headers;
    }

    @Override
    public MediaType getContentType() {
        return this.delegate.getContentType();
    }

    @Override
    public Long getContentLength() {
        // 因为消息体被压缩，因此不知道有多长
        return -1L;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        switch (this.type) {
            case GZIP -> {
                try (GZIPOutputStream gzip = new GZIPOutputStream(out)) {
                    gzip.write(IOStreamx.readBytes(this.delegate.getInputStream()));
                    gzip.flush();
                }
            }
            case DEFLATE -> {
                try (DeflaterOutputStream deflate = new DeflaterOutputStream(out)) {
                    deflate.write(IOStreamx.readBytes(this.delegate.getInputStream()));
                    deflate.flush();
                }
            }
            default -> {
                throw new RuntimeException("不支持的压缩类型: " + this.type.getName() + "(" + this.type.getValue() + ")");
            }
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    @Override
    public String description() {
        return "Compressed(" + delegate.description() + ")";
    }

    @Override
    public void close() throws Exception {
        this.delegate.close();
    }
}