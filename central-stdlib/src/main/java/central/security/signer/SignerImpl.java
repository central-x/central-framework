package central.security.signer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.SignatureException;

/**
 * 签名实体
 *
 * @author Alan Yeh
 * @since 2022/07/10
 */
public interface SignerImpl {
    /**
     * 签名算法名称
     */
    String getName();

    /**
     * 生成新的密钥对
     */
    KeyPair generateKeyPair();

    /**
     * 根据 Base64 字符串生成签名密钥
     *
     * @param keySpec Base64 字符串，通过 Base64.getEncoder().encode(signKey.getEncoded()) 生成
     * @return 公钥
     */
    Key getSignKey(String keySpec);

    /**
     * 根据 Base64 字符串生成验签密钥
     *
     * @param keySpec Base64 字符串，通过 Base64.getEncoder().encode(verifyKey.getEncoded()) 生成
     * @return 私钥
     */
    Key getVerifyKey(String keySpec);

    /**
     * 签名
     *
     * @param text    等签名文本
     * @param signKey 签名密钥
     * @return Base64 后的签名
     */
    default String sign(String text, Key signKey) throws IOException, SignatureException {
        return this.sign(new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8)), signKey);
    }

    /**
     * 签名
     *
     * @param input   待签名的数据流
     * @param signKey 签名密钥
     * @return Base64 后的签名
     */
    String sign(InputStream input, Key signKey) throws IOException, SignatureException;

    /**
     * 验签
     *
     * @param text      待签名文本
     * @param verifyKey 验签密钥
     * @param signature 原签名（Base64字符串）
     * @return 是否匹配签名
     */
    default boolean verify(String text, Key verifyKey, String signature) throws IOException, SignatureException {
        return this.verify(new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8)), verifyKey, signature);
    }

    /**
     * 验签
     *
     * @param input     待签名的数据流
     * @param verifyKey 验签密钥
     * @param signature 原签名（Base64字符串）
     * @return 是否匹配签名
     */
    boolean verify(InputStream input, Key verifyKey, String signature) throws IOException, SignatureException;
}
