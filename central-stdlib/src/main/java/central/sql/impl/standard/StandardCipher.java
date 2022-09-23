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

package central.sql.impl.standard;

import central.lang.Assertx;
import central.security.cipher.CipherImpl;
import central.sql.SqlCipher;
import central.lang.Stringx;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.security.GeneralSecurityException;
import java.security.Key;

/**
 * Cipherx
 *
 * @author Alan Yeh
 * @since 2022/08/05
 */
@RequiredArgsConstructor
public class StandardCipher implements SqlCipher {
    /**
     * 加密器
     */
    private final CipherImpl cipher;

    /**
     * 加密密钥
     */
    private final Key encryptKey;

    /**
     * 解密密钥
     */
    private final Key decryptKey;

    @Override
    @SneakyThrows
    public String encrypt(String data) {
        return this.cipher.getName() + ":" + this.cipher.encrypt(data, this.encryptKey);
    }

    @Override
    @SneakyThrows
    public String decrypt(String encryptedData) {
        if (Stringx.isNotBlank(this.cipher.getName())) {
            // 如果有加密前缀，需要检测一下前缀是否相同，如果不相同，说明不能解密
            Assertx.mustTrue(encryptedData.startsWith(this.cipher.getName() + ":"), GeneralSecurityException::new, "数据解密出错：{} 不是有效的 {} 密文", encryptedData, this.cipher.getName());
            return this.cipher.decrypt(encryptedData.substring((this.cipher.getName() + ":").length()), this.decryptKey);
        } else {
            return this.cipher.decrypt(encryptedData, decryptKey);
        }
    }
}
