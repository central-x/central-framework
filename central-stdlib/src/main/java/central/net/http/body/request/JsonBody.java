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

import central.lang.reflect.TypeReference;
import central.net.http.body.Body;
import central.util.Jsonx;
import central.util.Stringx;
import org.springframework.http.MediaType;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Json Body
 * 主要用于传输 JSON
 *
 * @author Alan Yeh
 * @since 2022/07/14
 */
public class JsonBody implements Body {

    private final Map<String, Object> json = new HashMap<>();

    public JsonBody() {

    }

    public JsonBody(Object obj) {
        this.json.putAll(Jsonx.Default().deserialize(Jsonx.Default().serialize(obj), TypeReference.forMapType(String.class, Object.class)));
    }

    public JsonBody(Map<String, ?> map) {
        this.json.putAll(map);
    }

    public JsonBody(String json) {
        this.json.putAll(Jsonx.Default().deserialize(json, TypeReference.forMapType(String.class, Object.class)));
    }

    public JsonBody set(String name, Object value) {
        this.json.put(name, value);
        return this;
    }

    public JsonBody remove(String name) {
        this.json.remove(name);
        return this;
    }

    @Override
    public MediaType getContentType() {
        return new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8);
    }

    @Override
    public Long getContentLength() {
        String json = Jsonx.Default().serialize(this.json);
        byte[] data = json.getBytes(StandardCharsets.UTF_8);
        return (long) data.length;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        String json = Jsonx.Default().serialize(this.json);
        byte[] data = json.getBytes(StandardCharsets.UTF_8);
        return new ByteArrayInputStream(data);
    }

    @Override
    public String description() {
        return Stringx.format("(Json: {})", Jsonx.Default().serialize(this.json));
    }
}
