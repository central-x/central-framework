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
import jakarta.annotation.Nonnull;
import lombok.SneakyThrows;

import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.util.Base64;

/**
 * AES 加解密算法
 *
 * @author Alan Yeh
 * @since 2022/07/05
 */
public class AESImpl implements CipherImpl {
    private static final String ALGORITHM = "AES";

    // 支持 128 位、192 位、256 位
    private static final int KEY_LENGTH = 256;

    @Override
    public @Nonnull String getName() {
        return ALGORITHM;
    }

    @Override
    @SneakyThrows
    public @Nonnull KeyPair generateKeyPair() {
        KeyGenerator generator = KeyGenerator.getInstance(ALGORITHM);
        generator.init(KEY_LENGTH);

        Key key = generator.generateKey();
        return new KeyPair(key, key);
    }

    /**
     * 获取 AES Key
     * 加密密钥与解密密钥是相同的
     *
     * @param keySpec 密钥，必须是 16 + N * 8 位
     */
    @Override
    public @Nonnull Key getEncryptKey(@Nonnull String keySpec) throws GeneralSecurityException {
        return this.getKey(keySpec);
    }

    /**
     * 获取 AES Key
     * 加密密钥与解密密钥是相同的
     *
     * @param keySpec 密钥，必须是 16 + N * 8 位
     */
    @Override
    public @Nonnull Key getDecryptKey(@Nonnull String keySpec) throws GeneralSecurityException {
        return this.getKey(keySpec);
    }

    private @Nonnull Key getKey(@Nonnull String keySpec) {
        Assertx.mustNotBlank(keySpec, "Argument 'keySpec' must not blank");
        Assertx.mustTrue((keySpec.length() - 16) % 8 == 0, "密钥必须是 16 + N * 8 位");

        return new SecretKeySpec(Base64.getDecoder().decode(keySpec), ALGORITHM);
    }

    @Override
    public @Nonnull byte[] encrypt(@Nonnull byte[] data, @Nonnull Key key) throws GeneralSecurityException, IOException {
        return this.cipher(javax.crypto.Cipher.ENCRYPT_MODE, data, key);
    }

    @Override
    public @Nonnull byte[] decrypt(@Nonnull byte[] data, @Nonnull Key key) throws GeneralSecurityException, IOException {
        return this.cipher(javax.crypto.Cipher.DECRYPT_MODE, data, key);
    }

    private byte[] cipher(int mode, byte[] data, @Nonnull Key key) throws GeneralSecurityException {
        // Cipher 对象完成加密操作
        javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance(ALGORITHM);

        cipher.init(mode, key);

        return cipher.doFinal(data);
    }
}
