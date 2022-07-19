package central.net.http.processor;

import central.pattern.chain.Processor;
import central.net.http.HttpRequest;
import central.net.http.HttpResponse;
import central.net.http.body.Body;

/**
 * 请求处理器
 *
 * @author Alan Yeh
 * @since 2022/07/14
 */
public interface HttpProcessor extends Processor<HttpRequest, HttpResponse<? extends Body>> {
}
