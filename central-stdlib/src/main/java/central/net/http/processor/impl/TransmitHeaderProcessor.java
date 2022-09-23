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

import central.pattern.chain.ProcessChain;
import central.pattern.chain.reactive.ReactiveProcessChain;
import central.net.http.HttpRequest;
import central.net.http.HttpResponse;
import central.net.http.body.Body;
import central.net.http.processor.HttpProcessor;
import central.net.http.processor.ReactiveHttpProcessor;
import central.lang.Arrayx;
import central.util.Listx;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 传递请求头
 *
 * @author Alan Yeh
 * @since 2022/07/15
 */
public class TransmitHeaderProcessor implements HttpProcessor, ReactiveHttpProcessor {
    private final Set<String> headers;

    public TransmitHeaderProcessor(String... headers) {
        this.headers = Arrayx.asStream(headers).map(String::toLowerCase).collect(Collectors.toSet());
    }

    @Override
    public boolean predicate(HttpRequest target) {
        return true;
    }

    @Override
    public HttpResponse<? extends Body> process(HttpRequest target, ProcessChain<HttpRequest, HttpResponse<? extends Body>> chain) throws Exception {
        var attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            var request = attributes.getRequest();
            for (String header : this.headers) {
                // 如果请求头上没有这个头部，才加上
                if (!target.getHeaders().containsKey(header)) {
                    target.getHeaders().add(header, request.getHeader(header));
                }
            }
        }
        return chain.process(target);
    }

    @Override
    public Mono<HttpResponse<? extends Body>> process(HttpRequest target, ReactiveProcessChain<HttpRequest, HttpResponse<? extends Body>> chain) {
        return Mono.deferContextual(context -> {
            Optional<ServerHttpRequest> serverRequest = context.getOrEmpty("webflux.request");
            if (serverRequest.isPresent()) {

                for (String header : this.headers) {
                    // 如果请求头上没有这个头部，才加上
                    if (!target.getHeaders().containsKey(header)) {
                        List<String> values = serverRequest.get().getHeaders().get(header);
                        if (Listx.isNotEmpty(values)) {
                            target.getHeaders().addAll(header, values);
                        }
                    }
                }
            }
            return chain.process(target);
        });
    }
}
