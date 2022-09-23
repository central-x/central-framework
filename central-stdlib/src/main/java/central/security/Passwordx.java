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

import central.lang.Assertx;
import central.lang.Stringx;
import lombok.experimental.UtilityClass;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * 密码工具
 * 将任意密码摘要成 64 位摘要信息
 * 即使相同的密码产生的接摘要也是不一样的
 *
 * @author Alan Yeh
 * @since 2022/07/05
 */
@UtilityClass
public class Passwordx {
    // 密钥大小
    private static final int SALT_SIZE = 8;
    // 生成随机加密盐
    private static SecureRandom random = new SecureRandom();

    /**
     * 将输入的文本生成摘要
     */
    public static String digest(@Nonnull String plainPassword) {
        return Digestx.SHA256.digest(plainPassword, StandardCharsets.UTF_8);
    }

    /**
     * 将输入的文本，生成随机的8位salt，并将文本使用 sha256 进行摘要
     * <p>
     * 用于生成安全密码
     */
    public static String encrypt(@Nonnull String plainPassword) {
        Assertx.mustNotNull(plainPassword, "Argument 'plainPassword' must not null");

        var salt = generateSalt(SALT_SIZE);
        return generatePassword(plainPassword, salt);
    }

    /**
     * 验证两个密码是否相等
     *
     * @param plainPassword 明文密码
     * @param password      密文密码
     */
    public static boolean verify(@Nonnull String plainPassword, @Nonnull String password) {
        Assertx.mustNotNull(plainPassword, "Argument 'plainPassword' must not null");
        Assertx.mustNotNull(password, "Argument 'password' must not null");

        try {
            var salt = Stringx.decodeHex(password.substring(0, SALT_SIZE * 2));
            return password.equals(generatePassword(plainPassword, salt));
        } catch (Exception ignored) {
            return false;
        }
    }

    // 生成随机16位盐
    private static byte[] generateSalt(int size) {
        Assertx.mustTrue(size > 0, "size 必须大于 0");

        var result = new byte[size];
        random.nextBytes(result);
        return result;
    }

    // 根据盐生成密码
    private static String generatePassword(@Nonnull String plainPassword, byte[] salt) {
        var hashPassword = digest(plainPassword.getBytes(), salt);
        return (Stringx.encodeHex(salt) + Stringx.encodeHex(hashPassword)).substring(0, 64);
    }

    // 信息摘要
    private static byte[] digest(byte[] input, byte[] salt) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException("[Passwordx] 初始化 MessageDigest 出现异常: " + ex.getLocalizedMessage(), ex);
        }

        if (salt != null) {
            digest.update(salt);
        }

        var result = digest.digest(input);

        // 加盐重摘要，这样可以让相同的密码摘要出来的信息不一样
        digest.reset();
        result = digest.digest(result);

        return result;
    }
}
