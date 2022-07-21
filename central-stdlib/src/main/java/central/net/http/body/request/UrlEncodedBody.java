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
import central.util.Objectx;
import central.util.Stringx;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * Url Encoded Body
 * 用于提交键值对表单
 *
 * @author Alan Yeh
 * @since 2022/07/14
 */
public class UrlEncodedBody implements Body {

    @Getter
    private final MultiValueMap<String, String> body = new LinkedMultiValueMap<>();

    public UrlEncodedBody() {
    }

    public UrlEncodedBody(MultiValueMap<String, String> body) {
        this.body.addAll(body);
    }

    // 属性反射
    @SneakyThrows
    public UrlEncodedBody(Object instance) {
        if (instance == null) {

        } else if (instance instanceof Map<?, ?> map) {
            for (Map.Entry<?, ?> it : map.entrySet()) {
                this.body.set(it.getKey().toString(), Objectx.toString(it.getValue()));
            }
        } else if (instance instanceof List<?>) {
            throw new IllegalArgumentException(Stringx.format("Unsupported type '{}' for UrlEncodedBody", List.class.getName()));
        } else {
            // 反射获取所有属性
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
                if (value instanceof List<?> values) {
                    for (Object val : values) {
                        if (!HttpConverters.Default().support(val)) {
                            throw new IllegalArgumentException(Stringx.format("Unsupported value type '{}", val.getClass().getName()));
                        }

                        this.body.add(name, HttpConverters.Default().convert(val));
                    }
                } else {
                    if (!HttpConverters.Default().support(value)) {
                        throw new IllegalArgumentException(Stringx.format("Unsupported value type '{}", value.getClass().getName()));
                    }
                    this.body.add(name, HttpConverters.Default().convert(value));
                }
            }
        }
    }

    // 键值对
    public UrlEncodedBody(Map<String, ?> map) {
        for (Map.Entry<String, ?> it : map.entrySet()) {
            Object value = it.getValue();
            if (!HttpConverters.Default().support(value)) {
                throw new IllegalArgumentException(Stringx.format("Unsupported value type '{}'", value.getClass().getName()));
            }
            this.body.set(it.getKey(), HttpConverters.Default().convert(value));
        }
    }

    public UrlEncodedBody set(String name, String value) {
        this.content = null;
        this.body.set(name, value);
        return this;
    }

    public UrlEncodedBody set(String name, List<String> value) {
        this.content = null;
        this.body.put(name, value);
        return this;
    }

    public UrlEncodedBody add(String name, String value) {
        this.content = null;
        this.body.add(name, value);
        return this;
    }

    public UrlEncodedBody add(String name, List<String> value) {
        this.content = null;
        this.body.addAll(name, value);
        return this;
    }

    public UrlEncodedBody add(String name, Object value) {
        if (!HttpConverters.Default().support(value)) {
            throw new IllegalArgumentException(Stringx.format("Unsupported value type '{}'", value.getClass().getName()));
        }
        this.content = null;
        this.body.add(name, HttpConverters.Default().convert(value));
        return this;
    }

    private transient String content;

    public String getContent() {
        if (content == null) {
            StringBuilder builder = new StringBuilder();

            for (Map.Entry<String, List<String>> entry : this.body.entrySet()) {
                for (String value : entry.getValue()) {
                    if (builder.length() > 0) {
                        builder.append('&');
                    }
                    builder.append(Stringx.encodeUrl(entry.getKey())).append('=');
                    if (value != null) {
                        builder.append(Stringx.encodeUrl(value));
                    }
                }
            }

            this.content = builder.toString();
        }

        return content;
    }

    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(this.getContent().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public Long getContentLength() {
        return (long) this.getContent().getBytes(StandardCharsets.UTF_8).length;
    }

    @Override
    public MediaType getContentType() {
        return MediaType.APPLICATION_FORM_URLENCODED;
    }

    @Override
    public String description() {
        return Stringx.format("(UrlEncodedBody: {})", getContent());
    }
}
