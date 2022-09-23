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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

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
        Assertions.assertTrue(Convertx.Default().support(Boolean.class, Boolean.class));
        Boolean target = Convertx.Default().convert(Boolean.TRUE, Boolean.class);
        Assertions.assertEquals(Boolean.TRUE, target);

        target = Convertx.Default().convert(Boolean.FALSE, Boolean.class);
        Assertions.assertEquals(Boolean.FALSE, target);

        target = Convertx.Default().convert(true, Boolean.class);
        Assertions.assertEquals(Boolean.TRUE, target);

        target = Convertx.Default().convert(false, Boolean.class);
        Assertions.assertEquals(Boolean.FALSE, target);

        target = Convertx.Default().convert(true, boolean.class);
        Assertions.assertEquals(Boolean.TRUE, target);

        target = Convertx.Default().convert(false, boolean.class);
        Assertions.assertEquals(Boolean.FALSE, target);


        // String
        var strSource = "true";
        Assertions.assertTrue(Convertx.Default().support(strSource.getClass(), Boolean.class));
        target = Convertx.Default().convert(strSource, Boolean.class);
        Assertions.assertEquals(Boolean.TRUE, target);

        strSource = "1";
        target = Convertx.Default().convert(strSource, Boolean.class);
        Assertions.assertEquals(Boolean.TRUE, target);

        strSource = "false";
        target = Convertx.Default().convert(strSource, Boolean.class);
        Assertions.assertEquals(Boolean.FALSE, target);

        strSource = "0";
        target = Convertx.Default().convert(strSource, Boolean.class);
        Assertions.assertEquals(Boolean.FALSE, target);

        strSource = "test";
        target = Convertx.Default().convert(strSource, Boolean.class);
        Assertions.assertEquals(Boolean.FALSE, target);

        // Integer
        Integer intSource = 1;
        Assertions.assertTrue(Convertx.Default().support(intSource.getClass(), Boolean.class));
        target = Convertx.Default().convert(intSource, Boolean.class);
        Assertions.assertEquals(Boolean.TRUE, target);

        intSource = 0;
        target = Convertx.Default().convert(intSource, Boolean.class);
        Assertions.assertEquals(Boolean.FALSE, target);

        intSource = -1;
        target = Convertx.Default().convert(intSource, Boolean.class);
        Assertions.assertEquals(Boolean.TRUE, target);

        // Long
        Long longSource = 1L;
        Assertions.assertTrue(Convertx.Default().support(longSource.getClass(), Boolean.class));
        target = Convertx.Default().convert(longSource, Boolean.class);
        Assertions.assertEquals(Boolean.TRUE, target);

        longSource = 0L;
        target = Convertx.Default().convert(longSource, Boolean.class);
        Assertions.assertEquals(Boolean.FALSE, target);

        longSource = -1L;
        target = Convertx.Default().convert(longSource, Boolean.class);
        Assertions.assertEquals(Boolean.TRUE, target);
    }

    /**
     * Byte Convert
     */
    @Test
    public void case2() {
        // Byte
        Byte byteSource = Byte.MAX_VALUE;
        Assertions.assertTrue(Convertx.Default().support(Byte.class, Byte.class));
        Byte target = Convertx.Default().convert(byteSource, Byte.class);
        Assertions.assertEquals(byteSource, target);

        target = Convertx.Default().convert(byteSource, byte.class);
        Assertions.assertEquals(byteSource, target);

        // Long
        Long longSource = byteSource.longValue();
        Assertions.assertTrue(Convertx.Default().support(Long.class, Byte.class));
        target = Convertx.Default().convert(longSource, Byte.class);
        Assertions.assertEquals(byteSource, target);

        // Integer
        Integer intSource = byteSource.intValue();
        Assertions.assertTrue(Convertx.Default().support(Integer.class, Byte.class));
        target = Convertx.Default().convert(intSource, Byte.class);
        Assertions.assertEquals(byteSource, target);
    }

    /**
     * Double Convert
     */
    @Test
    public void case3() {
        // Double
        Double doubleSource = Double.parseDouble("123");
        Assertions.assertTrue(Convertx.Default().support(Double.class, Double.class));
        Double target = Convertx.Default().convert(doubleSource, Double.class);
        Assertions.assertEquals(doubleSource, target);

        target = Convertx.Default().convert(doubleSource, double.class);
        Assertions.assertEquals(doubleSource, target);

        // Long
        Long longSource = doubleSource.longValue();
        Assertions.assertTrue(Convertx.Default().support(Long.class, Double.class));
        target = Convertx.Default().convert(longSource, Double.class);
        Assertions.assertEquals(doubleSource, target);

        // Integer
        Integer intSource = doubleSource.intValue();
        Assertions.assertTrue(Convertx.Default().support(Integer.class, Double.class));
        target = Convertx.Default().convert(intSource, Double.class);
        Assertions.assertEquals(doubleSource, target);

        // Float
        Float floatSource = doubleSource.floatValue();
        Assertions.assertTrue(Convertx.Default().support(Float.class, Double.class));
        target = Convertx.Default().convert(floatSource, Double.class);
        Assertions.assertEquals(doubleSource, target);
    }

    /**
     * Float Convert
     */
    @Test
    public void case4() {
        // Float
        Float floatSource = Float.parseFloat("123");
        Assertions.assertTrue(Convertx.Default().support(Float.class, Float.class));
        Float target = Convertx.Default().convert(floatSource, Float.class);
        Assertions.assertEquals(floatSource, target);

        target = Convertx.Default().convert(floatSource, float.class);
        Assertions.assertEquals(floatSource, target);

        // Long
        Long longSource = floatSource.longValue();
        Assertions.assertTrue(Convertx.Default().support(Long.class, Float.class));
        target = Convertx.Default().convert(longSource, Float.class);
        Assertions.assertEquals(floatSource, target);

        // Integer
        Integer intSource = floatSource.intValue();
        Assertions.assertTrue(Convertx.Default().support(Integer.class, Float.class));
        target = Convertx.Default().convert(intSource, Float.class);
        Assertions.assertEquals(floatSource, target);

        // Double
        Double doubleSource = floatSource.doubleValue();
        Assertions.assertTrue(Convertx.Default().support(Float.class, Float.class));
        target = Convertx.Default().convert(floatSource, Float.class);
        Assertions.assertEquals(floatSource, target);
    }

    /**
     * Integer Convert
     */
    @Test
    public void case5() {
        // Integer
        Integer intSource = 1;
        Assertions.assertTrue(Convertx.Default().support(Integer.class, Integer.class));
        Integer target = Convertx.Default().convert(intSource, Integer.class);
        Assertions.assertEquals(intSource, target);

        target = Convertx.Default().convert(intSource, int.class);
        Assertions.assertEquals(intSource, target);

        // Long
        Long longSource = 1L;
        Assertions.assertTrue(Convertx.Default().support(Long.class, Integer.class));
        target = Convertx.Default().convert(longSource, Integer.class);
        Assertions.assertEquals(intSource, target);

        // String
        String stringSource = "1";
        Assertions.assertTrue(Convertx.Default().support(String.class, Integer.class));
        target = Convertx.Default().convert(stringSource, Integer.class);
        Assertions.assertEquals(intSource, target);
    }

    /**
     * Long Convert
     */
    @Test
    public void case6() {
        // Long
        Long longSource = 1L;
        Assertions.assertTrue(Convertx.Default().support(Long.class, Long.class));
        Long target = Convertx.Default().convert(longSource, Long.class);
        Assertions.assertEquals(longSource, target);

        target = Convertx.Default().convert(longSource, long.class);
        Assertions.assertEquals(longSource, target);

        // Integer
        Integer intSource = 1;
        Assertions.assertTrue(Convertx.Default().support(Integer.class, Long.class));
        target = Convertx.Default().convert(intSource, Long.class);
        Assertions.assertEquals(longSource, target);

        // String
        String stringSource = "1";
        Assertions.assertTrue(Convertx.Default().support(String.class, Long.class));
        target = Convertx.Default().convert(stringSource, Long.class);
        Assertions.assertEquals(longSource, target);

        // Date
        Date dataSource = new Date();
        Assertions.assertTrue(Convertx.Default().support(Date.class, Long.class));
        target = Convertx.Default().convert(dataSource, Long.class);
        Assertions.assertEquals(dataSource.getTime(), target.longValue());

        // Timestamp
        Timestamp timestampSource = new Timestamp(System.currentTimeMillis());
        Assertions.assertTrue(Convertx.Default().support(Timestamp.class, Long.class));
        target = Convertx.Default().convert(timestampSource, Long.class);
        Assertions.assertEquals(timestampSource.getTime(), target.longValue());
    }

    /**
     * Short Convert
     */
    @Test
    public void case7() {
        // Short
        Short shortSource = Short.parseShort("64");
        Assertions.assertTrue(Convertx.Default().support(Short.class, Short.class));
        Short target = Convertx.Default().convert(shortSource, Short.class);
        Assertions.assertEquals(shortSource, target);

        target = Convertx.Default().convert(shortSource, short.class);
        Assertions.assertEquals(shortSource, target);

        // Long
        Long longSource = shortSource.longValue();
        Assertions.assertTrue(Convertx.Default().support(Long.class, Short.class));
        target = Convertx.Default().convert(longSource, Short.class);
        Assertions.assertEquals(shortSource, target);

        // Integer
        Integer intSource = shortSource.intValue();
        Assertions.assertTrue(Convertx.Default().support(Integer.class, Short.class));
        target = Convertx.Default().convert(intSource, Short.class);
        Assertions.assertEquals(shortSource, target);

        // Double
        Double doubleSource = shortSource.doubleValue();
        Assertions.assertTrue(Convertx.Default().support(Float.class, Short.class));
        target = Convertx.Default().convert(doubleSource, Short.class);
        Assertions.assertEquals(shortSource, target);
    }

    /**
     * String Convert
     */
    @Test
    public void case8() {
        // String
        String stringSource = "string";
        Assertions.assertTrue(Convertx.Default().support(String.class, String.class));
        String target = Convertx.Default().convert(stringSource, String.class);
        Assertions.assertEquals(stringSource, target);

        // Long
        Long longSource = 1L;
        Assertions.assertTrue(Convertx.Default().support(Long.class, String.class));
        target = Convertx.Default().convert(longSource, String.class);
        Assertions.assertEquals("1", target);

        // Integer
        Integer intSource = 1;
        Assertions.assertTrue(Convertx.Default().support(Integer.class, String.class));
        target = Convertx.Default().convert(intSource, String.class);
        Assertions.assertEquals("1", target);
    }


    /**
     * Timestamp Convert
     */
    @Test
    public void case9() {
        var origin = new Timestamp(System.currentTimeMillis());

        // Timestamp
        Assertions.assertTrue(Convertx.Default().support(Timestamp.class, Timestamp.class));
        Timestamp target = Convertx.Default().convert(origin, Timestamp.class);
        Assertions.assertEquals(origin.getTime(), target.getTime());

        // Long
        Long longSource = origin.getTime();
        Assertions.assertTrue(Convertx.Default().support(longSource.getClass(), Timestamp.class));
        target = Convertx.Default().convert(longSource, Timestamp.class);
        Assertions.assertEquals(origin.getTime(), target.getTime());
    }

    /**
     * Test Register
     */
    @Test
    public void case10() {
        var converter = new Convertx(List.of(new LazyConverter()));

        Assertions.assertTrue(converter.support(Integer.class, LazyValue.class));
        Assertions.assertTrue(converter.support(String.class, LazyValue.class));
        Assertions.assertTrue(converter.support(Double.class, LazyValue.class));

        var result = converter.convert(1, LazyValue.class);
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result instanceof LazyValue<?>);
        var value = result.get();
        Assertions.assertEquals(1, value);
    }

    /**
     * Test Register/Deregister
     */
    @Test
    public void case11() {
        var converter = new Convertx();

        Assertions.assertFalse(converter.support(Integer.class, LazyValue.class));
        Assertions.assertFalse(converter.support(String.class, LazyValue.class));
        Assertions.assertFalse(converter.support(Double.class, LazyValue.class));

        LazyConverter lazy = new LazyConverter();
        converter.register(lazy);

        Assertions.assertTrue(converter.support(Integer.class, LazyValue.class));
        Assertions.assertTrue(converter.support(String.class, LazyValue.class));
        Assertions.assertTrue(converter.support(Double.class, LazyValue.class));

        converter.deregister(lazy);

        Assertions.assertFalse(converter.support(Integer.class, LazyValue.class));
        Assertions.assertFalse(converter.support(String.class, LazyValue.class));
        Assertions.assertFalse(converter.support(Double.class, LazyValue.class));

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
