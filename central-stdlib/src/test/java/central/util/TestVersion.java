package central.util;

import central.lang.CompareResultEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Version Test Cases
 *
 * @author Alan Yeh
 * @since 2022/07/13
 */
public class TestVersion {
    /**
     * 测试解析版本
     */
    @Test
    public void case1() {
        var version = new Version("1.2.3.45678");

        Assertions.assertEquals(1, version.getMajor());
        Assertions.assertEquals(2, version.getMinor());
        Assertions.assertEquals(3, version.getRevision());
        Assertions.assertEquals(45678, version.getBuild());
    }

    /**
     * 测试版本号对比
     */
    @Test
    public void case2() {
        var first = new Version("1.0.0");
        var second = new Version("1.0.2");

        Assertions.assertTrue(CompareResultEnum.LESS.isCompatibleWith(first.compareTo(second)));
    }
}
