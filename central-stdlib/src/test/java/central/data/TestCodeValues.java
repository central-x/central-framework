package central.data;

import central.util.Setx;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * CodeValues Test Cases
 *
 * @author Alan Yeh
 * @since 2022/07/05
 */
public class TestCodeValues {
    @Test
    public void case1() {
        var data = new CodeValues<String>();
        data.put("code1", "value1");
        data.put("code2", "value2");
        data.put("code3", "value3");
        data.put("code2", "value4");

        Assertions.assertEquals(3, data.size());
        Assertions.assertEquals(Setx.newHashSet("code1", "code3", "code2"), data.codeSet());
    }

    @Test
    public void case2(){
        var data = new CodeValues<String>();
        data.put("code1", "value1");
        data.put("code2", "value2");
        data.put("code1", "value3");

        Assertions.assertEquals(2, data.size());
        Assertions.assertEquals("value3", data.get("code1"));
    }

    @Test
    public void case3(){
        var data = new CodeValues<String>();
        data.put("code1", "value1");
        data.put("code2", "value2");
        data.put("code1", "value3");
        data.remove("code1");

        Assertions.assertEquals(1, data.size());
        Assertions.assertNull(data.get("code1"));
    }
}
