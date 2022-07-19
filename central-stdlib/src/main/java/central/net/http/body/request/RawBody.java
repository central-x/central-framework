package central.net.http.body.request;

import central.net.http.body.Body;
import central.util.Stringx;
import org.springframework.http.MediaType;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Raw Body
 * 主要是用于传输字符串，如 Json 字符串、XML 字符串
 *
 * @author Alan Yeh
 * @since 2022/07/14
 */
public class RawBody implements Body {
    private final byte[] body;

    private final MediaType contentType;

    public RawBody(String body, MediaType contentType) {
        this(body.getBytes(StandardCharsets.UTF_8), new MediaType(contentType, StandardCharsets.UTF_8));
    }

    public RawBody(byte[] body, MediaType contentType) {
        this.body = body;
        this.contentType = contentType;
    }

    @Override
    public MediaType getContentType() {
        return this.contentType;
    }

    @Override
    public Long getContentLength() {
        return (long) body.length;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(body);
    }

    @Override
    public String description() {
        return Stringx.format("(Raw: binary)");
    }
}
