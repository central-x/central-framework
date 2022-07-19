package central.starter.webmvc.render;

import central.util.Mapx;
import central.util.Objectx;
import central.util.Stringx;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * @author Alan Yeh
 * @since 2022/07/16
 */
public class ErrorRender extends Render<ErrorRender> {
    public ErrorRender(HttpServletRequest request, HttpServletResponse response) {
        super(request, response);
    }

    private String message;
    private Throwable throwable;

    public ErrorRender setError(HttpStatusCode status, String message) {
        this.setStatus(status);
        this.message = message;
        return this;
    }

    public void render(HttpStatusCode status, String message) throws IOException {
        this.setError(status, message).render();
    }

    public ErrorRender setError(HttpStatusCode status, Throwable throwable) {
        this.setStatus(status);
        this.message = throwable.getLocalizedMessage();
        this.throwable = throwable;
        return this;
    }

    public void render(HttpStatus status, Throwable throwable) throws IOException {
        this.setError(status, throwable).render();
    }

    public ErrorRender setError(Throwable throwable) {
        if (throwable instanceof ResponseStatusException exception) {
            return setError(exception.getStatusCode(), throwable);
        } else {
            return setError(HttpStatus.INTERNAL_SERVER_ERROR, throwable);
        }
    }

    public void render(Throwable throwable) throws IOException {
        this.setError(throwable).render();
    }

    @Override
    public void render() throws IOException {
        if (isAccept(MediaType.APPLICATION_JSON)) {
            // 需要返回 JSON 格式的数据
            new JsonRender(this.getRequest(), this.getResponse()).render(Mapx.newHashMap("message", this.message));
        } else {
            String body = Stringx.format("<html><body><h2>{} ({})</h2><p>{}</p><div id='created'>{}</div></body></html>", Objectx.get(HttpStatus.resolve(this.getStatus().value()), HttpStatus.INTERNAL_SERVER_ERROR).getReasonPhrase(), this.getStatus().value(), this.message, OffsetDateTime.now().toString());

            response.setHeader(HttpHeaders.PRAGMA, "no-cache");
            response.setHeader(HttpHeaders.CACHE_CONTROL, "no-cache");
            response.setDateHeader(HttpHeaders.EXPIRES, 0);
            response.setContentType("text/html; charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.write(body);
        }
    }

    private boolean isAccept(MediaType type) {
        List<MediaType> accepts = MediaType.parseMediaTypes(this.getRequest().getHeader(HttpHeaders.ACCEPT));

        for (MediaType accept : accepts) {
            if ("*".equals(accept.getType()) && "*".equals(accept.getSubtype())) {
                continue;
            }

            if (accept.isCompatibleWith(type)) {
                return true;
            }
        }

        return false;
    }
}
