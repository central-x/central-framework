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

package central.net.http;

import central.util.Listx;
import central.util.Mapx;
import central.lang.Stringx;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Http Url
 *
 * @author Alan Yeh
 * @since 2022/07/14
 */
public class HttpUrl {
    @Getter
    private String baseUrl;

    @Setter
    protected String value;

    public HttpUrl(String value) {
        this.value = value;
    }


    public static HttpUrl of(String value) {
        return new HttpUrl(value);
    }

    public static HttpUrl ofImmutable(String value) {
        return new ImmutableHttpUrl(value);
    }

    /**
     * Query 参数
     */
    @Getter
    private final MultiValueMap<String, String> query = new LinkedMultiValueMap<>();

    /**
     * URL 参数
     */
    @Getter
    private final Map<String, Object> variables = new HashMap<>();

    /**
     * 设置 Query 参数
     */
    public HttpUrl setQuery(String name, String value) {
        this.query.set(name, value);
        return this;
    }

    /**
     * 设置 Query 参数
     */
    public HttpUrl setQuery(String name, List<String> values) {
        this.query.put(name, values);
        return this;
    }

    /**
     * 添加 Query 参数
     */
    public HttpUrl addQuery(String name, String value) {
        this.query.add(name, value);
        return this;
    }

    /**
     * 添加 Query 参数
     */
    public HttpUrl addQuery(String name, List<String> values) {
        this.query.addAll(name, values);
        return this;
    }

    /**
     * 添加 Query 参数
     */
    public HttpUrl addQuery(MultiValueMap<String, String> query) {
        this.query.addAll(query);
        return this;
    }

    /**
     * 设置 URL 参数
     */
    public HttpUrl setVariable(String name, Object value) {
        this.variables.put(name, value);
        return this;
    }

    public HttpUrl setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    public String getValue() {
        URI source;
        if (Mapx.isNullOrEmpty(this.getVariables())) {
            source = URI.create(this.value);
        } else {
            // 替换路径里的参数
            source = UriComponentsBuilder.fromUriString(this.value).build(this.getVariables());
        }

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(source.toString());

        if (Stringx.isNotBlank(this.baseUrl)) {
            // 拼接 baseUrl 的 scheme、host、port、part
            URI baseUri = URI.create(this.baseUrl);
            if (Stringx.isNullOrBlank(source.getHost()) && Stringx.isNotBlank(baseUri.getHost())) {
                builder.scheme(baseUri.getScheme())
                        .host(baseUri.getHost())
                        .port(baseUri.getPort());
            }
            // 添加 baseUrl 的前缀
            if (Stringx.isNotBlank(baseUri.getPath())) {
                builder.replacePath(Stringx.removeSuffix(baseUri.getPath(), "/") + "/" + Stringx.removePrefix(source.getPath(), "/"));
            }
        }

        for (Map.Entry<String, List<String>> item : this.getQuery().entrySet()) {
            if (Listx.isNotEmpty(item.getValue())) {
                builder.queryParam(Stringx.encodeUrl(item.getKey()), item.getValue().stream().map(Stringx::encodeUrl).collect(Collectors.toList()));
            }
        }

        return builder.build().toString();
    }

    public String getPath() {
        return URI.create(this.getValue()).getPath();
    }

    public URI toURI() {
        return URI.create(this.getValue());
    }

    @Override
    public String toString() {
        return this.getValue();
    }

    /**
     * 不能修改的 Url
     */
    public static class ImmutableHttpUrl extends HttpUrl {

        public ImmutableHttpUrl(String value) {
            super(value);
        }

        @Override
        public String getValue() {
            return this.value;
        }
    }
}
