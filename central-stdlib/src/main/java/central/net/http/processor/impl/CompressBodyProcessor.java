package central.net.http.processor.impl;

import central.pattern.chain.ProcessChain;
import central.pattern.chain.reactive.ReactiveProcessChain;
import central.net.http.HttpRequest;
import central.net.http.HttpResponse;
import central.net.http.body.Body;
import central.net.http.body.CompressType;
import central.net.http.body.CompressedBody;
import central.net.http.processor.HttpProcessor;
import central.net.http.processor.ReactiveHttpProcessor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import reactor.core.publisher.Mono;

/**
 * 压缩请求体
 *
 * @author Alan Yeh
 * @since 2022/07/14
 */
@Getter
@AllArgsConstructor
public class CompressBodyProcessor implements HttpProcessor, ReactiveHttpProcessor {

    private final CompressType type;

    @Override
    public boolean predicate(HttpRequest target) {
        return true;
    }

    @Override
    public HttpResponse<? extends Body> process(HttpRequest target, ProcessChain<HttpRequest, HttpResponse<? extends Body>> chain) throws Exception {
        target.setBody(new CompressedBody(target.getBody(), type));
        return chain.process(target);
    }

    @Override
    public Mono<HttpResponse<? extends Body>> process(HttpRequest target, ReactiveProcessChain<HttpRequest, HttpResponse<? extends Body>> chain) {
        target.setBody(new CompressedBody(target.getBody(), type));
        return chain.process(target);
    }
}
