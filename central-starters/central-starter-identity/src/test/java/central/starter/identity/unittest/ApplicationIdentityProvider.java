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

package central.starter.identity.unittest;

import central.starter.identity.IdentityProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * 应用安全配转走
 *
 * @author Alan Yeh
 * @since 2023/02/13
 */
@Component
public class ApplicationIdentityProvider implements IdentityProvider {
    @Override
    public void onReceiveAuthenticationToken(String token) {
        try {
            JWT.require(Algorithm.HMAC256("test")).build()
                    .verify(token);
        } catch (Exception ignored) {
            throw new UnauthorizedException("凭证无效");
        }
    }

    @Override
    public void onReceiveAuthorizationInfo(String token, SimpleAuthorizationInfo authorizationInfo) {
        var jwt = JWT.decode(token);
        var permissions = jwt.getClaim("permissions").asString();
        authorizationInfo.addStringPermissions(Arrays.asList(permissions.split(",")));
    }

    @Override
    public void onLogout(String token) {

    }
}
