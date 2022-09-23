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

import central.lang.Attribute;
import central.net.http.body.Body;
import central.util.Objectx;
import central.lang.Stringx;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Http 请求
 *
 * @author Alan Yeh
 * @since 2022/07/14
 */
public class HttpRequest implements AutoCloseable {
    /**
     * 请求创建时间
     */
    @Getter
    private final long timestamp = System.currentTimeMillis();

    /**
     * 请求属性
     * 请求属性不参与网络请求，只用于跟踪请求上下文
     */
    @Getter
    private final Map<String, Object> attributes = new HashMap<>();

    /**
     * 添加请求属性
     */
    public <T> HttpRequest setAttribute(Attribute<T> attribute, T value) {
        this.attributes.put(attribute.getCode(), value);
        return this;
    }

    /**
     * 获取请求属性
     *
     * @param attribute 属性
     * @param <T>       属性类型
     */
    public <T> T getAttribute(Attribute<T> attribute) {
        var result = (T) this.attributes.get(attribute.getCode());
        if (result == null) {
            result = attribute.getValue();
            if (result != null) {
                this.attributes.put(attribute.getCode(), result);
            }
        }
        return result;
    }

    /**
     * 请求方法
     */
    @Getter
    @Setter
    private HttpMethod method;

    /**
     * 请求地址
     * 如果请求地址是完整的 URL 地址，则使用该 URL 地址访问
     * 如果请求地址不是完整的 URL 地址，则使用 HttpClient 的 baseUrl 进行拼接
     */
    @Getter
    @Setter
    private HttpUrl url;

    /**
     * Cookie
     */
    @Getter
    @Setter
    private Map<String, String> cookies = new LinkedHashMap<>();

    /**
     * 设置 Cookie
     */
    public HttpRequest setCookie(String name, String value) {
        this.cookies.put(name, value);
        return this;
    }

    /**
     * 设置 Cookie
     */
    public HttpRequest setCookie(Map<String, String> cookies) {
        this.cookies.putAll(cookies);
        return this;
    }

    public String getCookieHeader() {
        // 如果 Cookie 有值，需要加上 Cookie 的请求头之后再发送请求
        String cookie = "";
        if (this.headers.containsKey(HttpHeaders.COOKIE)) {
            cookie = Objectx.get(this.headers.getFirst(HttpHeaders.COOKIE), "").trim();
        }
        if (cookie.endsWith(";")) {
            cookie = cookie.substring(0, cookie.length() - 1);
        }

        for (Map.Entry<String, String> entry : this.cookies.entrySet()) {
            if (Stringx.isNotBlank(cookie)) {
                cookie += ";";
            }
            cookie = cookie + " " + entry.getKey() + "=" + entry.getValue();
        }

        return cookie;
    }

    /**
     * 请求头
     */
    @Setter
    @Getter
    private HttpHeaders headers = new HttpHeaders();

    /**
     * 设置请求头
     */
    public HttpRequest setHeader(String name, String value) {
        this.headers.set(name, value);
        return this;
    }

    /**
     * 设置请求头
     */
    public HttpRequest setHeader(String name, List<String> values) {
        this.headers.put(name, values);
        return this;
    }

    /**
     * 添加请求头
     */
    public HttpRequest addHeader(String name, String value) {
        this.headers.add(name, value);
        return this;
    }

    /**
     * 添加请求头
     */
    public HttpRequest addHeader(String name, List<String> values) {
        this.headers.addAll(name, values);
        return this;
    }

    /**
     * 添加请求头
     */
    public HttpRequest addHeaders(MultiValueMap<String, String> headers) {
        this.headers.addAll(headers);
        return this;
    }

    /**
     * 请求体
     */
    @Getter
    @Setter
    private Body body;

    @Override
    public void close() throws Exception {
        if (this.body != null) {
            this.body.close();
        }
    }

    public static Builder get(HttpUrl url){
        return new Builder().method(HttpMethod.GET).url(url);
    }

    public static Builder post(HttpUrl url){
        return new Builder().method(HttpMethod.POST).url(url);
    }

    public static Builder put(HttpUrl url){
        return new Builder().method(HttpMethod.PUT).url(url);
    }

    public static Builder delete(HttpUrl url){
        return new Builder().method(HttpMethod.DELETE).url(url);
    }

    public static Builder patch(HttpUrl url){
        return new Builder().method(HttpMethod.PATCH).url(url);
    }

    public static Builder builder(){
        return new Builder();
    }

    public static class Builder {
        private Builder(){}

        private HttpMethod method;

        public Builder method(HttpMethod method){
            this.method = method;
            return this;
        }

        private HttpUrl url;

        public Builder url(HttpUrl url){
            this.url = url;
            return this;
        }

        public HttpRequest build(){
            HttpRequest request = new HttpRequest();
            request.setUrl(this.url);
            request.setMethod(this.method);
            return request;
        }
    }
}
