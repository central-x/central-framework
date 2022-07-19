package central.net.http;

import central.util.Stringx;
import lombok.Getter;

import java.io.Serial;

/**
 * Http Exception
 *
 * @author Alan Yeh
 * @since 2022/07/14
 */
public class HttpException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = 8580026645615849189L;

    @Getter
    private final HttpRequest request;

    @Getter
    private final HttpResponse<?> response;

    public HttpException(HttpRequest request, HttpResponse<?> response){
        super(Stringx.format("Request failed: {}({}) {} {}", response.getStatus().value(), response.getStatus().getReasonPhrase(), request.getMethod().name(), request.getUrl()));
        this.request = request;
        this.response = response;
    }

    public HttpException(HttpRequest request, HttpResponse<?> response, String message){
        super(message);
        this.request = request;
        this.response = response;
    }

    public HttpException(HttpRequest request, HttpResponse<?> response, String message, Throwable cause){
        super(message, cause);
        this.request = request;
        this.response = response;
    }
}
