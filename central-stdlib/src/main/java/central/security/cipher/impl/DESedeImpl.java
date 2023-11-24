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

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.util.Base64;

/**
 * AES 加解密算法
 *
 * @author Alan Yeh
 * @since 2022/07/10
 */
public class DESedeImpl implements CipherImpl {
    private static final String ALGORITHM = "DESede";
    private static final String IV = "01234567";
    private static final String CIPHER_ALGORITHM = "DESede/CBC/PKCS5Padding";

    @Override
    public @Nonnull String getName() {
        return ALGORITHM;
    }

    @Override
    @SneakyThrows
    public @Nonnull KeyPair generateKeyPair() {
        KeyGenerator generator = KeyGenerator.getInstance(ALGORITHM);
        generator.init(168);

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

    private @Nonnull Key getKey(@Nonnull String keySpec) throws GeneralSecurityException {
        Assertx.mustNotBlank(keySpec, "Argument 'keySpec' must not null");
        byte[] keyData = Base64.getDecoder().decode(keySpec);
        Assertx.mustTrue(keyData.length >= 8, "不是有效的 DESede 密钥: " + keySpec);

        // 从原始密钥数据创建 DESedeKeySpec 对象
        DESedeKeySpec spec = new DESedeKeySpec(Base64.getDecoder().decode(keySpec));
        // 创建一个密钥工厂，然后用它把 DESKeySpec 转换成 SecretKey 对象
        return SecretKeyFactory.getInstance(ALGORITHM).generateSecret(spec);
    }

    @Override
    public @Nonnull byte[] encrypt(@Nonnull byte[] data, @Nonnull Key key) throws GeneralSecurityException, IOException {
        return this.cipher(Cipher.ENCRYPT_MODE, data, key);
    }

    @Override
    public @Nonnull byte[] decrypt(@Nonnull byte[] data, @Nonnull Key key) throws GeneralSecurityException, IOException {
        return this.cipher(Cipher.DECRYPT_MODE, data, key);
    }

    private byte[] cipher(int mode, byte[] data, Key key) throws GeneralSecurityException {
        // Cipher 对象完成加密操作
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        IvParameterSpec ips = new IvParameterSpec(IV.getBytes(StandardCharsets.UTF_8));
        cipher.init(mode, key, ips);

        return cipher.doFinal(data);
    }
}
