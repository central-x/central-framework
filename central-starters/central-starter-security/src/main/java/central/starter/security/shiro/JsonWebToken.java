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

package central.starter.security.shiro;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.Nonnull;
import lombok.Getter;
import org.apache.shiro.authc.AuthenticationToken;

import java.io.Serial;

/**
 * Json Web Token
 *
 * @author Alan Yeh
 * @since 2023/02/13
 */
public class JsonWebToken implements AuthenticationToken {
    @Serial
    private static final long serialVersionUID = -4407799661511688123L;

    private final String token;

    @Getter
    private final DecodedJWT decodedJWT;

    public JsonWebToken(@Nonnull String token) {
        this.token = token;
        this.decodedJWT = JWT.decode(token);
    }

    @Override
    public Object getPrincipal() {
        return decodedJWT.getSubject();
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}
