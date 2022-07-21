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

import central.security.cipher.CipherImpl;
import central.security.cipher.KeyPair;
import lombok.Getter;
import lombok.SneakyThrows;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * RSA 加解密实现
 *
 * @author Alan Yeh
 * @since 2022/07/10
 */
public class RSAImpl implements CipherImpl {
    private static final String ALGORITHM = "RSA";

    private static final int KEY_SIZE = 2048;

    @Getter
    private final String name = "RSA";

    @Override
    @SneakyThrows
    public KeyPair generateKeyPair() {
        KeyPairGenerator generator = KeyPairGenerator.getInstance(ALGORITHM);
        generator.initialize(KEY_SIZE);

        java.security.KeyPair keyPair = generator.generateKeyPair();

        return new KeyPair(keyPair.getPublic(), keyPair.getPrivate());
    }

    /**
     * 获取公钥
     * 在 RSA 算法中，公钥用于加密
     *
     * @param keySpec Base64 字符串，通过 Base64.getEncoder().encode(publicKey.getEncoded()) 生成
     * @return 公钥
     */
    @Override
    public Key getEncryptKey(String keySpec) throws GeneralSecurityException {
        return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(keySpec)));
    }

    /**
     * 获取私钥
     * 在 RSA 算法中，私钥用于解密
     *
     * @param keySpec Base64 字符串，通过 Base64.getEncoder().encode(privateKey.getEncoded()) 生成
     * @return 私钥
     */
    @Override
    public Key getDecryptKey(String keySpec) throws GeneralSecurityException {
        return KeyFactory.getInstance("RSA").generatePrivate(new X509EncodedKeySpec(Base64.getDecoder().decode(keySpec)));
    }

    @Override
    public byte[] encrypt(byte[] data, Key key) throws GeneralSecurityException, IOException {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);

        int BYTE_SIZE = ((RSAKey) key).getModulus().bitLength() / 8 - 11;

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            int inputLen = data.length;
            int offset = 0;
            byte[] cache;

            while (inputLen - offset > 0) {
                if (inputLen - offset > BYTE_SIZE) {
                    cache = cipher.doFinal(data, offset, BYTE_SIZE);
                } else {
                    cache = cipher.doFinal(data, offset, inputLen - offset);
                }
                out.write(cache, 0, cache.length);
                offset += BYTE_SIZE;
            }

            return out.toByteArray();
        }
    }

    @Override
    public byte[] decrypt(byte[] data, Key key) throws GeneralSecurityException, IOException {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);

        int BYTE_SIZE = ((RSAKey) key).getModulus().bitLength() / 8;

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            int inputLen = data.length;
            int offset = 0;
            byte[] cache;

            while (inputLen - offset > 0) {
                if (inputLen - offset > BYTE_SIZE) {
                    cache = cipher.doFinal(data, offset, BYTE_SIZE);
                } else {
                    cache = cipher.doFinal(data, offset, inputLen - offset);
                }
                out.write(cache, 0, cache.length);
                offset += BYTE_SIZE;
            }

            return out.toByteArray();
        }
    }
}
