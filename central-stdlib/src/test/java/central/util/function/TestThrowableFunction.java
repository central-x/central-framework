package central.util.function;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Throwable Function Test Cases
 *
 * @author Alan Yeh
 * @since 2022/07/11
 */
public class TestThrowableFunction {
    /**
     * o
     */
    @Test
    public void case1() {
        var function = ThrowableFunction.of((String str) -> Integer.parseInt(str)).ignoreThrows();

        Assertions.assertEquals(Integer.valueOf(1), function.apply("1"));
        Assertions.assertNull(function.apply("test"));
    }

    @Test
    public void case2() {
        var function = ThrowableFunction.of((String str) -> Integer.parseInt(str))
                .catchThrows(cause -> Integer.valueOf(2));

        Assertions.assertEquals(Integer.valueOf(1), function.apply("1"));
        Assertions.assertEquals(Integer.valueOf(2), function.apply("test"));
    }

    @Test
    public void case3() {
        var function = ThrowableFunction.of((String str) -> Integer.parseInt(str))
                .catchThrows(cause -> {
                    throw new IllegalArgumentException("Illegal string");
                });

        Assertions.assertEquals(Integer.valueOf(1), function.apply("1"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> function.apply("test"));
    }

    @Test
    public void test4() {
        var function = ThrowableFunction.of((String str) -> Integer.parseInt(str))
                .andThen((Integer value) -> {
                    Assertions.assertTrue(value instanceof Integer);
                    return value.toString();
                })
                .andThen((String value) -> {
                    Assertions.assertTrue(value instanceof String);
                    return Long.valueOf(value);
                })
                .andThen((Long value) -> {
                    Assertions.assertTrue(value instanceof Long);
                    return value.doubleValue();
                });

        Assertions.assertEquals(Double.valueOf(2), function.apply("2"));
        Assertions.assertThrows(NumberFormatException.class, () -> function.apply("test"));
    }

    @Test
    public void test5(){
        AtomicInteger countDown = new AtomicInteger(1);

        var function = ThrowableFunction.of((String str) -> Integer.parseInt(str))
                        .andThen((var value) -> {
                            Assertions.assertEquals(Integer.valueOf(1), value);
                            countDown.decrementAndGet();
                        }).ignoreThrows();

        function.accept("1");

        Assertions.assertEquals(0, countDown.get());
    }
}
