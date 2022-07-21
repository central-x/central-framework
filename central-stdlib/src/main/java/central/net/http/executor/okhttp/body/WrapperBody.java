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

package central.net.http.executor.okhttp.body;

import central.net.http.body.Body;
import central.io.IOStreamx;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;
import okio.Okio;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/**
 * 请求体包装
 *
 * @author Alan Yeh
 * @since 2022/07/15
 */
public class WrapperBody extends RequestBody {
    private final Body body;

    public WrapperBody(Body body) {
        this.body = body;
    }

    @Override
    public long contentLength() throws IOException {
        return body.getContentLength();
    }

    @Nullable
    @Override
    public MediaType contentType() {
        return MediaType.parse(this.body.getContentType().toString());
    }

    @Override
    public void writeTo(@NotNull BufferedSink bufferedSink) throws IOException {
        var contentLength = this.contentLength();
        if (contentLength == -1) {
            // 未知长度，一直读取到流结束
            try (var source = Okio.source(this.body.getInputStream())) {
                bufferedSink.writeAll(source);
            }
        } else if (contentLength > 0) {
            // 只读取指定长度的流
            var length = contentLength;
            var bytesRead = 0;
            var buffer = new byte[IOStreamx.BUFFER_SIZE];
            try (var stream = IOStreamx.buffered(this.body.getInputStream())) {
                do {
                    bytesRead = stream.read(buffer, 0, (int) Math.min(length, IOStreamx.BUFFER_SIZE));

                    if (bytesRead > 0) {
                        bufferedSink.write(buffer, 0, bytesRead);
                    }

                    length -= bytesRead;
                } while (length > 0 && bytesRead > 0);
            }
        }
        // 长度为 0，不处理
    }
}
