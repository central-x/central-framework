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

package central.net.http.executor.okhttp;

import central.net.http.HttpRequest;
import central.net.http.HttpResponse;
import central.net.http.body.Body;
import lombok.Getter;
import okhttp3.Response;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.io.InputStream;

/**
 * OKHttp Response
 *
 * @author Alan Yeh
 * @since 2022/07/15
 */
public class OkHttpResponse extends HttpResponse {
    @Getter
    private final Response response;

    @Getter
    private final Body body;

    @Getter
    private final HttpHeaders headers = new HttpHeaders();

    public OkHttpResponse(HttpRequest request, Response response) {
        super(request);
        this.response = response;
        this.response.headers().forEach(header -> this.headers.add(header.getFirst(), header.getSecond()));
        this.body = new ResponseBody(this.headers, response.body());
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.resolve(response.code());
    }

    private static class ResponseBody implements Body {

        private final okhttp3.ResponseBody body;

        @Getter
        private final HttpHeaders headers;

        private ResponseBody(HttpHeaders headers, okhttp3.ResponseBody body) {
            this.headers = headers;
            this.body = body;
        }

        @Override
        public MediaType getContentType() {
            return this.headers.getContentType();
        }

        @Override
        public Long getContentLength() {
            return this.headers.getContentLength();
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return this.body.byteStream();
        }

        @Override
        public String description() {
            return "(binary)";
        }

        @Override
        public void close() throws Exception {
            this.body.close();
        }
    }
}
