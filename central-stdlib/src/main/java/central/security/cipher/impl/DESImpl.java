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

package central.security.cipher.impl;

import central.lang.Assertx;
import central.security.cipher.CipherImpl;
import central.security.cipher.KeyPair;
import lombok.Getter;
import lombok.SneakyThrows;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * DES 加解密算法
 * @author Alan Yeh
 * @since 2022/07/10
 */
public class DESImpl implements CipherImpl {
    private static final String ALGORITHM = "DES";

    @Getter
    private final String name = "DES";

    @Override
    @SneakyThrows
    public KeyPair generateKeyPair() {
        KeyGenerator generator = KeyGenerator.getInstance(ALGORITHM);
        generator.init(56);
        Key key = generator.generateKey();

        return new KeyPair(key, key);
    }

    /**
     * 获取 DES Key
     * 加密密钥和钥密密钥是相同的
     *
     * @param keySpec Key Spec
     */
    @Override
    public Key getEncryptKey(String keySpec) throws GeneralSecurityException {
        return this.getKey(keySpec);
    }

    /**
     * 获取 DES Key
     * 加密密钥和钥密密钥是相同的
     *
     * @param keySpec Key Spec
     */
    @Override
    public Key getDecryptKey(String keySpec) throws GeneralSecurityException {
        return this.getKey(keySpec);
    }

    private Key getKey(String keySpec) throws GeneralSecurityException {
        Assertx.mustNotBlank(keySpec, "Argument 'keySpec' must not blank");
        Assertx.mustTrue(keySpec.length() >= 8, "密钥必须大于 8 位的");
        // 从原始密钥数据创建 DESKeySpec 对象
        DESKeySpec spec = new DESKeySpec(Base64.getDecoder().decode(keySpec));

        // 创建一个密钥工厂，然后用它把 DESKeySpec 转换成 SecretKey 对象
        SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
        return factory.generateSecret(spec);
    }

    @Override
    public byte[] encrypt(byte[] data, Key key) throws GeneralSecurityException, IOException {
        return this.cipher(Cipher.ENCRYPT_MODE, data, key);
    }

    @Override
    public byte[] decrypt(byte[] data, Key key) throws GeneralSecurityException, IOException {
        return this.cipher(Cipher.DECRYPT_MODE, data, key);
    }

    private byte[] cipher(int mode, byte[] data, Key key) throws GeneralSecurityException {
        // 生成一个可信任的随机数据源
        SecureRandom random = new SecureRandom();

        // Cipher 对象完成加密操作
        Cipher cipher = Cipher.getInstance(ALGORITHM);

        cipher.init(mode, key, random);

        return cipher.doFinal(data);
    }
}
