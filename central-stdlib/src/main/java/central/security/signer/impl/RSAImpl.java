package central.security.signer.impl;

import central.util.Stringx;
import central.security.signer.SignerImpl;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * RSA 签名算法
 *
 * @author Alan Yeh
 * @since 2022/07/10
 */
public class RSAImpl implements SignerImpl {
    // 数字签名 签名/验证算法
    public static final String ALGORITHM = "SHA256withRSA";
    private static final int BUFFER_SIZE = 8 * 1024;
    private static final int KEY_SIZE = 2048;

    @Getter
    private final String name = "RSA";

    @Override
    @SneakyThrows
    public central.security.signer.KeyPair generateKeyPair() {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(KEY_SIZE);

        java.security.KeyPair keyPair = generator.generateKeyPair();

        return new central.security.signer.KeyPair(keyPair.getPrivate(), keyPair.getPublic());
    }

    @Override
    @SneakyThrows
    public Key getSignKey(String keySpec) {
        return KeyFactory.getInstance("RSA").generatePrivate(new X509EncodedKeySpec(Base64.getDecoder().decode(keySpec)));
    }

    @Override
    @SneakyThrows
    public Key getVerifyKey(String keySpec) {
        return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(keySpec)));
    }

    @Override
    @SneakyThrows
    public String sign(InputStream input, Key signKey) throws IOException, SignatureException {
        if (!(signKey instanceof PrivateKey)) {
            throw new SignatureException("Invalid signKey");
        }

        SecureRandom random = new SecureRandom();
        Signature signer = Signature.getInstance(ALGORITHM);
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
    @SneakyThrows
    public boolean verify(InputStream input, Key verifyKey, String signature) throws IOException, SignatureException {
        if (!(verifyKey instanceof PublicKey)) {
            throw new SignatureException("Invalid verifyKey");
        }

        Signature signer = Signature.getInstance(ALGORITHM);
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
