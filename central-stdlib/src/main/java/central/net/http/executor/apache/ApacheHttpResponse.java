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

package central.net.http.executor.apache;

import central.net.http.HttpRequest;
import central.net.http.HttpResponse;
import central.net.http.body.Body;
import central.net.http.body.InputStreamBody;
import central.net.http.body.ReusableBody;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

/**
 * Apache Http Response
 *
 * @author Alan Yeh
 * @since 2024/01/11
 */
public class ApacheHttpResponse extends HttpResponse {
    private final ClassicHttpResponse response;

    @Getter
    private final HttpHeaders headers = new HttpHeaders();

    @Getter
    private final  Body body;

    @SneakyThrows(IOException.class)
    public ApacheHttpResponse(HttpRequest request, ClassicHttpResponse response) {
        super(request);
        this.response = response;
        for (var header : response.getHeaders()){
            this.headers.add(header.getName(), header.getValue());
        }
        var cache = Files.createDirectories(Path.of("cache", "http"));
        this.body = new ReusableBody(new File(cache.toFile(), UUID.randomUUID() + ".tmp"),
                new InputStreamBody(response.getEntity().getContent(), this.headers));
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.resolve(this.response.getCode());
    }

}
