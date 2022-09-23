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
    public void case2() {
        assertThrows(IllegalArgumentException.class, () -> Assertx.mustTrue("test".getBytes().length < 1, "Test Assertx#mustTrue"));

        assertThrows(IllegalStateException.class, () -> Assertx.mustTrue("test".getBytes().length < 1, IllegalStateException::new, "Test Assertx#mustTrue"));

        assertThrows(IllegalStateException.class, () -> Assertx.mustTrue("test".getBytes().length < 1, () -> new IllegalStateException("Test Assertx#mustTrue")));
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
        Integer obj = null;
        assertThrows(IllegalArgumentException.class, () -> Assertx.requireNotNull(obj, "Test Assertx#{}mustNotNull"));

        assertThrows(IllegalStateException.class, () -> Assertx.requireNotNull(obj, IllegalStateException::new, "Test Assertx#{}mustNotNull"));

        assertThrows(IllegalStateException.class, () -> Assertx.requireNotNull(obj, () -> new IllegalStateException("Test Assertx#{}mustNotNull")));
    }

    /**
     * @see Assertx#mustNotNull
     */
    @Test
    public void case7() {
        Integer obj = null;
        assertThrows(IllegalArgumentException.class, () -> Assertx.mustNotNull(obj, "Test Assertx#{}mustNotNull"));

        assertThrows(IllegalStateException.class, () -> Assertx.mustNotNull(obj, IllegalStateException::new, "Test Assertx#{}mustNotNull"));

        assertThrows(IllegalStateException.class, () -> Assertx.mustNotNull(obj, () -> new IllegalStateException("Test Assertx#{}mustNotNull")));
    }

    /**
     * @see Assertx#requireNotEmpty
     */
    @Test
    public void case8() {
        assertThrows(IllegalArgumentException.class, () -> Assertx.requireNotEmpty("", "Test Assertx#mustNotEmpty"));

        assertThrows(IllegalStateException.class, () -> Assertx.requireNotEmpty("", IllegalStateException::new, "Test Assertx#mustNotEmpty"));

        assertThrows(IllegalStateException.class, () -> Assertx.requireNotEmpty("", () -> new IllegalStateException("Test Assertx#mustNotEmpty")));
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
        assertThrows(IllegalArgumentException.class, () -> Assertx.requireNotBlank("    ", "Test Assertx#mustNotBlank"));

        assertThrows(IllegalStateException.class, () -> Assertx.requireNotBlank("    ", IllegalStateException::new, "Test Assertx#mustNotBlank"));

        assertThrows(IllegalStateException.class, () -> Assertx.requireNotBlank("    ", () -> new IllegalStateException("Test Assertx#mustNotBlank")));
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
        assertThrows(IllegalArgumentException.class, () -> Assertx.requireNotEmpty(new String[0], "Test Assertx#mustNotEmpty"));

        assertThrows(IllegalStateException.class, () -> Assertx.requireNotEmpty(new String[0], IllegalStateException::new, "Test Assertx#mustNotEmpty"));

        assertThrows(IllegalStateException.class, () -> Assertx.requireNotEmpty(new String[0], () -> new IllegalStateException("Test Assertx#mustNotEmpty")));
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
        assertThrows(IllegalArgumentException.class, () -> Assertx.requireNotEmpty(new ArrayList<>(), "Test Assertx#mustNullOrEmpty"));

        assertThrows(IllegalStateException.class, () -> Assertx.requireNotEmpty(new ArrayList<>(), IllegalStateException::new, "Test Assertx#mustNullOrEmpty"));

        assertThrows(IllegalStateException.class, () -> Assertx.requireNotEmpty(new ArrayList<>(), () -> new IllegalStateException("Test Assertx#mustNullOrEmpty")));
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
        assertThrows(IllegalArgumentException.class, () -> Assertx.requireNotEmpty(Map.of(), "Test Assertx#mustNotEmpty"));

        assertThrows(IllegalStateException.class, () -> Assertx.requireNotEmpty(Map.of(), IllegalStateException::new, "Test Assertx#mustNotEmpty"));

        assertThrows(IllegalStateException.class, () -> Assertx.requireNotEmpty(Map.of(), () -> new IllegalStateException("Test Assertx#mustNotEmpty")));
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
        assertThrows(IllegalArgumentException.class, () -> Assertx.requireInstanceOf(String.class, 1, "Test Assertx#mustInstanceOf"));

        assertThrows(IllegalStateException.class, () -> Assertx.requireInstanceOf(String.class, 1, IllegalStateException::new, "Test Assertx#mustInstanceOf"));

        assertThrows(IllegalStateException.class, () -> Assertx.requireInstanceOf(String.class, 1, () -> new IllegalStateException("Test Assertx#mustInstanceOf")));
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
     * @see Assertx#mustInstanceOf
     */
    @Test
    public void case26() {
        assertThrows(IllegalArgumentException.class, () -> Assertx.mustAssignableFrom(String.class, Integer.class, "Test Assertx#mustInstanceOf"));

        assertThrows(IllegalStateException.class, () -> Assertx.mustAssignableFrom(String.class, Integer.class, IllegalStateException::new, "Test Assertx#mustInstanceOf"));

        assertThrows(IllegalStateException.class, () -> Assertx.mustAssignableFrom(String.class, Integer.class, () -> new IllegalStateException("Test Assertx#mustInstanceOf")));
    }

}
