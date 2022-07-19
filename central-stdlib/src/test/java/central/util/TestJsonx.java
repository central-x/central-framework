package central.util;

import central.lang.reflect.TypeReference;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.HashMap;

/**
 * Jsonx Test Cases
 *
 * @author Alan Yeh
 * @since 2022/07/05
 */
public class TestJsonx {

    @Test
    public void case1() {
        var map = new HashMap<String, Object>();
        map.put("int", 1);
        map.put("string", "str");
        map.put("null", null);

        var json = Jsonx.Default().serialize(map);

        var result = Jsonx.Default().deserialize(json, TypeReference.forMapType(String.class, Object.class));

        Assertions.assertEquals(3, result.size());
        Assertions.assertEquals(1, result.get("int"));
        Assertions.assertEquals("str", result.get("string"));
        Assertions.assertNull(result.get("null"));
    }

    @Test
    public void case2(){
        var map = new HashMap<String, Object>();
        map.put("int", 1);
        map.put("string", "我是中国人");
        map.put("null", null);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Charset charset = Charset.forName("GB2312");
        Jsonx.Default().serialize(output, charset, map, false);

        var result = Jsonx.Default().deserialize(output.toString(charset), TypeReference.forMapType(String.class, Object.class));

        Assertions.assertEquals(3, result.size());
        Assertions.assertEquals(1, result.get("int"));
        Assertions.assertEquals("我是中国人", result.get("string"));
        Assertions.assertNull(result.get("null"));
    }
}
