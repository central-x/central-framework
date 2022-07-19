package central.util;

import java.util.UUID;

/**
 * 随机主键生成
 *
 * @author Alan Yeh
 * @since 2022/07/05
 */
public class Guidx {
    private static String digits(long val, int digits) {
        var hi = 1L << (digits * 4);
        return Numberx.toString(hi | (val & (hi - 1)), Numberx.MAX_RADIX)
                .substring(1);
    }

    public static String nextID() {
        var uuid = UUID.randomUUID();

        var result = new StringBuilder();

        result.append(digits(uuid.getMostSignificantBits() >> 32, 8));
        result.append(digits(uuid.getMostSignificantBits() >> 16, 4));
        result.append(digits(uuid.getMostSignificantBits(), 4));
        result.append(digits(uuid.getLeastSignificantBits() >> 48, 4));
        result.append(digits(uuid.getLeastSignificantBits(), 12));

        return result.toString();
    }
}
