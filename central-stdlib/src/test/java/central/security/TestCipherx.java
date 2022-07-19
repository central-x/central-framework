package central.security;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Cipherx Test Cases
 *
 * @author Alan Yeh
 * @since 2022/07/11
 */
public class TestCipherx {
    /**
     * None
     */
    @Test
    public void case1() throws Exception{
        var keys = Cipherx.NONE.generateKeyPair();

        String source = "Unencrypted text";
        String encrypted = Cipherx.NONE.encrypt(source, keys.getEncryptKey());
        Assertions.assertEquals(source, encrypted);

        String decrypted = Cipherx.NONE.decrypt(encrypted, keys.getDecryptKey());
        Assertions.assertEquals(source, decrypted);
    }

    /**
     * RSA
     */
    @Test
    public void case2() throws Exception {
        var keys = Cipherx.RSA.generateKeyPair();

        String source = "Unencrypted text";
        String encrypted = Cipherx.RSA.encrypt(source, keys.getEncryptKey());
        Assertions.assertNotEquals(source, encrypted);

        String decrypted = Cipherx.RSA.decrypt(encrypted, keys.getDecryptKey());
        Assertions.assertEquals(source, decrypted);
    }

    /**
     * DES
     */
    @Test
    public void case3() throws Exception {
        var keys = Cipherx.DES.generateKeyPair();

        String source = "Unencrypted text";
        String encrypted = Cipherx.DES.encrypt(source, keys.getEncryptKey());
        Assertions.assertNotEquals(source, encrypted);

        String decrypted = Cipherx.DES.decrypt(encrypted, keys.getDecryptKey());
        Assertions.assertEquals(source, decrypted);
    }

    /**
     * 3DES
     */
    @Test
    public void case4() throws Exception {
        var keys = Cipherx.DESede.generateKeyPair();

        String source = "Unencrypted text";
        String encrypted = Cipherx.DESede.encrypt(source, keys.getEncryptKey());
        Assertions.assertNotEquals(source, encrypted);

        String decrypted = Cipherx.DESede.decrypt(encrypted, keys.getDecryptKey());
        Assertions.assertEquals(source, decrypted);
    }

    /**
     * AES
     */
    @Test
    public void case5() throws Exception {
        var keys = Cipherx.AES.generateKeyPair();

        String source = "Unencrypted text";
        String encrypted = Cipherx.AES.encrypt(source, keys.getEncryptKey());
        Assertions.assertNotEquals(source, encrypted);

        String decrypted = Cipherx.AES.decrypt(encrypted, keys.getDecryptKey());
        Assertions.assertEquals(source, decrypted);
    }

    /**
     * SM2
     */
    @Test
    public void case6() throws Exception {
        var keys = Cipherx.SM2.generateKeyPair();

        String source = "Unencrypted text";
        String encrypted = Cipherx.SM2.encrypt(source, keys.getEncryptKey());
        Assertions.assertNotEquals(source, encrypted);

        String decrypted = Cipherx.SM2.decrypt(encrypted, keys.getDecryptKey());
        Assertions.assertEquals(source, decrypted);
    }

    /**
     * SM4
     */
    @Test
    public void case7() throws Exception {
        var keys = Cipherx.SM4.generateKeyPair();

        String source = "Unencrypted text";
        String encrypted = Cipherx.SM4.encrypt(source, keys.getEncryptKey());
        Assertions.assertNotEquals(source, encrypted);

        String decrypted = Cipherx.SM4.decrypt(encrypted, keys.getDecryptKey());
        Assertions.assertEquals(source, decrypted);
    }
}
