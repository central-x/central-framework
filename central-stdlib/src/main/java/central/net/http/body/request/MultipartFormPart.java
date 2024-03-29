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
import central.net.http.body.InputStreamBody;
import central.lang.Assertx;
import central.util.Listx;
import central.io.IOStreamx;
import central.lang.Stringx;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * MultipartForm Part
 *
 * @author Alan Yeh
 * @since 2022/07/14
 */
public class MultipartFormPart implements Body {

    private static final byte[] COLONSPACE = {':', ' '};
    private static final byte[] CRLF = {'\r', '\n'};

    @Getter
    private final HttpHeaders headers;

    @Getter
    private final Body body;

    @Getter
    private final String name;

    public MultipartFormPart(String name, HttpHeaders headers, Body body) {
        if (headers == null) {
            headers = new HttpHeaders();
        }

        this.name = name;
        this.headers = headers;
        this.body = body;
    }


    @Override
    @SneakyThrows
    public Long getContentLength() {
        long contentLength = 0;

        InputStream stream = getHeaderInputStream();
        byte[] bytes = IOStreamx.readBytes(stream);
        contentLength += bytes.length;

        contentLength += CRLF.length;

        long bodyLength = this.body.getContentLength();
        if (bodyLength == -1) {
            return -1L;
        }

        contentLength += bodyLength;

        return contentLength;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        var streams = new ArrayList<InputStream>(3);

        streams.add(getHeaderInputStream());
        streams.add(new ByteArrayInputStream(CRLF));
        streams.add(this.body.getInputStream());

        return new SequenceInputStream(Collections.enumeration(streams));
    }

    @Override
    public String description() {
        return Stringx.format("{}={}", name, this.body.description());
    }

    private InputStream getHeaderInputStream() {
        var streams = new ArrayList<InputStream>();
        if (this.getContentType() != null) {
            this.headers.setContentType(this.getBody().getContentType());
        }

        for (String name : headers.keySet()) {
            List<String> values = headers.get(name);
            if (Listx.isNotEmpty(values)) {
                for (String value : values) {
                    streams.add(new ByteArrayInputStream(name.getBytes(StandardCharsets.UTF_8)));
                    streams.add(new ByteArrayInputStream(COLONSPACE));
                    streams.add(new ByteArrayInputStream(value.getBytes(StandardCharsets.UTF_8)));
                    streams.add(new ByteArrayInputStream(CRLF));
                }
            }
        }

        return new SequenceInputStream(Collections.enumeration(streams));
    }

    @Override
    public MediaType getContentType() {
        return this.body.getContentType();
    }

    public static MultipartFormPart create(String name, String body) {
        return create(name, body, MediaType.TEXT_PLAIN);
    }

    public static MultipartFormPart create(String name, String body, MediaType contentType) {
        Assertx.mustNotBlank(name, "Required parameter 'name' is missing");
        Assertx.mustNotNull(contentType, "Required parameter 'contentType' is missing");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.builder("form-data").name(name).build());

        if (Stringx.isNotBlank(body)) {
            return new MultipartFormPart(name, headers, new StringBody(body, contentType));
        } else {
            return new MultipartFormPart(name, headers, new RawBody(new byte[0], MediaType.TEXT_PLAIN));
        }

    }

    public static MultipartFormPart create(String name, File body) throws IOException {
        return create(name, body.getName(), body);
    }

    public static MultipartFormPart create(String name, String filename, File body) throws IOException {
        return create(name, filename, body, MediaType.APPLICATION_OCTET_STREAM);
    }

    public static MultipartFormPart create(String name, String filename, File body, MediaType contentType) throws IOException {
        Assertx.mustNotBlank(name, "Required parameter 'name' is missing");
        Assertx.mustNotNull(body, "Required parameter 'body' is missing");
        Assertx.mustNotNull(contentType, "Required parameter 'contentType' is missing");

        if (Stringx.isNullOrBlank(filename)) {
            filename = body.getName();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.builder("form-data").name(name).filename(filename).build());
        headers.setContentType(contentType);
        headers.setContentLength(body.length());

        return new MultipartFormPart(name, headers, new FileBody(body, contentType));
    }

    public static MultipartFormPart create(String name, String filename, InputStream body) throws IOException {
        return create(name, filename, body, -1);
    }

    public static MultipartFormPart create(String name, String filename, InputStream body, long contentLength) throws IOException {
        return create(name, filename, body, contentLength, MediaType.APPLICATION_OCTET_STREAM);
    }

    public static MultipartFormPart create(String name, String filename, InputStream body, long contentLength, MediaType contentType) throws IOException {
        Assertx.mustNotBlank(name, "Required parameter 'name' is missing");
        Assertx.mustNotBlank(filename, "Required parameter 'filename' is missing");
        Assertx.mustNotNull(body, "Required parameter 'body' is missing");
        Assertx.mustNotEquals(contentLength, 0, "Parameter 'contentLength' cannot be 0");
        Assertx.mustNotNull(contentType, "Required parameter 'contentType' is missing");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.builder("form-data").name(name).filename(filename).build());
        headers.setContentType(contentType);
        headers.setContentLength(contentLength);

        return new MultipartFormPart(name, headers, new InputStreamBody(body, contentLength, contentType));
    }

    public static MultipartFormPart create(String name, String filename, byte[] body) throws IOException {
        return create(name, filename, body, MediaType.APPLICATION_OCTET_STREAM);
    }

    public static MultipartFormPart create(String name, String filename, byte[] body, MediaType contentType) throws IOException {
        Assertx.mustNotBlank(name, "Required parameter 'name' is missing");
        Assertx.mustNotBlank(filename, "Required parameter 'filename' is missing");
        Assertx.mustNotNull(body, "Required parameter 'body' is missing");
        Assertx.mustNotNull(contentType, "Required parameter 'contentType' is missing");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.builder("form-data").name(name).filename(filename).build());
        headers.setContentType(contentType);
        headers.setContentLength(body.length);

        return new MultipartFormPart(name, headers, new ByteArrayBody(body, contentType));
    }
}
