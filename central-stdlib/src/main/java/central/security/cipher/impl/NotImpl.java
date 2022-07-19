package central.security.cipher.impl;

import central.security.cipher.CipherImpl;
import central.security.cipher.KeyPair;
import lombok.Getter;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;

/**
 * 未实现
 *
 * @author Alan Yeh
 * @since 2022/07/10
 */
public class NotImpl implements CipherImpl {

    @Getter
    private final String name = "NOT_IMPL";

    @Override
    public byte[] encrypt(byte[] data, Key key) throws GeneralSecurityException, IOException {
        throw new UnsupportedOperationException("未实现");
    }

    @Override
    public byte[] decrypt(byte[] data, Key key) throws GeneralSecurityException, IOException {
        throw new UnsupportedOperationException("未实现");
    }

    @Override
    public KeyPair generateKeyPair() {
        return null;
    }

    @Override
    public Key getEncryptKey(String keySpec) throws GeneralSecurityException {
        return null;
    }

    @Override
    public Key getDecryptKey(String keySpec) throws GeneralSecurityException {
        return null;
    }
}
