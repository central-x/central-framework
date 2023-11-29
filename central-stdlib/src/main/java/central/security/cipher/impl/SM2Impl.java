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
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.*;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;

import java.io.IOException;
import java.io.Serial;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

/**
 * 国密 SM2 加解密 (非对称算法)
 * SM2算法就是ECC椭圆曲线密码机制，但在签名、密钥交换方面不同于ECDSA、ECDH等国际标准，而是采取了更为安全的机制。另外，SM2推荐了一条256位的曲线作为标准曲线。
 * SM2标准包括总则，数字签名算法，密钥交换协议，公钥加密算法四个部分，并在每个部分的附录详细说明了实现的相关细节及示例。
 * <p>
 * 密文有 C1|C2|C3 和 C1｜C3｜C2 的区别，本实现采用 C1|C2|C3 作为密文
 *
 * @author Alan Yeh
 * @since 2022/07/10
 */
public class SM2Impl implements CipherImpl {
    private static final ECNamedCurveParameterSpec PARAMS = ECNamedCurveTable.getParameterSpec("sm2p256v1");
    private static final ECCurve CURVE = PARAMS.getCurve();
    private static final ECDomainParameters DOMAIN_PARAMS = new ECDomainParameters(CURVE, PARAMS.getG(), PARAMS.getN(), PARAMS.getH());

    public static final int COORDS_SIZE = 32;
    public static final int POINT_SIZE = COORDS_SIZE * 2 + 1;
    // The length of sm3 output is 32 bytes
    public static final int SM3DIGEST_LENGTH = 32;

    @Override
    public @Nonnull String getName() {
        return "SM2";
    }

    @Override
    public @Nonnull KeyPair generateKeyPair() {
        SecureRandom random = new SecureRandom();

        ECKeyGenerationParameters keyGenerationParams = new ECKeyGenerationParameters(DOMAIN_PARAMS, random);
        ECKeyPairGenerator keyPairGenerator = new ECKeyPairGenerator();

        // To generate the key pair
        keyPairGenerator.init(keyGenerationParams);
        AsymmetricCipherKeyPair keyPair = keyPairGenerator.generateKeyPair();

        Key encryptKey = new SM2EncryptKey(((ECPublicKeyParameters) keyPair.getPublic()).getQ().getEncoded(false));
        Key decryptKey = new SM2DecryptKey(((ECPrivateKeyParameters) keyPair.getPrivate()).getD().toByteArray());

        return new KeyPair(encryptKey, decryptKey);
    }

    @Override
    public @Nonnull Key getEncryptKey(@Nonnull String keySpec) throws GeneralSecurityException {
        return new SM2EncryptKey(Base64.getDecoder().decode(keySpec));
    }

    private static class SM2EncryptKey implements Key {
        @Serial
        private static final long serialVersionUID = 7138366658809908375L;

        @Getter
        private final byte[] encoded;

        public SM2EncryptKey(byte[] encoded) {
            this.encoded = encoded;
        }

        @Override
        public String getAlgorithm() {
            return "SM2";
        }

        @Override
        public String getFormat() {
            return "X.509";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SM2EncryptKey that = (SM2EncryptKey) o;
            return Arrays.equals(this.encoded, that.encoded);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(this.encoded);
        }
    }

    @Override
    public @Nonnull Key getDecryptKey(@Nonnull String keySpec) throws GeneralSecurityException {
        return new SM2DecryptKey(Base64.getDecoder().decode(keySpec));
    }

    private static class SM2DecryptKey implements Key {
        @Serial
        private static final long serialVersionUID = 644493133111452334L;
        private static final int PRIVKEY_SIZE = 32;

        @Getter
        private final byte[] encoded;

        public SM2DecryptKey(byte[] encoded) {
            this.encoded = new byte[PRIVKEY_SIZE];
            if (encoded.length > PRIVKEY_SIZE) {
                System.arraycopy(encoded, encoded.length - PRIVKEY_SIZE, this.encoded, 0, PRIVKEY_SIZE);
            } else {
                System.arraycopy(encoded, 0, this.encoded, PRIVKEY_SIZE - encoded.length, encoded.length);
            }
        }

        @Override
        public String getAlgorithm() {
            return "SM2";
        }

        @Override
        public String getFormat() {
            return "X.509";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SM2DecryptKey that = (SM2DecryptKey) o;
            return Arrays.equals(encoded, that.encoded);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(encoded);
        }
    }

    @Override
    public @Nonnull byte[] encrypt(@Nonnull byte[] data, @Nonnull Key key) throws GeneralSecurityException, IOException {
        SecureRandom random = new SecureRandom();
        ECPoint pubKeyPoint = CURVE.decodePoint(key.getEncoded());
        ECPublicKeyParameters pubKey = new ECPublicKeyParameters(pubKeyPoint, DOMAIN_PARAMS);
        ParametersWithRandom params = new ParametersWithRandom(pubKey, random);

        SM2Engine encryptor = new SM2Engine();

        encryptor.init(true, params);

        // To generate the twisted ciphertext c1c2c3.
        // The latest standard specification indicates that the correct ordering is
        // c1c3c2
        byte[] c1c2c3;
        try {
            c1c2c3 = encryptor.processBlock(data, 0, data.length);
        } catch (InvalidCipherTextException ex) {
            throw new GeneralSecurityException(ex.getMessage(), ex);
        }

        return c1c2c3;
        // get correct output c1c3c2 from c1c2c3
//        byte[] c1c3c2 = new byte[c1c2c3.length];
//        System.arraycopy(c1c2c3, 0, c1c3c2, 0, POINT_SIZE);
//        System.arraycopy(c1c2c3, POINT_SIZE, c1c3c2, POINT_SIZE + SM3DIGEST_LENGTH, data.length);
//        System.arraycopy(c1c2c3, POINT_SIZE + data.length, c1c3c2, POINT_SIZE, SM3DIGEST_LENGTH);
//
//        return c1c3c2;
    }

    @Override
    public @Nonnull byte[] decrypt(@Nonnull byte[] data, @Nonnull Key key) throws GeneralSecurityException, IOException {
        ECPrivateKeyParameters privKey = new ECPrivateKeyParameters(new BigInteger(1, key.getEncoded()), DOMAIN_PARAMS);

        SM2Engine decryptor = new SM2Engine();
        decryptor.init(false, privKey);

        // To get c1c2c3 from ciphertext whose ordering is c1c3c2
//        byte[] c1c2c3 = new byte[data.length];
//        System.arraycopy(data, 0, c1c2c3, 0, POINT_SIZE);
//        System.arraycopy(data, POINT_SIZE, c1c2c3, data.length - SM3DIGEST_LENGTH, SM3DIGEST_LENGTH);
//        System.arraycopy(data, SM3DIGEST_LENGTH + POINT_SIZE, c1c2c3, POINT_SIZE, data.length - SM3DIGEST_LENGTH - POINT_SIZE);

        // To output the plaintext
        try {
            return decryptor.processBlock(data, 0, data.length);
        } catch (InvalidCipherTextException e) {
            throw new GeneralSecurityException(e.getMessage(), e);
        }
    }
}
