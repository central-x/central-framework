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
import kotlin.NotImplementedError;

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

    @Override
    public @Nonnull String getName() {
        return "NOT_IMPL";
    }

    @Override
    public @Nonnull byte[] encrypt(@Nonnull byte[] data, @Nonnull Key key) throws GeneralSecurityException, IOException {
        throw new UnsupportedOperationException("未实现");
    }

    @Override
    public @Nonnull byte[] decrypt(@Nonnull byte[] data, @Nonnull Key key) throws GeneralSecurityException, IOException {
        throw new UnsupportedOperationException("未实现");
    }

    @Override
    public @Nonnull KeyPair generateKeyPair() {
        throw new NotImplementedError();
    }

    @Override
    public @Nonnull Key getEncryptKey(@Nonnull String keySpec) throws GeneralSecurityException {
        throw new NotImplementedError();
    }

    @Override
    public @Nonnull Key getDecryptKey(@Nonnull String keySpec) throws GeneralSecurityException {
        throw new NotImplementedError();
    }
}
