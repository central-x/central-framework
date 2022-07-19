package central.net.http.processor.impl;

import central.pattern.chain.ProcessChain;
import central.pattern.chain.reactive.ReactiveProcessChain;
import central.net.http.HttpRequest;
import central.net.http.HttpResponse;
import central.net.http.body.Body;
import central.net.http.processor.HttpProcessor;
import central.net.http.processor.ReactiveHttpProcessor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Mono;

/**
 * 为请求添加头部信息
 *
 * @author Alan Yeh
 * @since 2022/07/14
 */
public class AddHeaderProcessor implements HttpProcessor, ReactiveHttpProcessor {

    private final MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();

    public AddHeaderProcessor(String name, String value) {
        this.headers.add(name, value);
    }

    public AddHeaderProcessor(MultiValueMap<String, String> headers) {
        this.headers.addAll(headers);
    }

    @Override
    public boolean predicate(HttpRequest target) {
        return true;
    }

    @Override
    public HttpResponse<? extends Body> process(HttpRequest target, ProcessChain<HttpRequest, HttpResponse<? extends Body>> chain) throws Exception {
        target.getHeaders().addAll(this.headers);
        return chain.process(target);
    }

    @Override
    public Mono<HttpResponse<? extends Body>> process(HttpRequest target, ReactiveProcessChain<HttpRequest, HttpResponse<? extends Body>> chain) {
        target.getHeaders().addAll(this.headers);
        return chain.process(target);
    }
}
