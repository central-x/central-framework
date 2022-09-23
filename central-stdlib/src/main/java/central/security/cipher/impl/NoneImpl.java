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
import lombok.Getter;

import java.io.IOException;
import java.io.Serial;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.util.Arrays;

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
        @Serial
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

        @Override
        public int hashCode() {
            return Arrays.hashCode(new byte[0]);
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
