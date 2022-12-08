/*
 * MIT License
 *
 * Copyright (c) 2022-present Alan Yeh <alan@yeh.cn>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package central.lang;

import central.lang.reflect.TypeReference;
import central.util.Collectionx;
import central.util.Mapx;
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

    /**
     * @see Assertx#mustTrue
     */
    @Test
    public void case1() {
        assertThrows(IllegalArgumentException.class, () -> Assertx.mustTrue("test".getBytes().length < 1, "Test Assertx#mustTrue"));

        assertThrows(IllegalStateException.class, () -> Assertx.mustTrue("test".getBytes().length < 1, IllegalStateException::new, "Test Assertx#mustTrue"));

        assertThrows(IllegalStateException.class, () -> Assertx.mustTrue("test".getBytes().length < 1, () -> new IllegalStateException("Test Assertx#mustTrue")));
    }

    /**
     * @see Assertx#mustFalse
     */
    @Test
    public void case2() {
        assertThrows(IllegalArgumentException.class, () -> Assertx.mustFalse("test".getBytes().length > 1, "Test Assertx#mustFalse"));

        assertThrows(IllegalStateException.class, () -> Assertx.mustFalse("test".getBytes().length > 1, IllegalStateException::new, "Test Assertx#mustFalse"));

        assertThrows(IllegalStateException.class, () -> Assertx.mustFalse("test".getBytes().length > 1, () -> new IllegalStateException("Test Assertx#mustFalse")));
    }

    /**
     * @see Assertx#mustEquals
     */
    @Test
    public void case3() {
        assertThrows(IllegalArgumentException.class, () -> Assertx.mustEquals("a", 1, "Test Assertx#mustEquals"));

        assertThrows(IllegalStateException.class, () -> Assertx.mustEquals("a", 1, IllegalStateException::new, "Test Assertx#mustEquals"));

        assertThrows(IllegalStateException.class, () -> Assertx.mustEquals("a", 1, () -> new IllegalStateException("Test Assertx#mustEquals")));
    }

    /**
     * @see Assertx#mustNotEquals
     */
    @Test
    public void case4() {
        assertThrows(IllegalArgumentException.class, () -> Assertx.mustNotEquals("1", "1", "Test Assertx#mustNotEquals"));

        assertThrows(IllegalStateException.class, () -> Assertx.mustNotEquals("1", "1", IllegalStateException::new, "Test Assertx#mustNotEquals"));

        assertThrows(IllegalStateException.class, () -> Assertx.mustNotEquals("1", "1", () -> new IllegalStateException("Test Assertx#mustNotEquals")));
    }

    /**
     * @see Assertx#mustNull
     */
    @Test
    public void case5() {
        assertThrows(IllegalArgumentException.class, () -> Assertx.mustNull("1", "Test Assertx#mustNull"));

        assertThrows(IllegalStateException.class, () -> Assertx.mustNull("1", IllegalStateException::new, "Test Assertx#mustNull"));

        assertThrows(IllegalStateException.class, () -> Assertx.mustNull("1", () -> new IllegalStateException("Test Assertx#mustNull")));
    }

    /**
     * @see Assertx#requireNotNull
     */
    @Test
    public void case6() {
        var array = new String[0];
        assertThrows(IllegalArgumentException.class, () -> {
            var value = Assertx.requireNotNull(Arrayx.getFirstOrNull(array), "Test Assertx#requireNotNull");
            assertNotNull(value);
        });

        assertThrows(IllegalStateException.class, () -> {
            var value = Assertx.requireNotNull(Arrayx.getFirstOrNull(array), IllegalStateException::new, "Test Assertx#requireNotNull");
            assertNotNull(value);
        });

        assertThrows(IllegalStateException.class, () -> {
            var value = Assertx.requireNotNull(Arrayx.getFirstOrNull(array), () -> new IllegalStateException("Test Assertx#requireNotNull"));
            assertNotNull(value);
        });
    }

    /**
     * @see Assertx#mustNotNull
     */
    @Test
    public void case7() {
        var array = new String[0];
        assertThrows(IllegalArgumentException.class, () -> Assertx.mustNotNull(Arrayx.getFirstOrNull(array), "Test Assertx#mustNotNull"));

        assertThrows(IllegalStateException.class, () -> Assertx.mustNotNull(Arrayx.getFirstOrNull(array), IllegalStateException::new, "Test Assertx#mustNotNull"));

        assertThrows(IllegalStateException.class, () -> Assertx.mustNotNull(Arrayx.getFirstOrNull(array), () -> new IllegalStateException("Test Assertx#mustNotNull")));
    }

    /**
     * @see Assertx#requireNotEmpty
     */
    @Test
    public void case8() {
        assertThrows(IllegalArgumentException.class, () -> {
            var value = Assertx.requireNotEmpty("", "Test Assertx#requireNotEmpty");
            assertTrue(Stringx.isNotEmpty(value));
        });

        assertThrows(IllegalStateException.class, () -> {
            var value = Assertx.requireNotEmpty("", IllegalStateException::new, "Test Assertx#requireNotEmpty");
            assertTrue(Stringx.isNotEmpty(value));
        });

        assertThrows(IllegalStateException.class, () -> {
            var value = Assertx.requireNotEmpty("", () -> new IllegalStateException("Test Assertx#requireNotEmpty"));
            assertTrue(Stringx.isNotEmpty(value));
        });
    }

    /**
     * @see Assertx#mustNotEmpty
     */
    @Test
    public void case9() {
        assertThrows(IllegalArgumentException.class, () -> Assertx.mustNotEmpty("", "Test Assertx#mustNotEmpty"));

        assertThrows(IllegalStateException.class, () -> Assertx.mustNotEmpty("", IllegalStateException::new, "Test Assertx#mustNotEmpty"));

        assertThrows(IllegalStateException.class, () -> Assertx.mustNotEmpty("", () -> new IllegalStateException("Test Assertx#mustNotEmpty")));
    }


    /**
     * @see Assertx#mustNullOrEmpty
     */
    @Test
    public void case10() {
        assertThrows(IllegalArgumentException.class, () -> Assertx.mustNullOrEmpty("test", "Test Assertx#mustNullOrEmpty"));

        assertThrows(IllegalStateException.class, () -> Assertx.mustNullOrEmpty("test", IllegalStateException::new, "Test Assertx#mustNullOrEmpty"));

        assertThrows(IllegalStateException.class, () -> Assertx.mustNullOrEmpty("test", () -> new IllegalStateException("Test Assertx#mustNullOrEmpty")));
    }

    /**
     * @see Assertx#requireNotBlank
     */
    @Test
    public void case11() {
        assertThrows(IllegalArgumentException.class, () -> {
            var value = Assertx.requireNotBlank("    ", "Test Assertx#requireNotBlank");
            assertTrue(Stringx.isNotBlank(value));
        });

        assertThrows(IllegalStateException.class, () -> {
            var value = Assertx.requireNotBlank("    ", IllegalStateException::new, "Test Assertx#requireNotBlank");
            assertTrue(Stringx.isNotBlank(value));
        });

        assertThrows(IllegalStateException.class, () -> {
            var value = Assertx.requireNotBlank("    ", () -> new IllegalStateException("Test Assertx#requireNotBlank"));
            assertTrue(Stringx.isNotBlank(value));
        });
    }

    /**
     * @see Assertx#mustNotBlank
     */
    @Test
    public void case12() {
        assertThrows(IllegalArgumentException.class, () -> Assertx.mustNotBlank("    ", "Test Assertx#mustNotBlank"));

        assertThrows(IllegalStateException.class, () -> Assertx.mustNotBlank("    ", IllegalStateException::new, "Test Assertx#mustNotBlank"));

        assertThrows(IllegalStateException.class, () -> Assertx.mustNotBlank("    ", () -> new IllegalStateException("Test Assertx#mustNotBlank")));
    }

    /**
     * @see Assertx#mustNullOrBlank
     */
    @Test
    public void case13() {
        assertThrows(IllegalArgumentException.class, () -> Assertx.mustNullOrBlank("    1", "Test Assertx#mustNullOrBlank"));

        assertThrows(IllegalStateException.class, () -> Assertx.mustNullOrBlank("    1", IllegalStateException::new, "Test Assertx#mustNullOrBlank"));

        assertThrows(IllegalStateException.class, () -> Assertx.mustNullOrBlank("    1", () -> new IllegalStateException("Test Assertx#mustNullOrBlank")));
    }

    /**
     * @see Assertx#requireNotEmpty
     */
    @Test
    public void case14() {
        assertThrows(IllegalArgumentException.class, () -> {
            var value = Assertx.requireNotEmpty(new String[0], "Test Assertx#requireNotEmpty");
            assertTrue(Arrayx.isNotEmpty(value));
        });

        assertThrows(IllegalStateException.class, () -> {
            var value = Assertx.requireNotEmpty(new String[0], IllegalStateException::new, "Test Assertx#requireNotEmpty");
            assertTrue(Arrayx.isNotEmpty(value));

        });

        assertThrows(IllegalStateException.class, () -> {
            var value = Assertx.requireNotEmpty(new String[0], () -> new IllegalStateException("Test Assertx#requireNotEmpty"));
            assertTrue(Arrayx.isNotEmpty(value));
        });
    }

    /**
     * @see Assertx#mustNotEmpty
     */
    @Test
    public void case15() {
        assertThrows(IllegalArgumentException.class, () -> Assertx.mustNotEmpty(new String[0], "Test Assertx#mustNotEmpty"));

        assertThrows(IllegalStateException.class, () -> Assertx.mustNotEmpty(new String[0], IllegalStateException::new, "Test Assertx#mustNotEmpty"));

        assertThrows(IllegalStateException.class, () -> Assertx.mustNotEmpty(new String[0], () -> new IllegalStateException("Test Assertx#mustNotEmpty")));
    }

    /**
     * @see Assertx#mustNullOrEmpty
     */
    @Test
    public void case16() {
        assertThrows(IllegalArgumentException.class, () -> Assertx.mustNullOrEmpty(new String[]{"test"}, "Test Assertx#mustNullOrEmpty"));

        assertThrows(IllegalStateException.class, () -> Assertx.mustNullOrEmpty(new String[]{"test"}, IllegalStateException::new, "Test Assertx#mustNullOrEmpty"));

        assertThrows(IllegalStateException.class, () -> Assertx.mustNullOrEmpty(new String[]{"test"}, () -> new IllegalStateException("Test Assertx#mustNullOrEmpty")));
    }

    /**
     * @see Assertx#requireNotEmpty
     */
    @Test
    public void case17() {
        assertThrows(IllegalArgumentException.class, () -> {
            var value = Assertx.requireNotEmpty(new ArrayList<>(), "Test Assertx#requireNotEmpty");
            assertTrue(Collectionx.isNotEmpty(value));
        });

        assertThrows(IllegalStateException.class, () -> {
            var value = Assertx.requireNotEmpty(new ArrayList<>(), IllegalStateException::new, "Test Assertx#requireNotEmpty");
            assertTrue(Collectionx.isNotEmpty(value));
        });

        assertThrows(IllegalStateException.class, () -> {
            var value = Assertx.requireNotEmpty(new ArrayList<>(), () -> new IllegalStateException("Test Assertx#requireNotEmpty"));
            assertTrue(Collectionx.isNotEmpty(value));
        });
    }

    /**
     * @see Assertx#mustNotEmpty
     */
    @Test
    public void case18() {
        assertThrows(IllegalArgumentException.class, () -> Assertx.mustNotEmpty(new ArrayList<>(), "Test Assertx#mustNullOrEmpty"));

        assertThrows(IllegalStateException.class, () -> Assertx.mustNotEmpty(new ArrayList<>(), IllegalStateException::new, "Test Assertx#mustNullOrEmpty"));

        assertThrows(IllegalStateException.class, () -> Assertx.mustNotEmpty(new ArrayList<>(), () -> new IllegalStateException("Test Assertx#mustNullOrEmpty")));
    }

    /**
     * @see Assertx#mustNullOrEmpty
     */
    @Test
    public void case19() {
        assertThrows(IllegalArgumentException.class, () -> Assertx.mustNullOrEmpty(List.of("test"), "Test Assertx#mustNullOrEmpty"));

        assertThrows(IllegalStateException.class, () -> Assertx.mustNullOrEmpty(List.of("test"), IllegalStateException::new, "Test Assertx#mustNullOrEmpty"));

        assertThrows(IllegalStateException.class, () -> Assertx.mustNullOrEmpty(List.of("test"), () -> new IllegalStateException("Test Assertx#mustNullOrEmpty")));
    }

    /**
     * @see Assertx#requireNotEmpty
     */
    @Test
    public void case20() {
        assertThrows(IllegalArgumentException.class, () -> {
            var value = Assertx.requireNotEmpty(Map.of(), "Test Assertx#requireNotEmpty");
            assertTrue(Mapx.isNotEmpty(value));
        });

        assertThrows(IllegalStateException.class, () -> {
            var value = Assertx.requireNotEmpty(Map.of(), IllegalStateException::new, "Test Assertx#requireNotEmpty");
            assertTrue(Mapx.isNotEmpty(value));
        });

        assertThrows(IllegalStateException.class, () -> {
            var value = Assertx.requireNotEmpty(Map.of(), () -> new IllegalStateException("Test Assertx#requireNotEmpty"));
            assertTrue(Mapx.isNotEmpty(value));
        });
    }

    /**
     * @see Assertx#mustNotEmpty
     */
    @Test
    public void case21() {
        assertThrows(IllegalArgumentException.class, () -> Assertx.mustNotEmpty(Map.of(), "Test Assertx#mustNotEmpty"));

        assertThrows(IllegalStateException.class, () -> Assertx.mustNotEmpty(Map.of(), IllegalStateException::new, "Test Assertx#mustNotEmpty"));

        assertThrows(IllegalStateException.class, () -> Assertx.mustNotEmpty(Map.of(), () -> new IllegalStateException("Test Assertx#mustNotEmpty")));
    }

    /**
     * @see Assertx#mustNullOrEmpty
     */
    @Test
    public void case22() {
        assertThrows(IllegalArgumentException.class, () -> Assertx.mustNullOrEmpty(Map.of("key", "value"), "Test Assertx#mustNullOrEmpty"));

        assertThrows(IllegalStateException.class, () -> Assertx.mustNullOrEmpty(Map.of("key", "value"), IllegalStateException::new, "Test Assertx#mustNullOrEmpty"));

        assertThrows(IllegalStateException.class, () -> Assertx.mustNullOrEmpty(Map.of("key", "value"), () -> new IllegalStateException("Test Assertx#mustNullOrEmpty")));
    }

    /**
     * @see Assertx#requireInstanceOf
     */
    @Test
    public void case23() {
        assertThrows(IllegalArgumentException.class, () -> {
            var value = Assertx.requireInstanceOf(TypeReference.of(String.class), 1, "Test Assertx#requireInstanceOf");
            assertTrue(String.class.isAssignableFrom(value.getClass()));
        });

        assertThrows(IllegalStateException.class, () -> {
            var value = Assertx.requireInstanceOf(TypeReference.of(String.class), 1, IllegalStateException::new, "Test Assertx#requireInstanceOf");
            assertTrue(String.class.isAssignableFrom(value.getClass()));
        });

        assertThrows(IllegalStateException.class, () -> {
            var value = Assertx.requireInstanceOf(TypeReference.of(String.class), 1, () -> new IllegalStateException("Test Assertx#requireInstanceOf"));
            assertTrue(String.class.isAssignableFrom(value.getClass()));
        });

        assertThrows(IllegalArgumentException.class, () -> {
            var value = Assertx.requireInstanceOf(String.class, 1, "Test Assertx#requireInstanceOf");
            assertTrue(String.class.isAssignableFrom(value.getClass()));
        });

        assertThrows(IllegalStateException.class, () -> {
            var value = Assertx.requireInstanceOf(String.class, 1, IllegalStateException::new, "Test Assertx#requireInstanceOf");
            assertTrue(String.class.isAssignableFrom(value.getClass()));
        });

        assertThrows(IllegalStateException.class, () -> {
            var value = Assertx.requireInstanceOf(String.class, 1, () -> new IllegalStateException("Test Assertx#requireInstanceOf"));
            assertTrue(String.class.isAssignableFrom(value.getClass()));
        });
    }

    /**
     * @see Assertx#mustInstanceOf
     */
    @Test
    public void case24() {
        assertThrows(IllegalArgumentException.class, () -> Assertx.mustInstanceOf(String.class, 1, "Test Assertx#mustInstanceOf"));

        assertThrows(IllegalStateException.class, () -> Assertx.mustInstanceOf(String.class, 1, IllegalStateException::new, "Test Assertx#mustInstanceOf"));

        assertThrows(IllegalStateException.class, () -> Assertx.mustInstanceOf(String.class, 1, () -> new IllegalStateException("Test Assertx#mustInstanceOf")));
    }

    /**
     * @see Assertx#requireAssignableFrom
     */
    @Test
    public void case25() {
        assertThrows(IllegalArgumentException.class, () -> {
            var value = Assertx.requireAssignableFrom(String.class, Integer.class, "Test Assertx#requireAssignableFrom");
            assertSame(String.class, value);
        });

        assertThrows(IllegalStateException.class, () -> {
            var value = Assertx.requireAssignableFrom(String.class, Integer.class, IllegalStateException::new, "Test Assertx#requireAssignableFrom");
            assertSame(String.class, value);
        });

        assertThrows(IllegalStateException.class, () -> {
            var value = Assertx.requireAssignableFrom(String.class, Integer.class, () -> new IllegalStateException("Test Assertx#requireAssignableFrom"));
            assertSame(String.class, value);
        });
    }

    /**
     * @see Assertx#mustAssignableFrom
     */
    @Test
    public void case26() {
        assertThrows(IllegalArgumentException.class, () -> Assertx.mustAssignableFrom(String.class, Integer.class, "Test Assertx#mustInstanceOf"));

        assertThrows(IllegalStateException.class, () -> Assertx.mustAssignableFrom(String.class, Integer.class, IllegalStateException::new, "Test Assertx#mustInstanceOf"));

        assertThrows(IllegalStateException.class, () -> Assertx.mustAssignableFrom(String.class, Integer.class, () -> new IllegalStateException("Test Assertx#mustInstanceOf")));
    }

}
