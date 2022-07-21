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
