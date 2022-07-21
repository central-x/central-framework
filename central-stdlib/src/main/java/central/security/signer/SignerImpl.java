/*
 * MIT License
 *
 * Copyright (c) 2022-present Alan Yeh <alan@yeh.cn>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
