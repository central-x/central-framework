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
import jakarta.annotation.Nonnull;
import lombok.Getter;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.engines.SM4Engine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

import java.io.IOException;
import java.io.Serial;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

/**
 * 国密 SM4 加解密算法 (对称算法)
 * 无线局域网标准的分组数据算法。对称加密，密钥长度和分组长度均为 128 位
 * 本实现采用 CBC 分组加密
 *
 * @author Alan Yeh
 * @since 2022/07/10
 */
public class SM4Impl implements CipherImpl {
    // SM4 supports 128-bit(16 bytes) secret key
    public static final int KEY_SIZE = 128 / 8;
    // One block contains 16 bytes
    public static final int BLOCK_SIZE = 16;
    // Initial vector's size is 16 bytes
    public static final int IV_SIZE = 16;

    @Override
    public @Nonnull String getName() {
        return "SM4";
    }

    @Override
    public @Nonnull KeyPair generateKeyPair() {
        CipherKeyGenerator generator = new CipherKeyGenerator();
        // To provide secure randomness and key length as input
        // to prepare generate private key
        generator.init(new KeyGenerationParameters(new SecureRandom(), KEY_SIZE * 8));

        // To generate key
        byte[] bytes = generator.generateKey();
        Key key = new SM4Key(bytes);
        return new KeyPair(key, key);
    }

    private static class SM4Key implements Key {
        @Serial
        private static final long serialVersionUID = 6807028080317910008L;

        @Getter
        private final byte[] encoded;

        public SM4Key(byte[] encoded) {
            this.encoded = encoded;
        }

        @Override
        public String getAlgorithm() {
            return "SM4";
        }

        @Override
        public String getFormat() {
            return "String";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SM4Key sm4Key = (SM4Key) o;
            return Arrays.equals(this.encoded, sm4Key.encoded);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(this.encoded);
        }
    }

    @Override
    public @Nonnull Key getEncryptKey(@Nonnull String keySpec) throws GeneralSecurityException {
        return new SM4Key(Base64.getDecoder().decode(keySpec));
    }

    @Override
    public @Nonnull Key getDecryptKey(@Nonnull String keySpec) throws GeneralSecurityException {
        return new SM4Key(Base64.getDecoder().decode(keySpec));
    }

    @Override
    public @Nonnull byte[] encrypt(@Nonnull byte[] data, @Nonnull Key encryptKey) throws GeneralSecurityException, IOException {
        // 向量
        byte[] iv = new byte[IV_SIZE];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);

        // To get the value padded into input
        int padding = 16 - data.length % BLOCK_SIZE;
        // The plaintext with padding value
        byte[] plainBytesWithPadding = new byte[data.length + padding];
        System.arraycopy(data, 0, plainBytesWithPadding, 0, data.length);
        // The padder adds PKCS7 padding to the input, which makes its length to
        // become an integral multiple of 16 bytes
        PKCS7Padding padder = new PKCS7Padding();
        // To add padding
        padder.addPadding(plainBytesWithPadding, data.length);

        CBCBlockCipher encryptor = new CBCBlockCipher(new SM4Engine());
        // To provide key and initialisation vector as input
        encryptor.init(true, new ParametersWithIV(new KeyParameter(encryptKey.getEncoded()), iv));

        byte[] output = new byte[plainBytesWithPadding.length + IV_SIZE];
        // To encrypt the input_p in CBC mode
        int blockCount = plainBytesWithPadding.length / BLOCK_SIZE;
        int outOffset = 0;
        for (int i = 0; i < blockCount; i++) {
            encryptor.processBlock(plainBytesWithPadding, outOffset, output, outOffset + BLOCK_SIZE);
            outOffset += BLOCK_SIZE;
        }

        // The IV locates on the first block of ciphertext
        System.arraycopy(iv, 0, output, 0, BLOCK_SIZE);
        return output;
    }

    @Override
    public @Nonnull byte[] decrypt(@Nonnull byte[] data, @Nonnull Key decryptKey) throws GeneralSecurityException, IOException {
        byte[] iv = new byte[IV_SIZE];
        System.arraycopy(data, 0, iv, 0, BLOCK_SIZE);

        CBCBlockCipher decryptor = new CBCBlockCipher(new SM4Engine());
        // To prepare the decryption
        decryptor.init(false, new ParametersWithIV(new KeyParameter(decryptKey.getEncoded()), iv));
        byte[] outputWithPadding = new byte[data.length - BLOCK_SIZE];
        // To decrypt the input in CBC mode
        int blockCount = data.length / BLOCK_SIZE;
        int outOffset = 0;
        for (int i = 1; i < blockCount; i++) {
            decryptor.processBlock(data, outOffset + BLOCK_SIZE, outputWithPadding, outOffset);
            outOffset += BLOCK_SIZE;
        }

        int p = outputWithPadding[outputWithPadding.length - 1];
        // To ensure that the padding of output_p is valid
        if (p > BLOCK_SIZE || p < 0x01) {
            throw new GeneralSecurityException("There no exists such padding!");
        }
        for (int i = 0; i < p; i++) {
            if (outputWithPadding[outputWithPadding.length - i - 1] != p) {
                throw new GeneralSecurityException("Padding is invalid!");
            }
        }

        // To remove the padding from output and obtain plaintext
        byte[] output = new byte[outputWithPadding.length - p];
        System.arraycopy(outputWithPadding, 0, output, 0, output.length);
        return output;
    }
}
