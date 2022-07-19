package central.security.cipher.impl;

import central.security.cipher.CipherImpl;
import central.security.cipher.KeyPair;
import lombok.Getter;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;

/**
 * 不加密
 *
 * @author Alan Yeh
 * @since 2022/07/10
 */
public class NoneImpl implements CipherImpl {

    @Getter
    private final String name = "NONE";

    @Override
    public KeyPair generateKeyPair() {
        return new KeyPair(new EmptyKey(), new EmptyKey());
    }

    private static final Key EMPTY_KEY = new EmptyKey();

    private static class EmptyKey implements Key {
        private static final long serialVersionUID = -6945045424439243787L;

        @Override
        public String getAlgorithm() {
            return "none";
        }

        @Override
        public String getFormat() {
            return "blank";
        }

        @Override
        public byte[] getEncoded() {
            return new byte[0];
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof EmptyKey;
        }
    }

    @Override
    public Key getEncryptKey(String keySpec) throws GeneralSecurityException {
        return EMPTY_KEY;
    }

    @Override
    public Key getDecryptKey(String keySpec) throws GeneralSecurityException {
        return EMPTY_KEY;
    }

    @Override
    public String encrypt(String content, Key encryptKey) throws GeneralSecurityException, IOException {
        return content;
    }

    @Override
    public byte[] encrypt(byte[] data, Key encryptKey) throws GeneralSecurityException, IOException {
        return data;
    }

    @Override
    public String decrypt(String content, Key decryptKey) throws GeneralSecurityException, IOException {
        return content;
    }

    @Override
    public byte[] decrypt(byte[] data, Key decryptKey) throws GeneralSecurityException, IOException {
        return data;
    }
}
