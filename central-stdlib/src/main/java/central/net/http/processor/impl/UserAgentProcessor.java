package central.net.http.processor.impl;

import central.pattern.chain.ProcessChain;
import central.pattern.chain.reactive.ReactiveProcessChain;
import central.net.http.HttpRequest;
import central.net.http.HttpResponse;
import central.net.http.body.Body;
import central.net.http.processor.HttpProcessor;
import central.net.http.processor.ReactiveHttpProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;

/**
 * 处理 UserAgent
 *
 * @author Alan Yeh
 * @since 2022/07/15
 */
@RequiredArgsConstructor
public class UserAgentProcessor implements HttpProcessor, ReactiveHttpProcessor {

    private final String userAgent;

    @Override
    public boolean predicate(HttpRequest target) {
        return true;
    }

    @Override
    public HttpResponse<? extends Body> process(HttpRequest target, ProcessChain<HttpRequest, HttpResponse<? extends Body>> chain) throws Exception {
        target.setHeader(HttpHeaders.USER_AGENT, this.userAgent);
        return chain.process(target);
    }

    @Override
    public Mono<HttpResponse<? extends Body>> process(HttpRequest target, ReactiveProcessChain<HttpRequest, HttpResponse<? extends Body>> chain) {
        target.setHeader(HttpHeaders.USER_AGENT, this.userAgent);
        return chain.process(target);
    }
}
