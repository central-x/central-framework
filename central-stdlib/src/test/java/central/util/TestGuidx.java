package central.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Guidx Test Cases
 *
 * @author Alan Yeh
 * @since 2022/07/11
 */
public class TestGuidx {
    @Test
    public void case1(){
        for (int i = 0; i < 10_000; i ++){
            Assertions.assertEquals(19, Guidx.nextID().length());
        }
    }
}
