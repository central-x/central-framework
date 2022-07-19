package central.net.http.executor.okhttp;

import central.net.http.HttpRequest;
import central.net.http.HttpResponse;
import central.net.http.body.Body;
import central.util.LazyValue;
import lombok.Getter;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.io.InputStream;

/**
 * OKHttp Response
 *
 * @author Alan Yeh
 * @since 2022/07/15
 */
public class OkHttpResponse extends HttpResponse<OkHttpResponse.ResponseBody> {
    @Getter
    private final Response response;

    @Getter
    private final ResponseBody body;

    public OkHttpResponse(HttpRequest request, Response response) {
        super(request);
        this.response = response;
        this.body = new ResponseBody(response.body());
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.resolve(response.code());
    }

    private final LazyValue<HttpHeaders> headers = new LazyValue<>(() -> {
        HttpHeaders result = new HttpHeaders();
        getResponse().headers().forEach(pair -> result.add(pair.getFirst(), pair.getSecond()));
        return result;
    });
    @Override
    public HttpHeaders getHeaders() {
        return this.headers.get();
    }

    public static class ResponseBody implements Body {

        private final okhttp3.ResponseBody body;

        private ResponseBody(okhttp3.ResponseBody body){
            this.body = body;
        }


        @Override
        public MediaType getContentType() {
            if (this.body.contentType() != null){
                return MediaType.parseMediaType(this.body.contentType().toString());
            } else {
                return null;
            }
        }

        @Override
        public Long getContentLength() {
            return this.body.contentLength();
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return this.body.byteStream();
        }

        @Override
        public String description() {
            return "(binary)";
        }
    }
}
