package central.net.http.body.extractor;

import central.lang.reflect.TypeReference;
import central.net.http.body.Body;
import central.net.http.body.BodyExtractor;
import central.util.Jsonx;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * JSON 对象反序列化
 *
 * @author Alan Yeh
 * @since 2022/07/17
 */
public class JsonExtractor<T> implements BodyExtractor<T> {
    private final Charset charset;
    private final TypeReference<T> type;

    public JsonExtractor(TypeReference<T> type) {
        this.charset = StandardCharsets.UTF_8;
        this.type = type;
    }

    public static <T> JsonExtractor<T> of(TypeReference<T> type) {
        return new JsonExtractor<>(type);
    }

    @Override
    public T extract(Body body) throws IOException {
        return Jsonx.Default().deserialize(body.getInputStream(), StandardCharsets.UTF_8, this.type);
    }
}
