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
import central.net.http.body.HttpConverters;
import central.util.Arrayx;
import central.util.Guidx;
import central.util.Mapx;
import central.util.Stringx;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.http.MediaType;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Multipart Form Body
 * 用于提交带文件的表单
 *
 * @author Alan Yeh
 * @since 2022/07/14
 */
public class MultipartFormBody implements Body {

    private static final byte[] CRLF = {'\r', '\n'};
    private static final byte[] DASHDASH = {'-', '-'};

    private final byte[] boundary = ("----------" + Guidx.nextID()).getBytes(StandardCharsets.UTF_8);

    @Getter
    private final List<MultipartFormPart> body = new ArrayList<>();

    public MultipartFormBody() {

    }

    public MultipartFormBody(Map<String, ?> map) {
        initMap(map);
    }

    @SneakyThrows
    public MultipartFormBody(Object instance) {
        if (instance == null) {

        } else if (instance instanceof Map<?, ?>) {
            this.initMap((Map<?, ?>) instance);
        } else if (instance instanceof List<?>) {
            throw new IllegalArgumentException(Stringx.format("Unsupported value type '{}'", List.class.getName()));
        } else {
            BeanInfo info = Introspector.getBeanInfo(instance.getClass());
            PropertyDescriptor[] properties = info.getPropertyDescriptors();

            if (Arrayx.isNullOrEmpty(properties)) {
                throw new IllegalArgumentException(Stringx.format("Fail to get properties from value type '{}'", instance.getClass().getName()));
            }

            for (PropertyDescriptor property : properties) {
                if (property.getReadMethod() == null || property.getWriteMethod() == null) {
                    continue;
                }

                String name = property.getName();
                Object value = property.getReadMethod().invoke(instance);
                if (value instanceof File file) {
                    this.add(MultipartFormPart.create(name, file.getName(), file));
                } else {
                    if (!HttpConverters.Default().support(value.getClass())) {
                        throw new IllegalArgumentException(Stringx.format("Unsupported value type '{}", value.getClass().getName()));
                    }
                    this.add(MultipartFormPart.create(name, HttpConverters.Default().convert(value)));
                }
            }
        }
    }

    @SneakyThrows
    private void initMap(Map<?, ?> map) {
        for (Map.Entry<?, ?> it : map.entrySet()) {
            String name = it.getKey().toString();
            Object value = it.getValue();

            if (value instanceof File file) {
                this.add(MultipartFormPart.create(name, file.getName(), file));
            } else {
                if (!HttpConverters.Default().support(value)) {
                    throw new IllegalArgumentException(Stringx.format("Unsupported value type '{}", value.getClass().getName()));
                }
                this.add(MultipartFormPart.create(name, HttpConverters.Default().convert(value)));
            }
        }
    }

    public MultipartFormBody add(MultipartFormPart part) {
        this.body.add(part);
        return this;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        Vector<InputStream> streams = new Vector<>();

        for (MultipartFormPart part : this.body) {
            // --{boundary}
            streams.add(new ByteArrayInputStream(DASHDASH));
            streams.add(new ByteArrayInputStream(boundary));
            streams.add(new ByteArrayInputStream(CRLF));

            streams.add(part.getInputStream());

            streams.add(new ByteArrayInputStream(CRLF));
        }

        // --{boundary}--
        streams.add(new ByteArrayInputStream(DASHDASH));
        streams.add(new ByteArrayInputStream(boundary));
        streams.add(new ByteArrayInputStream(DASHDASH));
        streams.add(new ByteArrayInputStream(CRLF));

        return new SequenceInputStream(streams.elements());
    }

    @Override
    public Long getContentLength() {
        long contentLength = 0;

        for (MultipartFormPart part : this.body) {
            contentLength += DASHDASH.length;
            contentLength += boundary.length;
            contentLength += CRLF.length;

            long partLength = part.getContentLength();
            if (partLength == -1L) {
                return -1L;
            }
            contentLength += partLength;

            contentLength += CRLF.length;
        }

        contentLength += DASHDASH.length;
        contentLength += boundary.length;
        contentLength += DASHDASH.length;
        contentLength += CRLF.length;
        return contentLength;
    }

    @Override
    public MediaType getContentType() {
        return new MediaType(MediaType.MULTIPART_FORM_DATA, Mapx.newHashMap("boundary", new String(this.boundary, StandardCharsets.UTF_8)));
    }

    @Override
    public String description() {
        StringBuilder builder = new StringBuilder();
        for (MultipartFormPart part : this.body) {
            if (builder.length() > 0) {
                builder.append("&");
            }
            builder.append(part.description());
        }
        return Stringx.format("(MultipartForm: {})", builder);
    }

    @Override
    public void close() throws Exception {
        for (MultipartFormPart part : this.body) {
            part.close();
        }
    }
}
