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
public class TestConverterx {

    /**
     * Boolean Convert
     */
    @Test
    public void case1() {
        // Boolean
        Assertions.assertTrue(Converterx.Default().support(Boolean.class, Boolean.class));
        Boolean target = Converterx.Default().convert(Boolean.TRUE, Boolean.class);
        Assertions.assertEquals(Boolean.TRUE, target);

        target = Converterx.Default().convert(Boolean.FALSE, Boolean.class);
        Assertions.assertEquals(Boolean.FALSE, target);

        // String
        var strSource = "true";
        Assertions.assertTrue(Converterx.Default().support(strSource.getClass(), Boolean.class));
        target = Converterx.Default().convert(strSource, Boolean.class);
        Assertions.assertEquals(Boolean.TRUE, target);

        strSource = "1";
        target = Converterx.Default().convert(strSource, Boolean.class);
        Assertions.assertEquals(Boolean.TRUE, target);

        strSource = "false";
        target = Converterx.Default().convert(strSource, Boolean.class);
        Assertions.assertEquals(Boolean.FALSE, target);

        strSource = "0";
        target = Converterx.Default().convert(strSource, Boolean.class);
        Assertions.assertEquals(Boolean.FALSE, target);

        strSource = "test";
        target = Converterx.Default().convert(strSource, Boolean.class);
        Assertions.assertEquals(Boolean.FALSE, target);

        // Integer
        Integer intSource = 1;
        Assertions.assertTrue(Converterx.Default().support(intSource.getClass(), Boolean.class));
        target = Converterx.Default().convert(intSource, Boolean.class);
        Assertions.assertEquals(Boolean.TRUE, target);

        intSource = 0;
        target = Converterx.Default().convert(intSource, Boolean.class);
        Assertions.assertEquals(Boolean.FALSE, target);

        intSource = -1;
        target = Converterx.Default().convert(intSource, Boolean.class);
        Assertions.assertEquals(Boolean.TRUE, target);

        // Long
        Long longSource = 1L;
        Assertions.assertTrue(Converterx.Default().support(longSource.getClass(), Boolean.class));
        target = Converterx.Default().convert(longSource, Boolean.class);
        Assertions.assertEquals(Boolean.TRUE, target);

        longSource = 0L;
        target = Converterx.Default().convert(longSource, Boolean.class);
        Assertions.assertEquals(Boolean.FALSE, target);

        longSource = -1L;
        target = Converterx.Default().convert(longSource, Boolean.class);
        Assertions.assertEquals(Boolean.TRUE, target);
    }

    /**
     * Byte Convert
     */
    @Test
    public void case2() {
        // Byte
        Byte byteSource = Byte.MAX_VALUE;
        Assertions.assertTrue(Converterx.Default().support(Byte.class, Byte.class));
        Byte target = Converterx.Default().convert(byteSource, Byte.class);
        Assertions.assertEquals(byteSource, target);

        // Long
        Long longSource = byteSource.longValue();
        Assertions.assertTrue(Converterx.Default().support(Long.class, Byte.class));
        target = Converterx.Default().convert(longSource, Byte.class);
        Assertions.assertEquals(byteSource, target);

        // Integer
        Integer intSource = byteSource.intValue();
        Assertions.assertTrue(Converterx.Default().support(Integer.class, Byte.class));
        target = Converterx.Default().convert(intSource, Byte.class);
        Assertions.assertEquals(byteSource, target);
    }

    /**
     * Double Convert
     */
    @Test
    public void case3() {
        // Double
        Double doubleSource = Double.parseDouble("123");
        Assertions.assertTrue(Converterx.Default().support(Double.class, Double.class));
        Double target = Converterx.Default().convert(doubleSource, Double.class);
        Assertions.assertEquals(doubleSource, target);

        // Long
        Long longSource = doubleSource.longValue();
        Assertions.assertTrue(Converterx.Default().support(Long.class, Double.class));
        target = Converterx.Default().convert(longSource, Double.class);
        Assertions.assertEquals(doubleSource, target);

        // Integer
        Integer intSource = doubleSource.intValue();
        Assertions.assertTrue(Converterx.Default().support(Integer.class, Double.class));
        target = Converterx.Default().convert(intSource, Double.class);
        Assertions.assertEquals(doubleSource, target);

        // Float
        Float floatSource = doubleSource.floatValue();
        Assertions.assertTrue(Converterx.Default().support(Float.class, Double.class));
        target = Converterx.Default().convert(floatSource, Double.class);
        Assertions.assertEquals(doubleSource, target);
    }

    /**
     * Float Convert
     */
    @Test
    public void case4() {
        // Float
        Float floatSource = Float.parseFloat("123");
        Assertions.assertTrue(Converterx.Default().support(Float.class, Float.class));
        Float target = Converterx.Default().convert(floatSource, Float.class);
        Assertions.assertEquals(floatSource, target);

        // Long
        Long longSource = floatSource.longValue();
        Assertions.assertTrue(Converterx.Default().support(Long.class, Float.class));
        target = Converterx.Default().convert(longSource, Float.class);
        Assertions.assertEquals(floatSource, target);

        // Integer
        Integer intSource = floatSource.intValue();
        Assertions.assertTrue(Converterx.Default().support(Integer.class, Float.class));
        target = Converterx.Default().convert(intSource, Float.class);
        Assertions.assertEquals(floatSource, target);

        // Double
        Double doubleSource = floatSource.doubleValue();
        Assertions.assertTrue(Converterx.Default().support(Float.class, Float.class));
        target = Converterx.Default().convert(floatSource, Float.class);
        Assertions.assertEquals(floatSource, target);
    }

    /**
     * Integer Convert
     */
    @Test
    public void case5() {
        // Integer
        Integer intSource = 1;
        Assertions.assertTrue(Converterx.Default().support(Integer.class, Integer.class));
        Integer target = Converterx.Default().convert(intSource, Integer.class);
        Assertions.assertEquals(intSource, target);

        // Long
        Long longSource = 1L;
        Assertions.assertTrue(Converterx.Default().support(Long.class, Integer.class));
        target = Converterx.Default().convert(longSource, Integer.class);
        Assertions.assertEquals(intSource, target);

        // String
        String stringSource = "1";
        Assertions.assertTrue(Converterx.Default().support(String.class, Integer.class));
        target = Converterx.Default().convert(stringSource, Integer.class);
        Assertions.assertEquals(intSource, target);
    }

    /**
     * Long Convert
     */
    @Test
    public void case6() {
        // Long
        Long longSource = 1L;
        Assertions.assertTrue(Converterx.Default().support(Long.class, Long.class));
        Long target = Converterx.Default().convert(longSource, Long.class);
        Assertions.assertEquals(longSource, target);

        // Integer
        Integer intSource = 1;
        Assertions.assertTrue(Converterx.Default().support(Integer.class, Long.class));
        target = Converterx.Default().convert(intSource, Long.class);
        Assertions.assertEquals(longSource, target);

        // String
        String stringSource = "1";
        Assertions.assertTrue(Converterx.Default().support(String.class, Long.class));
        target = Converterx.Default().convert(stringSource, Long.class);
        Assertions.assertEquals(longSource, target);

        // Date
        Date dataSource = new Date();
        Assertions.assertTrue(Converterx.Default().support(Date.class, Long.class));
        target = Converterx.Default().convert(dataSource, Long.class);
        Assertions.assertEquals(dataSource.getTime(), target.longValue());

        // Timestamp
        Timestamp timestampSource = new Timestamp(System.currentTimeMillis());
        Assertions.assertTrue(Converterx.Default().support(Timestamp.class, Long.class));
        target = Converterx.Default().convert(timestampSource, Long.class);
        Assertions.assertEquals(timestampSource.getTime(), target.longValue());
    }

    /**
     * Short Convert
     */
    @Test
    public void case7(){
        // Short
        Short shortSource = Short.parseShort("64");
        Assertions.assertTrue(Converterx.Default().support(Short.class, Short.class));
        Short target = Converterx.Default().convert(shortSource, Short.class);
        Assertions.assertEquals(shortSource, target);

        // Long
        Long longSource = shortSource.longValue();
        Assertions.assertTrue(Converterx.Default().support(Long.class, Short.class));
        target = Converterx.Default().convert(longSource, Short.class);
        Assertions.assertEquals(shortSource, target);

        // Integer
        Integer intSource = shortSource.intValue();
        Assertions.assertTrue(Converterx.Default().support(Integer.class, Short.class));
        target = Converterx.Default().convert(intSource, Short.class);
        Assertions.assertEquals(shortSource, target);

        // Double
        Double doubleSource = shortSource.doubleValue();
        Assertions.assertTrue(Converterx.Default().support(Float.class, Short.class));
        target = Converterx.Default().convert(shortSource, Short.class);
        Assertions.assertEquals(shortSource, target);
    }

    /**
     * String Convert
     */
    @Test
    public void case8() {
        // String
        String stringSource = "string";
        Assertions.assertTrue(Converterx.Default().support(String.class, String.class));
        String target = Converterx.Default().convert(stringSource, String.class);
        Assertions.assertEquals(stringSource, target);

        // Long
        Long longSource = 1L;
        Assertions.assertTrue(Converterx.Default().support(Long.class, String.class));
        target = Converterx.Default().convert(longSource, String.class);
        Assertions.assertEquals("1", target);

        // Integer
        Integer intSource = 1;
        Assertions.assertTrue(Converterx.Default().support(Integer.class, String.class));
        target = Converterx.Default().convert(intSource, String.class);
        Assertions.assertEquals("1", target);
    }


    /**
     * Timestamp Convert
     */
    @Test
    public void case9() {
        var origin = new Timestamp(System.currentTimeMillis());

        // Timestamp
        Assertions.assertTrue(Converterx.Default().support(Timestamp.class, Timestamp.class));
        Timestamp target = Converterx.Default().convert(origin, Timestamp.class);
        Assertions.assertEquals(origin.getTime(), target.getTime());

        // Long
        Long longSource = origin.getTime();
        Assertions.assertTrue(Converterx.Default().support(longSource.getClass(), Timestamp.class));
        target = Converterx.Default().convert(longSource, Timestamp.class);
        Assertions.assertEquals(origin.getTime(), target.getTime());
    }

    /**
     * Test Register
     */
    @Test
    public void case10(){
        var converter = new Converterx(List.of(new LazyConverter()));

        Assertions.assertTrue(converter.support(Integer.class, LazyValue.class));
        Assertions.assertTrue(converter.support(String.class, LazyValue.class));
        Assertions.assertTrue(converter.support(Double.class, LazyValue.class));

        var result = converter.convert(Integer.valueOf(1), LazyValue.class);
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result instanceof LazyValue<?>);
        var value = result.get();
        Assertions.assertEquals(Integer.valueOf(1), value);
    }

    /**
     * Test Register/Deregister
     */
    @Test
    public void case11(){
        var converter = new Converterx();

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
