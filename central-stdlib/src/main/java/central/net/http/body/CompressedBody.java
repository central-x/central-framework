package central.net.http.body;

import central.io.IOStreamx;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPOutputStream;

/**
 * 压缩请求体
 *
 * @author Alan Yeh
 * @since 2022/07/14
 */
@RequiredArgsConstructor
public class CompressedBody implements Body {
    private final Body delegate;

    private final CompressType type;

    @Override
    public HttpHeaders getHeaders() {
        HttpHeaders headers = delegate.getHeaders();
        headers.remove(HttpHeaders.CONTENT_LENGTH);
        headers.remove(HttpHeaders.CONTENT_ENCODING);

        headers.set(HttpHeaders.CONTENT_ENCODING, this.type.getName());
        return headers;
    }

    @Override
    public MediaType getContentType() {
        return this.delegate.getContentType();
    }

    @Override
    public Long getContentLength() {
        // 因为消息体被压缩，因此不知道有多长
        return -1L;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        switch (this.type) {
            case GZIP -> {
                try (GZIPOutputStream gzip = new GZIPOutputStream(out)) {
                    gzip.write(IOStreamx.readBytes(this.delegate.getInputStream()));
                    gzip.flush();
                }
            }
            case DEFLATE -> {
                try (DeflaterOutputStream deflate = new DeflaterOutputStream(out)) {
                    deflate.write(IOStreamx.readBytes(this.delegate.getInputStream()));
                    deflate.flush();
                }
            }
            default -> {
                throw new RuntimeException("不支持的压缩类型: " + this.type.getName() + "(" + this.type.getValue() + ")");
            }
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    @Override
    public String description() {
        return "Compressed(" + delegate.description() + ")";
    }

    @Override
    public void close() throws Exception {
        this.delegate.close();
    }
}