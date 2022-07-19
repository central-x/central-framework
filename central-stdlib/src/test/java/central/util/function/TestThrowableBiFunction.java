package central.util.function;

import org.junit.jupiter.api.Test;

/**
 * ThrowableBiFunction Test Cases
 *
 * @author Alan Yeh
 * @since 2022/07/11
 */
public class TestThrowableBiFunction {
    @Test
    public void case1(){
        ThrowableBiFunction.of((String value1, String value2) -> {
            String result = value1 + value2;
            if ("throws".equals(result)){
                throw new IllegalArgumentException();
            } else {
                return result;
            }
        });
    }
}
