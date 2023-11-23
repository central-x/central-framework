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

package central.starter.logging.trace.reactive;

import central.lang.TraceLocal;
import central.web.XForwardedHeaders;
import jakarta.annotation.Nonnull;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * 追踪过滤器
 *
 * @author Alan Yeh
 * @since 2022/10/25
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
public class WebFluxTraceFilter implements WebFilter {
    @Override
    public @Nonnull Mono<Void> filter(@Nonnull ServerWebExchange exchange, @Nonnull WebFilterChain chain) {
        var traceId = TraceLocal.trace(exchange.getRequest().getHeaders().getFirst(XForwardedHeaders.TRACE));
        var tenant = TraceLocal.trace(exchange.getRequest().getHeaders().getFirst(XForwardedHeaders.TENANT));

        if (!exchange.getRequest().getHeaders().containsKey(XForwardedHeaders.TRACE)) {
            // 添加 traceId 到请求头里，用于传递到下一个微服务
            exchange = exchange.mutate().request(exchange.getRequest().mutate().header(XForwardedHeaders.TRACE, traceId).build()).build();
        }
        // 添加 traceId 到响应头里，用于返回给调用方
        exchange.getResponse().getHeaders().set(XForwardedHeaders.TRACE, traceId);

        return chain.filter(exchange)
                // 设置到 Reactor 上下文
                .contextWrite(context -> context
                        .put("webflux.traceId", traceId)
                        .put("webflux.tenant", tenant));
    }
}
