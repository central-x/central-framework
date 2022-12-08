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

package central.util;

import central.util.converter.Converter;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Converterx Test Cases
 *
 * @author Alan Yeh
 * @since 2022/07/11
 */
public class TestConvertx {

    /**
     * Boolean Convert
     */
    @Test
    public void case1() {
        // Boolean
        assertTrue(Convertx.Default().support(Boolean.class, Boolean.class));
        Boolean target = Convertx.Default().convert(Boolean.TRUE, Boolean.class);
        assertEquals(Boolean.TRUE, target);

        target = Convertx.Default().convert(Boolean.FALSE, Boolean.class);
        assertEquals(Boolean.FALSE, target);

        target = Convertx.Default().convert(true, Boolean.class);
        assertEquals(Boolean.TRUE, target);

        target = Convertx.Default().convert(false, Boolean.class);
        assertEquals(Boolean.FALSE, target);

        target = Convertx.Default().convert(true, boolean.class);
        assertEquals(Boolean.TRUE, target);

        target = Convertx.Default().convert(false, boolean.class);
        assertEquals(Boolean.FALSE, target);


        // String
        var strSource = "true";
        assertTrue(Convertx.Default().support(strSource.getClass(), Boolean.class));
        target = Convertx.Default().convert(strSource, Boolean.class);
        assertEquals(Boolean.TRUE, target);

        strSource = "1";
        target = Convertx.Default().convert(strSource, Boolean.class);
        assertEquals(Boolean.TRUE, target);

        strSource = "false";
        target = Convertx.Default().convert(strSource, Boolean.class);
        assertEquals(Boolean.FALSE, target);

        strSource = "0";
        target = Convertx.Default().convert(strSource, Boolean.class);
        assertEquals(Boolean.FALSE, target);

        strSource = "test";
        target = Convertx.Default().convert(strSource, Boolean.class);
        assertEquals(Boolean.FALSE, target);

        // Integer
        Integer intSource = 1;
        assertTrue(Convertx.Default().support(intSource.getClass(), Boolean.class));
        target = Convertx.Default().convert(intSource, Boolean.class);
        assertEquals(Boolean.TRUE, target);

        intSource = 0;
        target = Convertx.Default().convert(intSource, Boolean.class);
        assertEquals(Boolean.FALSE, target);

        intSource = -1;
        target = Convertx.Default().convert(intSource, Boolean.class);
        assertEquals(Boolean.TRUE, target);

        // Long
        Long longSource = 1L;
        assertTrue(Convertx.Default().support(longSource.getClass(), Boolean.class));
        target = Convertx.Default().convert(longSource, Boolean.class);
        assertEquals(Boolean.TRUE, target);

        longSource = 0L;
        target = Convertx.Default().convert(longSource, Boolean.class);
        assertEquals(Boolean.FALSE, target);

        longSource = -1L;
        target = Convertx.Default().convert(longSource, Boolean.class);
        assertEquals(Boolean.TRUE, target);
    }

    /**
     * Byte Convert
     */
    @Test
    public void case2() {
        // Byte
        Byte byteSource = Byte.MAX_VALUE;
        assertTrue(Convertx.Default().support(Byte.class, Byte.class));
        Byte target = Convertx.Default().convert(byteSource, Byte.class);
        assertEquals(byteSource, target);

        target = Convertx.Default().convert(byteSource, byte.class);
        assertEquals(byteSource, target);

        // Long
        Long longSource = byteSource.longValue();
        assertTrue(Convertx.Default().support(Long.class, Byte.class));
        target = Convertx.Default().convert(longSource, Byte.class);
        assertEquals(byteSource, target);

        // Integer
        Integer intSource = byteSource.intValue();
        assertTrue(Convertx.Default().support(Integer.class, Byte.class));
        target = Convertx.Default().convert(intSource, Byte.class);
        assertEquals(byteSource, target);
    }

    /**
     * Double Convert
     */
    @Test
    public void case3() {
        // Double
        Double doubleSource = Double.parseDouble("123");
        assertTrue(Convertx.Default().support(Double.class, Double.class));
        Double target = Convertx.Default().convert(doubleSource, Double.class);
        assertEquals(doubleSource, target);

        target = Convertx.Default().convert(doubleSource, double.class);
        assertEquals(doubleSource, target);

        // Long
        Long longSource = doubleSource.longValue();
        assertTrue(Convertx.Default().support(Long.class, Double.class));
        target = Convertx.Default().convert(longSource, Double.class);
        assertEquals(doubleSource, target);

        // Integer
        Integer intSource = doubleSource.intValue();
        assertTrue(Convertx.Default().support(Integer.class, Double.class));
        target = Convertx.Default().convert(intSource, Double.class);
        assertEquals(doubleSource, target);

        // Float
        Float floatSource = doubleSource.floatValue();
        assertTrue(Convertx.Default().support(Float.class, Double.class));
        target = Convertx.Default().convert(floatSource, Double.class);
        assertEquals(doubleSource, target);
    }

    /**
     * Float Convert
     */
    @Test
    public void case4() {
        // Float
        Float floatSource = Float.parseFloat("123");
        assertTrue(Convertx.Default().support(Float.class, Float.class));
        Float target = Convertx.Default().convert(floatSource, Float.class);
        assertEquals(floatSource, target);

        target = Convertx.Default().convert(floatSource, float.class);
        assertEquals(floatSource, target);

        // Long
        Long longSource = floatSource.longValue();
        assertTrue(Convertx.Default().support(Long.class, Float.class));
        target = Convertx.Default().convert(longSource, Float.class);
        assertEquals(floatSource, target);

        // Integer
        Integer intSource = floatSource.intValue();
        assertTrue(Convertx.Default().support(Integer.class, Float.class));
        target = Convertx.Default().convert(intSource, Float.class);
        assertEquals(floatSource, target);

        // Double
        Double doubleSource = floatSource.doubleValue();
        assertTrue(Convertx.Default().support(Float.class, Float.class));
        target = Convertx.Default().convert(floatSource, Float.class);
        assertEquals(floatSource, target);
    }

    /**
     * Integer Convert
     */
    @Test
    public void case5() {
        // Integer
        Integer intSource = 1;
        assertTrue(Convertx.Default().support(Integer.class, Integer.class));
        Integer target = Convertx.Default().convert(intSource, Integer.class);
        assertEquals(intSource, target);

        target = Convertx.Default().convert(intSource, int.class);
        assertEquals(intSource, target);

        // Long
        Long longSource = 1L;
        assertTrue(Convertx.Default().support(Long.class, Integer.class));
        target = Convertx.Default().convert(longSource, Integer.class);
        assertEquals(intSource, target);

        // String
        String stringSource = "1";
        assertTrue(Convertx.Default().support(String.class, Integer.class));
        target = Convertx.Default().convert(stringSource, Integer.class);
        assertEquals(intSource, target);
    }

    /**
     * Long Convert
     */
    @Test
    public void case6() {
        // Long
        Long longSource = 1L;
        assertTrue(Convertx.Default().support(Long.class, Long.class));
        Long target = Convertx.Default().convert(longSource, Long.class);
        assertEquals(longSource, target);

        target = Convertx.Default().convert(longSource, long.class);
        assertEquals(longSource, target);

        // Integer
        Integer intSource = 1;
        assertTrue(Convertx.Default().support(Integer.class, Long.class));
        target = Convertx.Default().convert(intSource, Long.class);
        assertEquals(longSource, target);

        // String
        String stringSource = "1";
        assertTrue(Convertx.Default().support(String.class, Long.class));
        target = Convertx.Default().convert(stringSource, Long.class);
        assertEquals(longSource, target);

        // Date
        Date dataSource = new Date();
        assertTrue(Convertx.Default().support(Date.class, Long.class));
        target = Convertx.Default().convert(dataSource, Long.class);
        assertEquals(dataSource.getTime(), target.longValue());

        // Timestamp
        Timestamp timestampSource = new Timestamp(System.currentTimeMillis());
        assertTrue(Convertx.Default().support(Timestamp.class, Long.class));
        target = Convertx.Default().convert(timestampSource, Long.class);
        assertEquals(timestampSource.getTime(), target.longValue());
    }

    /**
     * Short Convert
     */
    @Test
    public void case7() {
        // Short
        Short shortSource = Short.parseShort("64");
        assertTrue(Convertx.Default().support(Short.class, Short.class));
        Short target = Convertx.Default().convert(shortSource, Short.class);
        assertEquals(shortSource, target);

        target = Convertx.Default().convert(shortSource, short.class);
        assertEquals(shortSource, target);

        // Long
        Long longSource = shortSource.longValue();
        assertTrue(Convertx.Default().support(Long.class, Short.class));
        target = Convertx.Default().convert(longSource, Short.class);
        assertEquals(shortSource, target);

        // Integer
        Integer intSource = shortSource.intValue();
        assertTrue(Convertx.Default().support(Integer.class, Short.class));
        target = Convertx.Default().convert(intSource, Short.class);
        assertEquals(shortSource, target);

        // Double
        Double doubleSource = shortSource.doubleValue();
        assertTrue(Convertx.Default().support(Float.class, Short.class));
        target = Convertx.Default().convert(doubleSource, Short.class);
        assertEquals(shortSource, target);
    }

    /**
     * String Convert
     */
    @Test
    public void case8() {
        // String
        String stringSource = "string";
        assertTrue(Convertx.Default().support(String.class, String.class));
        String target = Convertx.Default().convert(stringSource, String.class);
        assertEquals(stringSource, target);

        // Long
        Long longSource = 1L;
        assertTrue(Convertx.Default().support(Long.class, String.class));
        target = Convertx.Default().convert(longSource, String.class);
        assertEquals("1", target);

        // Integer
        Integer intSource = 1;
        assertTrue(Convertx.Default().support(Integer.class, String.class));
        target = Convertx.Default().convert(intSource, String.class);
        assertEquals("1", target);
    }


    /**
     * Timestamp Convert
     */
    @Test
    public void case9() {
        // Timestamp
        var timestampSource = new Timestamp(System.currentTimeMillis());
        assertTrue(Convertx.Default().support(Timestamp.class, Timestamp.class));
        Timestamp target = Convertx.Default().convert(timestampSource, Timestamp.class);
        assertEquals(timestampSource.getTime(), target.getTime());

        // Long
        Long longSource = timestampSource.getTime();
        assertTrue(Convertx.Default().support(longSource.getClass(), Timestamp.class));
        target = Convertx.Default().convert(longSource, Timestamp.class);
        assertEquals(timestampSource.getTime(), target.getTime());
    }

    /**
     * Test Register
     */
    @Test
    public void case10() {
        var converter = new Convertx(List.of(new LazyConverter()));

        assertTrue(converter.support(Integer.class, LazyValue.class));
        assertTrue(converter.support(String.class, LazyValue.class));
        assertTrue(converter.support(Double.class, LazyValue.class));

        var result = converter.convert(1, LazyValue.class);
        assertNotNull(result);
        assertTrue(result instanceof LazyValue<?>);
        var value = result.get();
        assertEquals(1, value);
    }

    /**
     * Test Register/Deregister
     */
    @Test
    public void case11() {
        var converter = new Convertx();

        assertFalse(converter.support(Integer.class, LazyValue.class));
        assertFalse(converter.support(String.class, LazyValue.class));
        assertFalse(converter.support(Double.class, LazyValue.class));

        LazyConverter lazy = new LazyConverter();
        converter.register(lazy);

        assertTrue(converter.support(Integer.class, LazyValue.class));
        assertTrue(converter.support(String.class, LazyValue.class));
        assertTrue(converter.support(Double.class, LazyValue.class));

        converter.deregister(lazy);

        assertFalse(converter.support(Integer.class, LazyValue.class));
        assertFalse(converter.support(String.class, LazyValue.class));
        assertFalse(converter.support(Double.class, LazyValue.class));

    }

    private static class LazyConverter implements Converter<LazyValue<?>> {

        @Override
        public boolean support(Class<?> source) {
            return true;
        }

        @Override
        public LazyValue<?> convert(Object source) {
            return new LazyValue<>(() -> source);
        }
    }
}
