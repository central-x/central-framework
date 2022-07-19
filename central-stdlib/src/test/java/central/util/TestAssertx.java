package central.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Assertx Test Cases
 *
 * @author Alan Yeh
 * @since 2022/07/11
 */
public class TestAssertx {

    @Test
    public void case1() {
        Assertions.assertThrows(RuntimeException.class, () -> {
            Assertx.must("test".getBytes().length < 1, () -> new RuntimeException("Test must"));
        });
    }

    @Test
    public void case2() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Assertx.mustTrue("test".getBytes().length < 1, "Test Assertx#mustTrue");
        });
    }

    @Test
    public void case3() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Assertx.mustEquals("a", 1, "Test Assertx#mustEquals");
        });
    }

    @Test
    public void case4() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Assertx.mustNotEquals("1", "1", "Test Assertx#mustNotEquals");
        });
    }

    @Test
    public void case5() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Assertx.mustNull("1", "Test Assertx#mustNull");
        });
    }

    @Test
    public void case6() {
        Integer obj = null;
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Assertx.mustNotNull(obj, "Test Assertx#{}mustNotNull");
        });
    }

    @Test
    public void case7() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Assertx.mustNullOrEmpty("test", "Test Assertx#mustNullOrEmpty");
        });
    }

    @Test
    public void case8() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Assertx.mustNotEmpty("", "Test Assertx#mustNotEmpty");
        });
    }

    @Test
    public void case9() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Assertx.mustNullOrBlank("    1", "Test Assertx#mustNullOrBlank");
        });
    }

    @Test
    public void case10() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Assertx.mustNotBlank("    ", "Test Assertx#mustNotBlank");
        });
    }

    @Test
    public void case11() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Assertx.mustNullOrEmpty(new String[]{"test"}, "Test Assertx#mustNullOrEmpty");
        });
    }

    @Test
    public void case12() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Assertx.mustNotEmpty(new String[0], "Test Assertx#mustNotEmpty");
        });
    }

    @Test
    public void case13() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Assertx.mustNullOrEmpty(List.of("test"), "Test Assertx#mustNullOrEmpty");
        });
    }

    @Test
    public void case14() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Assertx.mustNotEmpty(new ArrayList<>(), "Test Assertx#mustNullOrEmpty");
        });
    }

    @Test
    public void case15() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Assertx.mustNullOrEmpty(Map.of("key", "value"), "Test Assertx#mustNullOrEmpty");
        });
    }

    @Test
    public void case16() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Assertx.mustNotEmpty(Map.of(), "Test Assertx#mustNotEmpty");
        });
    }

    @Test
    public void case17() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Assertx.mustInstanceOf(String.class, 1, "Test Assertx#mustInstanceOf");
        });
    }

    @Test
    public void case18() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Assertx.mustAssignableFrom(String.class, Integer.class, "Test Assertx#mustInstanceOf");
        });
    }

}
