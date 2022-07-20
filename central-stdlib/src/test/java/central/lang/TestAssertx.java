package central.lang;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Assertx Test Cases
 *
 * @author Alan Yeh
 * @since 2022/07/11
 */
public class TestAssertx {

    @Test
    public void case1() {
        assertThrows(RuntimeException.class, () -> {
            Assertx.must("test".getBytes().length < 1, () -> new RuntimeException("Test must"));
        });
    }

    @Test
    public void case2() {
        assertThrows(IllegalArgumentException.class, () -> {
            Assertx.mustTrue("test".getBytes().length < 1, "Test Assertx#mustTrue");
        });
    }

    @Test
    public void case3() {
        assertThrows(RuntimeException.class, () -> {
            Assertx.mustTrue("test".getBytes().length < 1, () -> new RuntimeException("Test Assertx#mustTrue"));
        });
    }

    @Test
    public void case4() {
        assertThrows(IllegalArgumentException.class, () -> {
            Assertx.mustEquals("a", 1, "Test Assertx#mustEquals");
        });
    }

    @Test
    public void case5() {
        assertThrows(RuntimeException.class, () -> {
            Assertx.mustEquals("a", 1, () -> new RuntimeException("Test Assertx#mustEquals"));
        });
    }

    @Test
    public void case6() {
        assertThrows(IllegalArgumentException.class, () -> {
            Assertx.mustNotEquals("1", "1", "Test Assertx#mustNotEquals");
        });
    }

    @Test
    public void case7() {
        assertThrows(RuntimeException.class, () -> {
            Assertx.mustNotEquals("1", "1", () -> new RuntimeException("Test Assertx#mustNotEquals"));
        });
    }

    @Test
    public void case8() {
        assertThrows(IllegalArgumentException.class, () -> {
            Assertx.mustNull("1", "Test Assertx#mustNull");
        });
    }

    @Test
    public void case9() {
        assertThrows(RuntimeException.class, () -> {
            Assertx.mustNull("1", () -> new RuntimeException("Test Assertx#mustNull"));
        });
    }

    @Test
    public void case10() {
        Integer obj = null;
        assertThrows(IllegalArgumentException.class, () -> {
            Assertx.mustNotNull(obj, "Test Assertx#{}mustNotNull");
        });
    }

    @Test
    public void case11() {
        Integer obj = null;
        assertThrows(RuntimeException.class, () -> {
            Assertx.mustNotNull(obj, () -> new RuntimeException("Test Assertx#{}mustNotNull"));
        });
    }

    @Test
    public void case12() {
        assertThrows(IllegalArgumentException.class, () -> {
            Assertx.mustNotEmpty("", "Test Assertx#mustNotEmpty");
        });
    }

    @Test
    public void case13() {
        assertThrows(RuntimeException.class, () -> {
            Assertx.mustNotEmpty("", () -> new RuntimeException("Test Assertx#mustNotEmpty"));
        });
    }

    @Test
    public void case14() {
        assertThrows(IllegalArgumentException.class, () -> {
            Assertx.mustNullOrEmpty("test", "Test Assertx#mustNullOrEmpty");
        });
    }

    @Test
    public void case15() {
        assertThrows(RuntimeException.class, () -> {
            Assertx.mustNullOrEmpty("test", () -> new RuntimeException("Test Assertx#mustNullOrEmpty"));
        });
    }

    @Test
    public void case16() {
        assertThrows(IllegalArgumentException.class, () -> {
            Assertx.mustNotBlank("    ", "Test Assertx#mustNotBlank");
        });
    }

    @Test
    public void case17() {
        assertThrows(RuntimeException.class, () -> {
            Assertx.mustNotBlank("    ", () -> new RuntimeException("Test Assertx#mustNotBlank"));
        });
    }

    @Test
    public void case18() {
        assertThrows(IllegalArgumentException.class, () -> {
            Assertx.mustNullOrBlank("    1", "Test Assertx#mustNullOrBlank");
        });
    }

    @Test
    public void case19() {
        assertThrows(RuntimeException.class, () -> {
            Assertx.mustNullOrBlank("    1", () -> new RuntimeException("Test Assertx#mustNullOrBlank"));
        });
    }

    @Test
    public void case20() {
        assertThrows(IllegalArgumentException.class, () -> {
            Assertx.mustNotEmpty(new String[0], "Test Assertx#mustNotEmpty");
        });
    }

    @Test
    public void case21() {
        assertThrows(RuntimeException.class, () -> {
            Assertx.mustNotEmpty(new String[0], () -> new RuntimeException("Test Assertx#mustNotEmpty"));
        });
    }

    @Test
    public void case22() {
        assertThrows(IllegalArgumentException.class, () -> {
            Assertx.mustNullOrEmpty(new String[]{"test"}, "Test Assertx#mustNullOrEmpty");
        });
    }

    @Test
    public void case23() {
        assertThrows(RuntimeException.class, () -> {
            Assertx.mustNullOrEmpty(new String[]{"test"}, () -> new RuntimeException("Test Assertx#mustNullOrEmpty"));
        });
    }

    @Test
    public void case24() {
        assertThrows(IllegalArgumentException.class, () -> {
            Assertx.mustNotEmpty(new ArrayList<>(), "Test Assertx#mustNullOrEmpty");
        });
    }

    @Test
    public void case25() {
        assertThrows(RuntimeException.class, () -> {
            Assertx.mustNotEmpty(new ArrayList<>(), () -> new RuntimeException("Test Assertx#mustNullOrEmpty"));
        });
    }

    @Test
    public void case26() {
        assertThrows(IllegalArgumentException.class, () -> {
            Assertx.mustNullOrEmpty(List.of("test"), "Test Assertx#mustNullOrEmpty");
        });
    }

    @Test
    public void case27() {
        assertThrows(RuntimeException.class, () -> {
            Assertx.mustNullOrEmpty(List.of("test"), () -> new RuntimeException("Test Assertx#mustNullOrEmpty"));
        });
    }

    @Test
    public void case28() {
        assertThrows(IllegalArgumentException.class, () -> {
            Assertx.mustNotEmpty(Map.of(), "Test Assertx#mustNotEmpty");
        });
    }

    @Test
    public void case29() {
        assertThrows(RuntimeException.class, () -> {
            Assertx.mustNotEmpty(Map.of(), () -> new RuntimeException("Test Assertx#mustNotEmpty"));
        });
    }

    @Test
    public void case30() {
        assertThrows(IllegalArgumentException.class, () -> {
            Assertx.mustNullOrEmpty(Map.of("key", "value"), "Test Assertx#mustNullOrEmpty");
        });
    }

    @Test
    public void case31() {
        assertThrows(RuntimeException.class, () -> {
            Assertx.mustNullOrEmpty(Map.of("key", "value"), () -> new RuntimeException("Test Assertx#mustNullOrEmpty"));
        });
    }

    @Test
    public void case32() {
        assertThrows(IllegalArgumentException.class, () -> {
            Assertx.mustInstanceOf(String.class, 1, "Test Assertx#mustInstanceOf");
        });
    }

    @Test
    public void case33() {
        assertThrows(RuntimeException.class, () -> {
            Assertx.mustInstanceOf(String.class, 1, () -> new RuntimeException("Test Assertx#mustInstanceOf"));
        });
    }

    @Test
    public void case34() {
        assertThrows(IllegalArgumentException.class, () -> {
            Assertx.mustAssignableFrom(String.class, Integer.class, "Test Assertx#mustInstanceOf");
        });
    }

    @Test
    public void case35() {
        assertThrows(RuntimeException.class, () -> {
            Assertx.mustAssignableFrom(String.class, Integer.class, () -> new RuntimeException("Test Assertx#mustInstanceOf"));
        });
    }

}
