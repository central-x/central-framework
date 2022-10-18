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

package central.starter.web.reactive.exception.handler;

import central.lang.Stringx;
import central.starter.web.reactive.exception.ExceptionHandler;
import central.starter.web.reactive.render.ErrorRender;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 参数绑定异常
 *
 * @author Alan Yeh
 * @since 2022/10/09
 */
@Component
public class WebExchangeBindExceptionHandler implements ExceptionHandler {
    @Override
    public boolean support(Throwable throwable) {
        return throwable instanceof WebExchangeBindException;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable throwable) {
        var ex = (WebExchangeBindException) throwable;

        var error = ex.getBindingResult().getAllErrors().get(0);
        var message = error.getDefaultMessage();

        if (error instanceof FieldError err) {
            if ("typeMismatch".equals(err.getCode())) {
                message = Stringx.format("参数[{}]类型不匹配", err.getField());
            } else {
                message = err.getDefaultMessage();
            }
        }

        return ErrorRender.of(exchange).render(HttpStatus.BAD_REQUEST, message);
    }
}
