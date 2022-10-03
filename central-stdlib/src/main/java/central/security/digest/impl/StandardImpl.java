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

package central.security.digest.impl;

import central.lang.Stringx;
import central.security.digest.DigestImpl;
import lombok.SneakyThrows;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.MessageDigest;

/**
 * 标准摘要算法
 *
 * @author Alan Yeh
 * @since 2022/07/05
 */
public class StandardImpl implements DigestImpl {

    private final String algorithm;

    @Override
    public String getName() {
        return this.algorithm;
    }

    public StandardImpl(String algorithm) {
        this.algorithm = algorithm;
    }

    @Override
    @SneakyThrows
    public String digest(byte[] bytes) {
        MessageDigest md = MessageDigest.getInstance(this.algorithm);
        md.update(bytes);
        byte[] digest = md.digest();

        return Stringx.encodeHex(digest);
    }

    @Override
    @SneakyThrows
    public String digest(InputStream is) {
        MessageDigest md = MessageDigest.getInstance(this.algorithm);

        int BUFFER_SIZE = 8 * 1024;
        try (BufferedInputStream buffered = new BufferedInputStream(is, BUFFER_SIZE)) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int length;

            while ((length = buffered.read(buffer)) != -1) {
                md.update(buffer, 0, length);
            }

            return Stringx.encodeHex(md.digest());
        }
    }

    @Override
    @SneakyThrows
    public String digest(String value, Charset charset) {
        return digest(value.getBytes(charset));
    }
}
