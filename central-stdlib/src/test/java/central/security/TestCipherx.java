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
