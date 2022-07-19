package central.net.http;

import central.net.http.body.Body;
import lombok.Getter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

/**
 * Http 响应
 *
 * @author Alan Yeh
 * @since 2022/07/14
 */
public abstract class HttpResponse<T extends Body> implements AutoCloseable {
    /**
     * 响应创建时间
     */
    @Getter
    private final long timestamp = System.currentTimeMillis();

    /**
     * 此响应的请求
     */
    @Getter
    private final HttpRequest request;

    public HttpResponse(HttpRequest request){
        this.request = request;
    }

    /**
     * 状态码
     */
    public abstract HttpStatus getStatus();

    /**
     * 状态码在 [200, 300) 之间为成功
     */
    public boolean isSuccess(){
        return this.getStatus().value() >= 200 && this.getStatus().value() < 300;
    }

    /**
     * 获取响应头
     */
    public abstract HttpHeaders getHeaders();

    /**
     * 获取响应体
     */
    public abstract T getBody();

    @Override
    public void close() throws Exception {
        if (this.getBody() != null){
            this.getBody().close();
        }
    }
}
