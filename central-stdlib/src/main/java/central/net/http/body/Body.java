package central.net.http.body;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.io.InputStream;

/**
 * Request/Response Body
 *
 * @author Alan Yeh
 * @since 2022/07/14
 */
public interface Body extends AutoCloseable {

    default HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(this.getContentType());
        headers.setContentLength(this.getContentLength());
        return headers;
    }

    /**
     * 内容类型
     */
    MediaType getContentType();

    /**
     * 数据长度
     */
    Long getContentLength();

    /**
     * 数据流
     */
    InputStream getInputStream() throws IOException;

    /**
     * 描述
     */
    String description();

    /**
     * 关闭 Body
     */
    @Override
    default void close() throws Exception {
    }

    default <T> T extract(BodyExtractor<T> extractor) throws IOException {
        return extractor.extract(this);
    }
}
