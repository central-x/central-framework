package central.net.http.body.extractor;

import central.io.IOStreamx;
import central.net.http.body.Body;
import central.net.http.body.BodyExtractor;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * String 序列化
 *
 * @author Alan Yeh
 * @since 2022/07/17
 */
public class StringExtractor implements BodyExtractor<String> {

    private final Charset charset;

    public StringExtractor() {
        this.charset = StandardCharsets.UTF_8;
    }

    public StringExtractor(Charset charset) {
        this.charset = charset;
    }

    public static StringExtractor of() {
        return new StringExtractor();
    }

    public static StringExtractor of(Charset charset) {
        return new StringExtractor(charset);
    }

    @Override
    public String extract(Body body) throws IOException {
        return IOStreamx.readText(body.getInputStream(), this.charset);
    }
}
