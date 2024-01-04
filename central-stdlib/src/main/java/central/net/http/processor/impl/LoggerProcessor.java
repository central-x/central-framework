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
import central.net.http.processor.HttpProcessor;
import central.net.http.processor.ReactiveHttpProcessor;
import central.util.Logx;
import lombok.experimental.ExtensionMethod;
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
@ExtensionMethod(Logx.class)
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

    private void log(HttpResponse response) {
        String lineSeparator = System.getProperty("line.separator", "\n");

        var builder = new StringBuilder("┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ ".wrap(Logx.Color.WHITE)).append("Request".wrap(Logx.Color.PURPLE)).append(" ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━".wrap(Logx.Color.WHITE)).append(lineSeparator);

        // 打印 HttpRequest 日志
        var request = response.getRequest();
        builder.append("┣ ".wrap(Logx.Color.WHITE)).append("Method".wrap(Logx.Color.BLUE)).append(": ").append(request.getMethod().name()).append(lineSeparator);
        builder.append("┣ ".wrap(Logx.Color.WHITE)).append("URL".wrap(Logx.Color.BLUE)).append(": ").append(request.getUrl().toString()).append(lineSeparator);
        builder.append("┣ ".wrap(Logx.Color.WHITE)).append("Headers".wrap(Logx.Color.BLUE)).append(": (").append(request.getHeaders().size()).append(")").append(lineSeparator);

        for (Map.Entry<String, List<String>> header : request.getHeaders().entrySet()) {
            for (String value : header.getValue()) {
                builder.append("┃ ".wrap(Logx.Color.WHITE)).append("- ").append(header.getKey()).append(": ").append(value).append(lineSeparator);
            }
        }
        builder.append("┣ ".wrap(Logx.Color.WHITE)).append("Attributes".wrap(Logx.Color.BLUE)).append(": (").append(request.getAttributes().size()).append(")").append(lineSeparator);

        for (Map.Entry<String, Object> attribute : request.getAttributes().entrySet()) {
            builder.append("┃ ".wrap(Logx.Color.WHITE)).append("- ").append(attribute.getKey()).append(": ").append(attribute.getValue()).append(lineSeparator);
        }

        if (request.getBody() != null) {
            builder.append("┣ ".wrap(Logx.Color.WHITE)).append("Body".wrap(Logx.Color.BLUE)).append(": ").append(request.getBody().description()).append(lineSeparator);
        } else {
            builder.append("┣ ".wrap(Logx.Color.WHITE)).append("Body".wrap(Logx.Color.BLUE)).append(": <null>").append(lineSeparator);
        }

        // 打印 HttpResponse 日志
        builder.append("┣━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ ".wrap(Logx.Color.WHITE)).append("Response".wrap(Logx.Color.PURPLE)).append(" ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━".wrap(Logx.Color.WHITE)).append(lineSeparator);
        builder.append("┣ ".wrap(Logx.Color.WHITE)).append("Duration".wrap(Logx.Color.BLUE)).append(": ").append(response.getTimestamp() - response.getRequest().getTimestamp()).append("ms").append(lineSeparator);
        builder.append("┣ ".wrap(Logx.Color.WHITE)).append("Status".wrap(Logx.Color.BLUE)).append(": ").append(response.getStatus().value()).append("(").append(response.getStatus().name()).append(")").append(lineSeparator);
        builder.append("┣ ".wrap(Logx.Color.WHITE)).append("Headers".wrap(Logx.Color.BLUE)).append(": (").append(response.getHeaders().size()).append(")").append(lineSeparator);
        for (Map.Entry<String, List<String>> header : response.getHeaders().entrySet()) {
            for (String value : header.getValue()) {
                builder.append("┃ ".wrap(Logx.Color.WHITE)).append("- ").append(header.getKey()).append(": ").append(value).append(lineSeparator);
            }
        }
        if (response.getBody() != null) {
            builder.append("┣ ".wrap(Logx.Color.WHITE)).append("Body".wrap(Logx.Color.BLUE)).append(": ").append(response.getBody().description()).append(lineSeparator);
        } else {
            builder.append("┣ ".wrap(Logx.Color.WHITE)).append("Body".wrap(Logx.Color.BLUE)).append(": <null>").append(lineSeparator);
        }
        builder.append("┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━".wrap(Logx.Color.WHITE));

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
    public HttpResponse process(HttpRequest target, ProcessChain<HttpRequest, HttpResponse> chain) throws Exception {
        var response = chain.process(target);
        this.log(response);
        return response;
    }

    @Override
    public Mono<HttpResponse> process(HttpRequest target, ReactiveProcessChain<HttpRequest, HttpResponse> chain) {
        return chain.process(target).doOnSuccess(this::log);
    }
}
