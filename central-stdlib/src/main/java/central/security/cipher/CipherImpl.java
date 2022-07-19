package central.security.cipher;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.util.Base64;

/**
 * 加密
 *
 * @author Alan Yeh
 * @since 2022/07/05
 */
public interface CipherImpl {
    /**
     * 加解密算法名称
     */
    String getName();

    /**
     * 生成新的密钥对
     */
    KeyPair generateKeyPair();

    /**
     * 根据 Base64 字符串生成用于加密的密钥
     *
     * @param keySpec Base64 字符串
     * @return 用于加密的密钥
     */
    Key getEncryptKey(String keySpec) throws GeneralSecurityException;

    /**
     * 根据 Base64 字符串生成用于解密的密钥
     *
     * @param keySpec Base64 字符串
     * @return 用于解密的密钥
     */
    Key getDecryptKey(String keySpec) throws GeneralSecurityException;

    /**
     * 对字符串加密，加密后输出 Base64 字符串
     *
     * @param content    字符串
     * @param encryptKey 加密密钥
     */
    default String encrypt(String content, Key encryptKey) throws GeneralSecurityException, IOException {
        byte[] encrypted = this.encrypt(content.getBytes(StandardCharsets.UTF_8), encryptKey);
        return Base64.getEncoder().encodeToString(encrypted);
    }

    /**
     * 加密字节码
     *
     * @param data       字节码
     * @param encryptKey 加密密钥
     * @return 被加密后的字节码
     */
    byte[] encrypt(byte[] data, Key encryptKey) throws GeneralSecurityException, IOException;

    /**
     * 对字符串解密，待解密字符串必须是 Base64 字符串
     *
     * @param content    字符串
     * @param decryptKey 密钥
     */
    default String decrypt(String content, Key decryptKey) throws GeneralSecurityException, IOException {
        byte[] decrypted = this.decrypt(Base64.getDecoder().decode(content), decryptKey);
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    /**
     * 解密字节码
     *
     * @param data       字节码
     * @param decryptKey 解密密钥
     * @return 被解密后的字节码
     */
    byte[] decrypt(byte[] data, Key decryptKey) throws GeneralSecurityException, IOException;
}
