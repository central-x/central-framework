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

package central.security.digest;

import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * 消息摘要算法
 *
 * @author Alan Yeh
 * @since 2022/07/05
 */
public interface DigestImpl {

    /**
     * 摘要算法名称
     */
    String getName();

    /**
     * 将字节码进行消息摘要
     *
     * @param bytes 字节码
     * @return 消息摘要
     */
    String digest(byte[] bytes);

    /**
     * 将字符串进行消息摘要
     *
     * @param value   字符串
     * @param charset 字符串的编码
     * @return 消息摘要
     */
    String digest(String value, Charset charset);

    /**
     * 对输入流进行消息摘要
     *
     * @param is 输入流
     * @return 消息摘要
     */
    String digest(InputStream is);
}
