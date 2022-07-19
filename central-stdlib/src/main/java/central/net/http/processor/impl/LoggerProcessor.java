package central.net.http.processor.impl;

import central.pattern.chain.ProcessChain;
import central.pattern.chain.reactive.ReactiveProcessChain;
import central.net.http.HttpRequest;
import central.net.http.HttpResponse;
import central.net.http.body.Body;
import central.net.http.processor.HttpProcessor;
import central.net.http.processor.ReactiveHttpProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/**
 * Logger
 *
 * @author Alan Yeh
 * @since 2022/07/14
 */
public class LoggerProcessor implements HttpProcessor, ReactiveHttpProcessor {

    private final Logger logger;

    public LoggerProcessor() {
        this(LoggerProcessor.class);
    }

    public LoggerProcessor(Class<?> clazz) {
        this(LoggerFactory.getLogger(clazz));
    }

    public LoggerProcessor(Logger logger) {
        this.logger = logger;
    }

    @Override
    public boolean predicate(HttpRequest target) {
        return true;
    }

    private void log(HttpResponse<? extends Body> response) {
        StringBuilder builder = new StringBuilder("\n┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        // 打印 HttpRequest 日志
        HttpRequest request = response.getRequest();
        builder.append("┣ Request:\n");
        builder.append("┣ Method: ").append(request.getMethod().name()).append("\n");
        builder.append("┣ URL: ").append(request.getUrl().toString()).append("\n");
        builder.append("┣ Headers: (").append(request.getHeaders().size()).append(")\n");
        for (Map.Entry<String, List<String>> header : request.getHeaders().entrySet()) {
            for (String value : header.getValue()) {
                builder.append("┣ - ").append(header.getKey()).append(": ").append(value).append("\n");
            }
        }
        builder.append("┣ Attributes: (").append(request.getAttributes().size()).append(")\n");

        for (Map.Entry<String, Object> attribute : request.getAttributes().entrySet()) {
            builder.append("┣ - ").append(attribute.getKey()).append(": ").append(attribute.getValue()).append("\n");
        }

        if (request.getBody() != null) {
            builder.append("┣ Body: ").append(request.getBody().description()).append("\n");
        } else {
            builder.append("┣ Body: (NULL)\n");
        }
        builder.append("┣━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");


        // 打印 HttpResponse 日志
        builder.append("┣ Response:\n");
        builder.append("┣ Duration: ").append(response.getTimestamp() - response.getRequest().getTimestamp()).append("ms\n");
        builder.append("┣ Status: ").append(response.getStatus().value()).append("(").append(response.getStatus().name()).append(")\n");
        builder.append("┣ Headers: (").append(response.getHeaders().size()).append(")\n");
        for (Map.Entry<String, List<String>> header : response.getHeaders().entrySet()) {
            for (String value : header.getValue()) {
                builder.append("┣ - ").append(header.getKey()).append(": ").append(value).append("\n");
            }
        }
        if (response.getBody() != null) {
            builder.append("┣ Body: ").append(response.getBody().description()).append("\n");
        } else {
            builder.append("┣ Body: (NULL)\n");
        }
        builder.append("┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        try {
            MDC.put("type", "network");
            MDC.put("duration", String.valueOf(response.getTimestamp() - response.getRequest().getTimestamp()));
            MDC.put("method", request.getMethod().name());
            MDC.put("url", request.getUrl().getValue());
            logger.info(builder.toString());
        } finally {
            MDC.remove("type");
            MDC.remove("duration");
            MDC.remove("method");
            MDC.remove("url");
        }
    }

    @Override
    public HttpResponse<? extends Body> process(HttpRequest target, ProcessChain<HttpRequest, HttpResponse<? extends Body>> chain) throws Exception {
        HttpResponse<? extends Body> response = chain.process(target);
        this.log(response);
        return response;
    }

    @Override
    public Mono<HttpResponse<? extends Body>> process(HttpRequest target, ReactiveProcessChain<HttpRequest, HttpResponse<? extends Body>> chain) {
        return chain.process(target).doOnSuccess(this::log);
    }
}
