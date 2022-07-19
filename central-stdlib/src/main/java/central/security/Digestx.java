package central.security;

import central.security.digest.DigestImpl;
import central.security.digest.impl.SM3Impl;
import central.security.digest.impl.StandardImpl;

/**
 * 消息摘要
 *
 * @author Alan Yeh
 * @since 2022/07/05
 */
public class Digestx {
    public static final DigestImpl MD5 = new StandardImpl("MD5");
    public static final DigestImpl SHA1 = new StandardImpl("SHA1");
    public static final DigestImpl SHA224 = new StandardImpl("SHA-224");
    public static final DigestImpl SHA256 = new StandardImpl("SHA-256");
    public static final DigestImpl SHA384 = new StandardImpl("SHA-384");
    public static final DigestImpl SHA512 = new StandardImpl("SHA-512");

    /**
     * 国密算法 SM3
     * 消息摘要算法，产生 32 位长度的接要字符串
     */
    public static final DigestImpl SM3 = new SM3Impl();
}
