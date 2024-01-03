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

package central.starter.probe.core.authorizer.jwt;

import central.bean.OptionalEnum;
import central.security.Signerx;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.security.interfaces.ECKey;
import java.security.interfaces.RSAKey;
import java.util.Arrays;
import java.util.function.Function;

/**
 * 加密算法类型
 *
 * @author Alan Yeh
 * @since 2024/01/03
 */
@Getter
@AllArgsConstructor
public enum AlgorithmType implements OptionalEnum<Function<String, Algorithm>> {
    HMAC256("hmac256", Algorithm::HMAC256),
    HMAC384("hmac384", Algorithm::HMAC384),
    HMAC512("hmac512", Algorithm::HMAC512),

    ECDSA256("ecdsa256", secret -> Algorithm.ECDSA256((ECKey) Signerx.ECDSA_256.getVerifyKey(secret))),
    ECDSA384("ecdsa384", secret -> Algorithm.ECDSA384((ECKey) Signerx.ECDSA_384.getVerifyKey(secret))),
    ECDSA512("ecdsa512", secret -> Algorithm.ECDSA512((ECKey) Signerx.ECDSA_512.getVerifyKey(secret))),

    RSA256("rsa256", secret -> Algorithm.RSA256((RSAKey) Signerx.RSA_256.getVerifyKey(secret))),
    RSA384("rsa384", secret -> Algorithm.RSA384((RSAKey) Signerx.RSA_384.getVerifyKey(secret))),
    RSA512("rsa512", secret -> Algorithm.RSA512((RSAKey) Signerx.RSA_512.getVerifyKey(secret)));

    private final String name;
    private final Function<String, Algorithm> value;

    public static @Nullable AlgorithmType resolve(String algorithm) {
        return Arrays.stream(AlgorithmType.values()).filter(it -> it.getName().equalsIgnoreCase(algorithm)).findAny().orElse(null);
    }
}
