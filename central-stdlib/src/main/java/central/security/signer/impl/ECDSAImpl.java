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

import central.lang.Stringx;
import central.security.signer.KeyPair;
import central.security.signer.SignerImpl;
import lombok.SneakyThrows;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.text.ParseException;
import java.util.Base64;

/**
 * ECDSA 签名算法
 *
 * @author Alan Yeh
 * @since 2024/01/03
 */
public abstract class ECDSAImpl implements SignerImpl {
    private static final int BUFFER_SIZE = 8 * 1024;

    @Override
    public abstract String getName();

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Override
    @SneakyThrows({NoSuchAlgorithmException.class, NoSuchProviderException.class, InvalidAlgorithmParameterException.class})
    public KeyPair generateKeyPair() {
        var keyPairGenerator = KeyPairGenerator.getInstance("EC", "BC");
        // 可以选择其他的曲线参数，如 "secp256r1" 等
        var spec = ECNamedCurveTable.getParameterSpec("secp256k1");
        keyPairGenerator.initialize(spec, new SecureRandom());
        var keyPair = keyPairGenerator.generateKeyPair();
        return new KeyPair(keyPair.getPrivate(), keyPair.getPublic());
    }

    @Override
    @SneakyThrows({NoSuchAlgorithmException.class, NoSuchProviderException.class, InvalidKeySpecException.class})
    public Key getSignKey(String keySpec) {
        return KeyFactory.getInstance("EC", "BC").generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(keySpec)));
    }

    @Override
    @SneakyThrows({NoSuchAlgorithmException.class, InvalidKeySpecException.class})
    public Key getVerifyKey(String keySpec) {
        return KeyFactory.getInstance("EC").generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(keySpec)));
    }

    @Override
    @SneakyThrows({NoSuchAlgorithmException.class, NoSuchProviderException.class, InvalidKeyException.class})
    public String sign(InputStream input, Key signKey) throws IOException, SignatureException {
        if (!(signKey instanceof PrivateKey)) {
            throw new SignatureException("Invalid signKey");
        }

        SecureRandom random = new SecureRandom();
        Signature signer = Signature.getInstance(this.getName(), "BC");
        signer.initSign((PrivateKey) signKey, random);

        try (BufferedInputStream buffered = new BufferedInputStream(input, BUFFER_SIZE)) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int size;
            while ((size = buffered.read(buffer, 0, BUFFER_SIZE)) > 0) {
                signer.update(buffer, 0, size);
            }
        }

        return Stringx.encodeHex(signer.sign());
    }

    @Override
    @SneakyThrows({NoSuchAlgorithmException.class, NoSuchProviderException.class, ParseException.class, InvalidKeyException.class})
    public boolean verify(InputStream input, Key verifyKey, String signature) throws IOException, SignatureException {

        if (!(verifyKey instanceof PublicKey)) {
            throw new SignatureException("Invalid verifyKey");
        }

        Signature signer = Signature.getInstance(this.getName(), "BC");
        signer.initVerify((PublicKey) verifyKey);

        try (BufferedInputStream buffered = new BufferedInputStream(input, BUFFER_SIZE)) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int size;
            while ((size = buffered.read(buffer, 0, BUFFER_SIZE)) > 0) {
                signer.update(buffer, 0, size);
            }
        }

        return signer.verify(Stringx.decodeHex(signature));
    }
}
