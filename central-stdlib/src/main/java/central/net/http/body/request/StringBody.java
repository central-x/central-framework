package central.net.http.body.request;

import central.net.http.body.Body;
import central.util.Stringx;
import org.springframework.http.MediaType;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * String Body
 * 用于传输字符串
 *
 * @author Alan Yeh
 * @since 2022/07/14
 */
public class StringBody implements Body {

    private final byte[] body;

    private final MediaType contentType;

    public StringBody(String body) {
        this(body, MediaType.TEXT_PLAIN);
    }

    public StringBody(String body, MediaType contentType) {
        this.body = body.getBytes(StandardCharsets.UTF_8);
        this.contentType = new MediaType(contentType, StandardCharsets.UTF_8);
    }

    @Override
    public MediaType getContentType() {
        return this.contentType;
    }

    @Override
    public Long getContentLength() {
        return (long) this.body.length;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(this.body);
    }

    @Override
    public String description() {
        return Stringx.format("(String: {})", new String(this.body, StandardCharsets.UTF_8));
    }
}