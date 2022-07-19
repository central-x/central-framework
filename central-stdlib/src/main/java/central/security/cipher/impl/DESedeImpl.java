package central.security.cipher.impl;

import central.util.Assertx;
import central.security.cipher.CipherImpl;
import central.security.cipher.KeyPair;
import lombok.Getter;
import lombok.SneakyThrows;

import javax.crypto.Cipher;
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
 * @since 2022/07/10
 */
public class DESedeImpl implements CipherImpl {
    private static final String ALGORITHM = "AES";

    // 支持 128 位、192 位、256 位
    private static final int KEY_LENGTH = 256;

    @Getter
    private final String name = "AES";

    @Override
    @SneakyThrows
    public KeyPair generateKeyPair() {
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
    public Key getEncryptKey(String keySpec) throws GeneralSecurityException {
        return this.getKey(keySpec);
    }

    /**
     * 获取 AES Key
     * 加密密钥与解密密钥是相同的
     *
     * @param keySpec 密钥，必须是 16 + N * 8 位
     */
    @Override
    public Key getDecryptKey(String keySpec) throws GeneralSecurityException {
        return this.getKey(keySpec);
    }

    private Key getKey(String keySpec) {
        Assertx.mustNotBlank(keySpec, "Argument 'keySpec' must not blank");
        Assertx.mustTrue((keySpec.length() - 16) % 8 == 0, "密钥必须是 16 + N * 8 位");

        return new SecretKeySpec(Base64.getDecoder().decode(keySpec), ALGORITHM);
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
        // Cipher 对象完成加密操作
        Cipher cipher = Cipher.getInstance(ALGORITHM);

        cipher.init(mode, key);

        return cipher.doFinal(data);
    }
}
