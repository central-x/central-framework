package central.net.http.processor.impl;

import central.pattern.chain.ProcessChain;
import central.pattern.chain.reactive.ReactiveProcessChain;
import central.net.http.HttpRequest;
import central.net.http.HttpResponse;
import central.net.http.body.Body;
import central.net.http.processor.HttpProcessor;
import central.net.http.processor.ReactiveHttpProcessor;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import reactor.core.publisher.Mono;

import java.util.Optional;

/**
 * 传递 X-Forwarded-* 请求头
 *
 * @author Alan Yeh
 * @since 2022/07/15
 */
public class TransmitForwardedProcessor implements HttpProcessor, ReactiveHttpProcessor {

    @Override
    public boolean predicate(HttpRequest target) {
        return true;
    }

    @Override
    public HttpResponse<? extends Body> process(HttpRequest target, ProcessChain<HttpRequest, HttpResponse<? extends Body>> chain) throws Exception {
        var attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            var request = attributes.getRequest();
            var headers = request.getHeaderNames();
            while (headers.hasMoreElements()) {
                var header = headers.nextElement();
                if (header.toLowerCase().startsWith("x-forwarded-")) {
                    target.getHeaders().add(header, request.getHeader(header));
                }
            }
        }
        return chain.process(target);
    }

    @Override
    public Mono<HttpResponse<? extends Body>> process(HttpRequest target, ReactiveProcessChain<HttpRequest, HttpResponse<? extends Body>> chain) {
        return Mono.deferContextual(context -> {
            Optional<ServerHttpRequest> request = context.getOrEmpty("webflux.request");
            if (request.isPresent()) {
                var headers = request.get().getHeaders();
                for (var header : headers.entrySet()) {
                    if (header.getKey().toLowerCase().startsWith("x-forwarded-")) {
                        target.getHeaders().addAll(header.getKey(), header.getValue());
                    }
                }
            }
            return chain.process(target);
        });
    }
}
