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

import central.security.digest.DigestImpl;
import central.security.digest.impl.SM3Impl;
import central.security.digest.impl.StandardImpl;

/**
 * 消息摘要
 *
 * @author Alan Yeh
 * @since 2022/07/05
 */
public class Digestx {
    public static final DigestImpl MD5 = new StandardImpl("MD5");
    public static final DigestImpl SHA1 = new StandardImpl("SHA1");
    public static final DigestImpl SHA224 = new StandardImpl("SHA-224");
    public static final DigestImpl SHA256 = new StandardImpl("SHA-256");
    public static final DigestImpl SHA384 = new StandardImpl("SHA-384");
    public static final DigestImpl SHA512 = new StandardImpl("SHA-512");

    /**
     * 国密算法 SM3
     * 消息摘要算法，产生 32 位长度的接要字符串
     */
    public static final DigestImpl SM3 = new SM3Impl();
}
