package central.util;

import central.util.json.JacksonSerializer;
import central.util.json.JsonSerializer;

/**
 * JSON 工具
 *
 * @author Alan Yeh
 * @since 2022/07/05
 */
public class Jsonx {
    private static final JsonSerializer INSTANCE = new JacksonSerializer();

    public static JsonSerializer Default() {
        return INSTANCE;
    }
}
