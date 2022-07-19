package central.net.http.executor.java;

import central.net.http.HttpRequest;
import central.net.http.body.InputStreamBody;
import central.util.LazyValue;
import lombok.Getter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import java.io.InputStream;
import java.net.http.HttpResponse;

/**
 * @author Alan Yeh
 * @since 2022/07/18
 */
public class JavaResponse extends central.net.http.HttpResponse<InputStreamBody> {

    @Getter
    private final HttpResponse<InputStream> response;

    public JavaResponse(HttpRequest request, HttpResponse<InputStream> response) {
        super(request);
        this.response = response;
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.resolve(response.statusCode());
    }

    private final LazyValue<HttpHeaders> headers = new LazyValue<>(() -> {
        var result = new HttpHeaders();
        getResponse().headers().map().forEach(result::addAll);
        return result;
    });

    @Override
    public HttpHeaders getHeaders() {
        return this.headers.get();
    }

    @Override
    public InputStreamBody getBody() {
        return new InputStreamBody(this.response.body());
    }
}
