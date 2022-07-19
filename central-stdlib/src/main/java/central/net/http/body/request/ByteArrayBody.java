package central.net.http.body.request;

import central.net.http.body.Body;
import org.springframework.http.MediaType;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * ByteArray Body
 * 用于传输 byte[]
 *
 * @author Alan Yeh
 * @since 2022/07/14
 */
public class ByteArrayBody implements Body {
    private final byte[] body;

    private final MediaType contentType;

    public ByteArrayBody(byte[] body) {
        this(body, MediaType.APPLICATION_OCTET_STREAM);
    }

    public ByteArrayBody(byte[] body, MediaType contentType) {
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
        return new ByteArrayInputStream(this.body);
    }

    @Override
    public String description() {
        return "(ByteArray: binary)";
    }
}
