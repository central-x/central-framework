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

import central.io.IOStreamx;
import central.net.http.HttpRequest;
import central.net.http.HttpResponse;
import central.net.http.body.request.TextBody;
import central.net.http.processor.HttpProcessor;
import central.net.http.processor.ReactiveHttpProcessor;
import central.pattern.chain.ProcessChain;
import central.pattern.chain.reactive.ReactiveProcessChain;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Set;

/// Text Body Processor
///
/// 如果响应体的 MediaType 是 application/json，则提前处理，方便后续处理
///
/// @author Alan Yeh
public class TextResponseProcessor implements HttpProcessor, ReactiveHttpProcessor {
    @Override
    public boolean predicate(HttpRequest target) {
        return true;
    }

    private final Set<MediaType> types = Set.of(
            MediaType.APPLICATION_JSON,
            MediaType.APPLICATION_XML,
            MediaType.TEXT_HTML,
            MediaType.TEXT_PLAIN,
            MediaType.TEXT_XML
    );

    @Override
    public HttpResponse process(HttpRequest target, ProcessChain<HttpRequest, HttpResponse> chain) throws Exception {
        return this.process(chain.process(target));
    }

    @Override
    public Mono<HttpResponse> process(HttpRequest target, ReactiveProcessChain<HttpRequest, HttpResponse> chain) {
        return chain.process(target)
                .flatMap(response -> {
                    try {
                        var result = this.process(response);
                        return Mono.just(result);
                    } catch (Exception error) {
                        return Mono.error(error);
                    }
                });
    }

    private HttpResponse process(HttpResponse response) throws Exception {
        if (response.getBody() != null && this.types.stream().allMatch(type -> type.isCompatibleWith(response.getHeaders().getContentType()))) {
            var charset = response.getBody().getContentType().getCharset();
            if (charset == null) {
                charset = StandardCharsets.UTF_8;
            }
            // 修改响应体
            return HttpResponse.mutate(response)
                    .body(new TextBody(IOStreamx.readText(response.getBody().getInputStream(), charset)))
                    .build();
        }
        return response;
    }
}
