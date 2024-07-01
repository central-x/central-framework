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

package central.starter.test.cookie;

import central.util.Mapx;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMapAdapter;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.util.ArrayList;

/**
 * Cookie Store
 * <p>
 * 用于解决测试过程中的会话问题
 *
 * @author Alan Yeh
 * @since 2024/07/01
 */
public class CookieStore {

    private final CookieManager manager;

    private final URI uri = URI.create("http://localhost:8080");

    public CookieStore() {
        manager = new CookieManager();
        manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
    }

    /**
     * 从响应头里抓到需要保存的 Cookie
     *
     * @param uri      请求地址
     * @param response 请求响应
     */
    public void put(URI uri, HttpServletResponse response) throws IOException {
        var headers = new HttpHeaders();

        for (var name : response.getHeaderNames()) {
            var values = response.getHeaders(name);
            headers.put(name, new ArrayList<>(values));
        }

        this.put(uri, headers);
    }

    /**
     * 从响应头里抓到需要保存的 Cookie
     *
     * @param uri     请求地址
     * @param headers 响应头
     */
    public void put(URI uri, HttpHeaders headers) throws IOException {
        var targetUri = UriComponentsBuilder.fromUri(uri)
                .scheme(this.uri.getScheme())
                .host(this.uri.getHost())
                .port(this.uri.getPort())
                .build().toUri();

        this.manager.put(targetUri, headers);
    }

    /**
     * 获取请求指定 URI 时需要提供的请求头（Cookie 请求头）
     *
     * @param uri 待访问地址
     * @return 请求头
     */
    public HttpHeaders get(URI uri) throws IOException {
        var targetUri = UriComponentsBuilder.fromUri(uri)
                .scheme(this.uri.getScheme())
                .host(this.uri.getHost())
                .port(this.uri.getPort())
                .build().toUri();

        return new HttpHeaders(new MultiValueMapAdapter<>(this.manager.get(targetUri, Mapx.newHashMap())));
    }

    /**
     * 根据待访问地址获取需要的 Cookie
     *
     * @param uri 待访问地址
     * @return Cookie
     */
    public Cookie[] getCookies(URI uri) throws IOException {
        var targetUri = UriComponentsBuilder.fromUri(uri)
                .scheme(this.uri.getScheme())
                .host(this.uri.getHost())
                .port(this.uri.getPort())
                .build().toUri();

        var cookies = this.manager.getCookieStore().get(targetUri);
        return cookies.stream().map(it -> new Cookie(it.getName(), it.getValue())).toArray(Cookie[]::new);
    }
}
