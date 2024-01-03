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

import central.lang.Assertx;
import central.lang.Stringx;
import central.starter.probe.core.ProbeException;
import central.starter.probe.core.authorizer.Authorizer;
import central.util.Collectionx;
import central.util.Mapx;
import central.validation.Label;
import central.validation.Validatex;
import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;

/**
 * JWT 监权
 *
 * @author Alan Yeh
 * @since 2024/01/03
 */
@Slf4j
public class JwtAuthorizer implements Authorizer, InitializingBean {
    @Setter
    @NotBlank
    @Size(max = 256)
    @Label("签名算法")
    private String algorithm;

    @Setter
    @NotBlank
    @Size(max = 4096)
    @Label("密钥")
    private String secret;

    @Setter
    @Label("声明")
    private Map<String, Object> claims;

    private AlgorithmType type;

    @Override
    public void afterPropertiesSet() throws Exception {
        Validatex.Default().validate(this);
        this.type = Assertx.requireNotNull(AlgorithmType.resolve(this.algorithm), "不支持的加密算法: " + this.algorithm);
    }

    @Override
    public void authorize(String authorization) {
        try {
            var builder = JWT.require(this.type.getValue().apply(this.secret))
                    // Not Before、Issued At、Expires At 三个时间的容差（秒）
                    // 避免因为时差原因导致验证失败
                    .acceptLeeway(30)
                    .withClaimPresence("exp")
                    .ignoreIssuedAt();

            if (Mapx.isNotEmpty(this.claims)) {
                for (var claim : this.claims.entrySet()) {
                    if (claim.getValue() == null) {
                        builder.withNullClaim(claim.getKey());
                    } else if (claim.getValue() instanceof Map<?, ?> map) {
                        // 数组
                        var values = map.values();
                        if (Collectionx.isNullOrEmpty(values)) {
                            builder.withClaimPresence(claim.getKey());
                        } else {
                            var value = values.iterator().next();
                            if (value instanceof Number) {
                                builder.withArrayClaim(claim.getKey(), values.stream().map(it -> (Number) it).map(Number::longValue).toArray(Long[]::new));
                            } else {
                                builder.withArrayClaim(claim.getKey(), values.stream().map(Objects::toString).toArray(String[]::new));
                            }
                        }
                    } else if (claim.getValue() instanceof Double value) {
                        builder.withClaim(claim.getKey(), value);
                    } else if (claim.getValue() instanceof Boolean value) {
                        builder.withClaim(claim.getKey(), value);
                    } else if (claim.getValue() instanceof Integer value) {
                        builder.withClaim(claim.getKey(), value);
                    } else if (claim.getValue() instanceof Long value) {
                        builder.withClaim(claim.getKey(), value);
                    } else if (claim.getValue() instanceof String value) {
                        if (Stringx.isNullOrBlank(value)){
                            builder.withClaimPresence(claim.getKey());
                        } else {
                            builder.withClaim(claim.getKey(), value);
                        }
                    } else {
                        builder.withClaim(claim.getKey(), Objects.toString(claim.getValue()));
                    }
                }
            }

            var decodedJwt = builder.build().verify(authorization);
            // 校验有效期是否过长
            if (decodedJwt.getExpiresAt().getTime() > (System.currentTimeMillis() + Duration.ofMinutes(30).toMillis())) {
                throw new JWTVerificationException("有效期[exp]过长，应限制在 30 分钟以内");
            }
        } catch (AlgorithmMismatchException cause) {
            throw new ProbeException("凭证签名算法不匹配", cause);
        } catch (TokenExpiredException cause) {
            throw new ProbeException("凭证已过期", cause);
        } catch (MissingClaimException cause) {
            throw new ProbeException("凭证声明[Claim]缺失: " + cause.getClaimName());
        } catch (IncorrectClaimException cause) {
            throw new ProbeException("凭证声明[Claim]错误: " + cause.getClaimName());
        } catch (SignatureVerificationException cause) {
            throw new ProbeException("凭证签名认证失败", cause);
        } catch (JWTVerificationException cause) {
            throw new ProbeException("凭证认证失败: " + cause.getLocalizedMessage(), cause);
        }
    }
}
