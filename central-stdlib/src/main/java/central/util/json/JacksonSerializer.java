package central.util.json;

import central.lang.reflect.TypeReference;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.*;
import java.nio.charset.Charset;

/**
 * Jackson 实现
 *
 * @author Alan Yeh
 * @since 2022/07/05
 */
public class JacksonSerializer implements JsonSerializer {
    @Getter
    private final ObjectMapper mapper;

    public JacksonSerializer() {
        this.mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    @Override
    @SneakyThrows
    public void serialize(OutputStream output, Charset charset, Object obj, boolean formatted) {
        ObjectWriter writer;
        if (formatted) {
            writer = mapper.writer(mapper.getSerializationConfig().getDefaultPrettyPrinter());
        } else {
            writer = mapper.writer();
        }
        writer.writeValues(new OutputStreamWriter(output, charset)).write(obj);
    }

    @Override
    @SneakyThrows
    public <T> T deserialize(InputStream input, Charset charset, TypeReference<T> reference) {
        return mapper.readValue(new InputStreamReader(input, charset.name()), mapper.getTypeFactory().constructType(reference.getType()));
    }
}
