package central.net.http.body.request;

import central.lang.reflect.TypeReference;
import central.net.http.body.Body;
import central.util.Jsonx;
import central.util.Stringx;
import org.springframework.http.MediaType;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Json Body
 * 主要用于传输 JSON
 *
 * @author Alan Yeh
 * @since 2022/07/14
 */
public class JsonBody implements Body {

    private final Map<String, Object> json = new HashMap<>();

    public JsonBody() {

    }

    public JsonBody(Object obj) {
        this.json.putAll(Jsonx.Default().deserialize(Jsonx.Default().serialize(obj), TypeReference.forMapType(String.class, Object.class)));
    }

    public JsonBody(Map<String, ?> map) {
        this.json.putAll(map);
    }

    public JsonBody(String json) {
        this.json.putAll(Jsonx.Default().deserialize(json, TypeReference.forMapType(String.class, Object.class)));
    }

    public JsonBody set(String name, Object value) {
        this.json.put(name, value);
        return this;
    }

    public JsonBody remove(String name) {
        this.json.remove(name);
        return this;
    }

    @Override
    public MediaType getContentType() {
        return new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8);
    }

    @Override
    public Long getContentLength() {
        String json = Jsonx.Default().serialize(this.json);
        byte[] data = json.getBytes(StandardCharsets.UTF_8);
        return (long) data.length;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        String json = Jsonx.Default().serialize(this.json);
        byte[] data = json.getBytes(StandardCharsets.UTF_8);
        return new ByteArrayInputStream(data);
    }

    @Override
    public String description() {
        return Stringx.format("(Json: {})", Jsonx.Default().serialize(this.json));
    }
}
