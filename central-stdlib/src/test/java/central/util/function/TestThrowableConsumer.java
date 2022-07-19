package central.util.function;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * ThrowableConsumer Test Cases
 *
 * @author Alan Yeh
 * @since 2022/07/11
 */
public class TestThrowableConsumer {
    @Test
    public void case1() {
        var lambda = ThrowableConsumer.of((String str) -> {
            if ("throws".equals(str)) {
                throw new IllegalArgumentException();
            } else {
                Assertions.assertEquals("test", str);
            }
        });

        lambda.accept("test");
        Assertions.assertThrows(IllegalArgumentException.class, () -> lambda.accept("throws"));
    }

    @Test
    public void case2() {
        AtomicInteger countDown = new AtomicInteger(1);

        ThrowableConsumer.of((String str) -> {
            countDown.decrementAndGet();
            Assertions.assertEquals("test", str);
        }).accept("test");
        Assertions.assertEquals(0, countDown.get());
    }
}
