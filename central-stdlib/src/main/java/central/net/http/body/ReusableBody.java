package central.net.http.body;

import central.io.Filex;
import central.io.IOStreamx;
import lombok.SneakyThrows;
import org.springframework.http.MediaType;

import java.io.*;

/**
 * 可重复使用的请求体
 *
 * @author Alan Yeh
 * @since 2022/07/14
 */
public class ReusableBody implements Body {
    private byte[] bytes;
    private final File cache;
    private final Body body;

    @SneakyThrows
    public ReusableBody(File cache, Body body) {
        this.cache = cache;
        this.body = body;

        if (this.getContentLength() > 0 && this.getContentLength() < 5 * 1024 * 1024) {
            // 如果 ContentLength < 5M，就缓存在内存里
            this.bytes = IOStreamx.readBytes(body.getInputStream());
        } else {
            // 如果 ContentLength 为 -1（未知），或大于 5M，就缓存到文件里
            if (this.cache.exists()) {
                Filex.delete(this.cache);
            }

            if (!this.cache.createNewFile()) {
                throw new IOException("Can not create file: " + this.cache.getAbsolutePath());
            }

            // 将请求的 Body 写到文件里
            IOStreamx.copy(this.body.getInputStream(), new FileOutputStream(this.cache));
        }
    }

    @Override
    public MediaType getContentType() {
        return this.body.getContentType();
    }

    @Override
    public Long getContentLength() {
        return this.body.getContentLength();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (this.bytes != null && this.bytes.length > 0) {
            return new ByteArrayInputStream(this.bytes);
        } else {
            return new FileInputStream(this.cache);
        }
    }

    @Override
    public String description() {
        return this.body.description();
    }

    @Override
    public void close() throws Exception {
        // 释放
        Filex.delete(this.cache);
        this.bytes = null;
    }

    public ReusableBody clone(File file) {
        return new ReusableBody(file, this);
    }
}
