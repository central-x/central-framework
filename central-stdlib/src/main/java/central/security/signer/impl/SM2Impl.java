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

package central.security.signer.impl;

import central.util.Stringx;
import central.security.signer.KeyPair;
import central.security.signer.SignerImpl;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.*;
import org.bouncycastle.crypto.signers.SM2Signer;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.Key;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.util.Arrays;
import java.util.Base64;

/**
 * 国密 SM2 签名算法
 *
 * @author Alan Yeh
 * @since 2022/07/10
 */
public class SM2Impl implements SignerImpl {
    private static final ECNamedCurveParameterSpec PARAMS = ECNamedCurveTable.getParameterSpec("sm2p256v1");
    private static final ECCurve CURVE = PARAMS.getCurve();
    private static final ECDomainParameters DOMAIN_PARAMS = new ECDomainParameters(CURVE, PARAMS.getG(), PARAMS.getN(), PARAMS.getH());

    private static final int R_SIZE = 32;
    private static final int S_SIZE = 32;

    @Getter
    private final String name = "SM2";

    @Override
    public KeyPair generateKeyPair() {
        SecureRandom random = new SecureRandom();

        ECKeyGenerationParameters keyGenerationParams = new ECKeyGenerationParameters(DOMAIN_PARAMS, random);
        ECKeyPairGenerator keyPairGenerator = new ECKeyPairGenerator();

        // To generate the key pair
        keyPairGenerator.init(keyGenerationParams);
        AsymmetricCipherKeyPair keyPair = keyPairGenerator.generateKeyPair();

        Key signKey = new SM2VerifyKey(((ECPrivateKeyParameters) keyPair.getPrivate()).getD().toByteArray());
        Key verifyKey = new SM2SignKey(((ECPublicKeyParameters) keyPair.getPublic()).getQ().getEncoded(false));

        return new KeyPair(signKey, verifyKey);
    }

    @RequiredArgsConstructor
    private static class SM2SignKey implements Key {
        private static final long serialVersionUID = 7138366658809908375L;

        @Getter
        private final byte[] encoded;

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
            SM2SignKey that = (SM2SignKey) o;
            return Arrays.equals(encoded, that.encoded);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(encoded);
        }
    }

    private static class SM2VerifyKey implements Key {
        private static final long serialVersionUID = 644493133111452334L;
        private static final int PRIVKEY_SIZE = 32;

        @Getter
        private final byte[] encoded;

        public SM2VerifyKey(byte[] encoded) {
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
            SM2VerifyKey that = (SM2VerifyKey) o;
            return Arrays.equals(encoded, that.encoded);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(encoded);
        }
    }

    @Override
    public Key getSignKey(String keySpec) {
        return new SM2SignKey(Base64.getDecoder().decode(keySpec));
    }

    @Override
    public Key getVerifyKey(String keySpec) {
        return new SM2VerifyKey(Base64.getDecoder().decode(keySpec));
    }

    private static final int BUFFER_SIZE = 8 * 1024;

    @Override
    public String sign(InputStream input, Key signKey) throws IOException, SignatureException {
        SecureRandom random = new SecureRandom();
        ECPrivateKeyParameters privKey = new ECPrivateKeyParameters(new BigInteger(1, signKey.getEncoded()), DOMAIN_PARAMS);
        CipherParameters params = new ParametersWithRandom(privKey, random);

        SM2Signer signer = new SM2Signer();
        signer.init(true, params);

        try (BufferedInputStream buffered = new BufferedInputStream(input, BUFFER_SIZE)) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int size;
            while ((size = buffered.read(buffer, 0, BUFFER_SIZE)) > 0) {
                signer.update(buffer, 0, size);
            }
        }

        byte[] encodedSignature;
        try {
            encodedSignature = signer.generateSignature();
        } catch (CryptoException ex) {
            throw new SignatureException(ex.getMessage(), ex);
        }

        ASN1Sequence sig = ASN1Sequence.getInstance(encodedSignature);
        byte[] rBytes = this.trimBigIntegerTo32Bytes(ASN1Integer.getInstance(sig.getObjectAt(0)).getValue());
        byte[] sBytes = this.trimBigIntegerTo32Bytes(ASN1Integer.getInstance(sig.getObjectAt(1)).getValue());

        byte[] signature = new byte[R_SIZE + S_SIZE];
        System.arraycopy(rBytes, 0, signature, 0, R_SIZE);
        System.arraycopy(sBytes, 0, signature, R_SIZE, S_SIZE);

        return Stringx.encodeHex(signature);
    }

    @Override
    @SneakyThrows
    public boolean verify(InputStream input, Key verifyKey, String signature) throws IOException, SignatureException {
        ECPoint pubkeyPoint = CURVE.decodePoint(verifyKey.getEncoded());
        ECPublicKeyParameters params = new ECPublicKeyParameters(pubkeyPoint, DOMAIN_PARAMS);


        SM2Signer verifier = new SM2Signer();

        verifier.init(false, params);

        try (BufferedInputStream buffered = new BufferedInputStream(input, BUFFER_SIZE)) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int offset = 0;
            int size;
            do {
                size = buffered.read(buffer, offset, BUFFER_SIZE);
                if (size > 0) {
                    verifier.update(buffer, 0, size);
                }
            } while (size >= BUFFER_SIZE);
        }

        byte[] signatureBytes = Stringx.decodeHex(signature);
        byte[] rBytes = new byte[R_SIZE];
        byte[] sBytes = new byte[S_SIZE];
        System.arraycopy(signatureBytes, 0, rBytes, 0, R_SIZE);
        System.arraycopy(signatureBytes, R_SIZE, sBytes, 0, S_SIZE);

        BigInteger r = new BigInteger(1, rBytes);
        BigInteger s = new BigInteger(1, sBytes);

        byte[] encodedSignature = new byte[0];
        try {
            encodedSignature = new DERSequence(new ASN1Encodable[]{new ASN1Integer(r), new ASN1Integer(s)}).getEncoded();
        } catch (IOException ignored) {
        }

        return verifier.verifySignature(encodedSignature);
    }

    private byte[] trimBigIntegerTo32Bytes(BigInteger b) {
        byte[] tmp = b.toByteArray();
        byte[] result = new byte[32];
        if (tmp.length > result.length) {
            System.arraycopy(tmp, tmp.length - result.length, result, 0, result.length);
        } else {
            System.arraycopy(tmp, 0, result, result.length - tmp.length, tmp.length);
        }
        return result;
    }
}
