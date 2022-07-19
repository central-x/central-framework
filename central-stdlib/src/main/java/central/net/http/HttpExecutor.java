package central.net.http;

import central.net.http.body.Body;

import java.io.IOException;

/**
 * 请求执行器
 *
 * @author Alan Yeh
 * @since 2022/07/14
 */
public interface HttpExecutor {
    /**
     * 执行器名称
     */
    String getName();

    /**
     * 执行 Http 请求
     *
     * @param request 请求
     */
    HttpResponse<? extends Body> execute(HttpRequest request) throws Exception;
}