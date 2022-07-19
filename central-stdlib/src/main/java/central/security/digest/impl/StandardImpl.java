package central.security.digest.impl;

import central.util.Stringx;
import central.security.digest.DigestImpl;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.MessageDigest;

/**
 * 标准摘要算法
 *
 * @author Alan Yeh
 * @since 2022/07/05
 */
public class StandardImpl implements DigestImpl {

    private final String algorithm;

    @Getter
    private final String name;

    public StandardImpl(String algorithm) {
        this.algorithm = algorithm;
        this.name = this.algorithm;
    }

    @Override
    @SneakyThrows
    public String digest(byte[] bytes) {
        MessageDigest md = MessageDigest.getInstance(this.algorithm);
        md.update(bytes);
        byte[] digest = md.digest();

        return Stringx.encodeHex(digest);
    }

    @Override
    @SneakyThrows
    public String digest(InputStream is) {
        MessageDigest md = MessageDigest.getInstance(this.algorithm);

        int BUFFER_SIZE = 8 * 1024;
        try (BufferedInputStream buffered = new BufferedInputStream(is, BUFFER_SIZE)) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int length = -1;

            while ((length = buffered.read(buffer)) != -1) {
                md.update(buffer, 0, length);
            }

            return Stringx.encodeHex(md.digest());
        }
    }

    @Override
    @SneakyThrows
    public String digest(String value, Charset charset) {
        return digest(value.getBytes(charset));
    }
}
