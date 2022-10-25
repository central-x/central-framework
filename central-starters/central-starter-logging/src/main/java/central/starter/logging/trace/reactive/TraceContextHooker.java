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
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Subscription;
import org.slf4j.MDC;
import reactor.core.CoreSubscriber;
import reactor.util.context.Context;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

/**
 * 追踪上下文 Hooker
 *
 * @author Alan Yeh
 * @since 2022/10/25
 */
@RequiredArgsConstructor
class TraceContextHooker<T> implements CoreSubscriber<T> {
    private final CoreSubscriber<T> delegate;

    @Override
    public @Nonnull Context currentContext() {
        return this.delegate.currentContext();
    }

    public <S> void trace(Consumer<S> consumer, S arg) {
        try {
            var traceId = TraceLocal.trace(this.currentContext().getOrDefault("webflux.traceId", ""));
            var tenant = this.currentContext().getOrDefault("webflux.tenant", "");

            MDC.put("traceId", traceId);
            MDC.put("tenant", tenant);

            consumer.accept(arg);
        } finally {
            MDC.clear();
            TraceLocal.end();
        }
    }

    @Override
    public void onSubscribe(@Nonnull Subscription s) {
        this.trace(this.delegate::onSubscribe, s);
    }

    @Override
    public void onNext(T t) {
        this.trace(this.delegate::onNext, t);
    }

    @Override
    public void onError(Throwable throwable) {
        this.trace(this.delegate::onError, throwable);
    }

    @Override
    public void onComplete() {
        try {
            var traceId = TraceLocal.trace(this.currentContext().getOrDefault("webflux.traceId", ""));
            var tenant = this.currentContext().getOrDefault("webflux.tenant", "");

            MDC.put("traceId", traceId);
            MDC.put("tenant", tenant);

            this.delegate.onComplete();
        } finally {
            MDC.clear();
            TraceLocal.end();
        }
    }
}
