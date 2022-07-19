package central.net.http.processor;

import central.pattern.chain.reactive.ReactiveProcessor;
import central.net.http.HttpRequest;
import central.net.http.HttpResponse;
import central.net.http.body.Body;

/**
 * 响应式处理器
 *
 * @author Alan Yeh
 * @since 2022/07/15
 */
public interface ReactiveHttpProcessor extends ReactiveProcessor<HttpRequest, HttpResponse<? extends Body>> {
}
