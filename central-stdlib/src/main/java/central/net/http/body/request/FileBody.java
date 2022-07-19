package central.net.http.body.request;

import central.net.http.body.Body;
import central.util.Stringx;
import org.springframework.http.MediaType;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

/**
 * File Body
 * 主要用于传输文件
 *
 * @author Alan Yeh
 * @since 2022/07/14
 */
public class FileBody implements Body {

    private final File body;

    private final MediaType contentType;

    public FileBody(File body) {
        this(body, MediaType.APPLICATION_OCTET_STREAM);
    }

    public FileBody(File body, MediaType contentType) {
        this.body = body;
        this.contentType = contentType;
    }

    @Override
    public MediaType getContentType() {
        return this.contentType;
    }

    @Override
    public Long getContentLength() {
        return this.body.length();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return Files.newInputStream(this.body.toPath(), StandardOpenOption.READ);
    }

    @Override
    public String description() {
        return Stringx.format("(File: {})", this.body.getName());
    }
}
