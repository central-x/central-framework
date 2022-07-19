package central.util.function;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

/**
 * ThrowableSupplier Test Cases
 *
 * @author Alan Yeh
 * @since 2022/07/11
 */
public class TestThrowableSupplier {
    @Test
    public void case1() {
        Assertions.assertEquals("test", ThrowableSupplier.of(() -> "test").get());
    }

    @Test
    public void case2() {
        var test = ThrowableSupplier.of(() -> {
                    var str = "test";
                    if (str.getBytes(StandardCharsets.UTF_8).length > 1) {
                        throw new ClassCastException("Class Not Found");
                    }
                    return str;
                })
                .ignoreThrows()
                .get();
        Assertions.assertNull(test);
    }

    @Test
    public void case3() {
        var test = ThrowableSupplier.of(() -> {
                    var str = "test";
                    if (str.getBytes(StandardCharsets.UTF_8).length > 1) {
                        throw new ClassCastException("Class Not Found");
                    }
                    return str;
                })
                .catchThrows(cause -> "fallback")
                .get();
        Assertions.assertEquals("fallback", test);
    }

    @Test
    public void case4() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ThrowableSupplier.of(() -> {
                        var str = "test";
                        if (str.getBytes(StandardCharsets.UTF_8).length > 1) {
                            throw new ClassCastException("Class Not Found");
                        }
                        return str;
                    })
                    .catchThrows(cause -> {
                        throw new IllegalArgumentException(cause);
                    })
                    .get();
        });
    }

    @Test
    public void case5(){
        var result = ThrowableSupplier.of(() -> "1").andThen(Integer::parseInt);

        Assertions.assertEquals(Integer.valueOf(1), result.get());
    }
}
