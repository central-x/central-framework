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

package central.net.http.processor.impl;

import central.net.http.HttpRequest;
import central.net.http.HttpResponse;
import central.net.http.processor.HttpProcessor;
import central.net.http.processor.ReactiveHttpProcessor;
import central.pattern.chain.ProcessChain;
import central.pattern.chain.reactive.ReactiveProcessChain;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * 添加 Cookie
 *
 * @author Alan Yeh
 * @since 2023/02/13
 */
public class AddCookieProcessor implements HttpProcessor, ReactiveHttpProcessor {
    private final Map<String, String> cookies = new HashMap<>();

    public AddCookieProcessor(String name, String value) {
        this.cookies.put(name, value);
    }

    public AddCookieProcessor(Map<String, String> cookies) {
        this.cookies.putAll(cookies);
    }

    @Override
    public boolean predicate(HttpRequest target) {
        return true;
    }

    @Override
    public HttpResponse process(HttpRequest target, ProcessChain<HttpRequest, HttpResponse> chain) throws Throwable {
        target.setCookies(this.cookies);
        return chain.process(target);
    }

    @Override
    public Mono<HttpResponse> process(HttpRequest target, ReactiveProcessChain<HttpRequest, HttpResponse> chain) {
        target.setCookies(this.cookies);
        return chain.process(target);
    }
}
