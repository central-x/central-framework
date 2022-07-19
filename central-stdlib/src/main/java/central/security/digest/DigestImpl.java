package central.security.digest;

import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * 消息摘要算法
 *
 * @author Alan Yeh
 * @since 2022/07/05
 */
public interface DigestImpl {

    /**
     * 摘要算法名称
     */
    String getName();

    /**
     * 将字节码进行消息摘要
     *
     * @param bytes 字节码
     * @return 消息摘要
     */
    String digest(byte[] bytes);

    /**
     * 将字符串进行消息摘要
     *
     * @param value   字符串
     * @param charset 字符串的编码
     * @return 消息摘要
     */
    String digest(String value, Charset charset);

    /**
     * 对输入流进行消息摘要
     *
     * @param is 输入流
     * @return 消息摘要
     */
    String digest(InputStream is);
}
