package central.security.digest.impl;

import central.util.Stringx;
import central.security.digest.DigestImpl;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bouncycastle.crypto.digests.SM3Digest;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * 国密 SM3 算法
 *
 * @author Alan Yeh
 * @since 2022/07/05
 */
public class SM3Impl implements DigestImpl {
    @Getter
    private final String name = "SM3";

    @Override
    public String digest(byte[] bytes) {
        return digest(new ByteArrayInputStream(bytes));
    }

    @Override
    public String digest(String value, Charset charset) {
        return digest(value.getBytes(charset));
    }

    @SneakyThrows
    @Override
    public String digest(InputStream is) {
        SM3Digest sm3 = new SM3Digest();

        int BUFFER_SIZE = 8 * 1024;
        try (BufferedInputStream buffered = new BufferedInputStream(is, BUFFER_SIZE)) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int length = -1;

            while ((length = buffered.read(buffer)) != -1) {
                sm3.update(buffer, 0, length);
            }

            byte[] result = new byte[sm3.getDigestSize()];
            sm3.doFinal(result, 0);
            return Stringx.encodeHex(result);
        }
    }
}
