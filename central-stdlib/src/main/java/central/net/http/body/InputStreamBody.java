package central.net.http.body;

import lombok.Getter;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.io.InputStream;

/**
 * InputStream Body
 * 主要用于传输数据流
 *
 * @author Alan Yeh
 * @since 2022/07/14
 */
public class InputStreamBody implements Body {

    @Getter
    private final Long contentLength;

    @Getter
    private final MediaType contentType;

    private final InputStream body;

    public InputStreamBody(InputStream body) {
        this(body, -1, MediaType.APPLICATION_OCTET_STREAM);
    }

    public InputStreamBody(InputStream body, long contentLength, MediaType contentType) {
        this.body = body;
        this.contentLength = contentLength;
        this.contentType = contentType;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return this.body;
    }

    @Override
    public String description() {
        return "(InputStream)";
    }

    @Override
    public void close() throws Exception {
        this.body.close();
    }
}
