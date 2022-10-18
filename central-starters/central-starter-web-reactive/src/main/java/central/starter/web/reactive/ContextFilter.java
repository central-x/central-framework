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

package central.starter.web.reactive;

import central.starter.web.reactive.exception.ExceptionHandler;
import central.starter.web.reactive.render.ErrorRender;
import central.util.Listx;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * 保存 WebFlux 上下文
 *
 * @author Alan Yeh
 * @since 2022/10/09
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ContextFilter implements WebFilter {

    @Setter(onMethod_ = @Autowired)
    private List<ExceptionHandler> handlers;

    @Override
    public @Nonnull Mono<Void> filter(@Nonnull ServerWebExchange exchange, WebFilterChain chain) {
        return chain.filter(exchange)
                .contextWrite(context -> context
                        .put("webflux.request", exchange.getRequest())
                        .put("webflux.response", exchange.getResponse())
                        .put("webflux.exchange", exchange))
                .onErrorResume(error -> {
                    if (Listx.isNotEmpty(handlers)) {
                        for (var handler : handlers) {
                            if (handler.support(error)) {
                                return handler.handle(exchange, error);
                            }
                        }
                    }
                    return ErrorRender.of(exchange).render(HttpStatus.INTERNAL_SERVER_ERROR, error);
                });
    }
}
