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

import central.lang.Stringx;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

import java.io.IOException;

/**
 * 空 Body
 *
 * @author Alan Yeh
 * @since 2022/07/15
 */
public class EmptyBody extends RequestBody {

    private final MediaType contentType;

    public EmptyBody() {
        this.contentType = MediaType.parse("application/octet-stream");
    }

    public EmptyBody(String contentType) {
        if (Stringx.isNullOrBlank(contentType)) {
            this.contentType = MediaType.parse("application/octet-stream");
        } else {
            this.contentType = MediaType.parse(contentType);
        }
    }

    @Override
    public long contentLength() throws IOException {
        return 0;
    }

    @Override
    public @Nullable MediaType contentType() {
        return this.contentType;
    }

    @Override
    public void writeTo(@Nonnull BufferedSink bufferedSink) throws IOException {

    }
}
