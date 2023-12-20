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

package central.starter.identity.shiro;

import central.starter.identity.IdentityProvider;
import lombok.Setter;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Shiro 鉴权域
 *
 * @author Alan Yeh
 * @since 2023/02/13
 */
public class SecurityRealm extends AuthorizingRealm {

    /**
     * 实际的鉴权服务提供者
     */
    @Setter(onMethod_ = @Autowired)
    private IdentityProvider provider;

    /**
     * 只支持 JWT 认证
     *
     * @param token 鉴权 Token
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JsonWebToken;
    }

    /**
     * 认证
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        this.provider.onReceiveAuthenticationToken(token.getCredentials().toString());
        return new SimpleAuthenticationInfo(token.getCredentials(), token.getCredentials(), this.provider.getClass().getSimpleName());
    }

    /**
     * 执行授权
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        var authorizationInfo = new SimpleAuthorizationInfo();
        this.provider.onReceiveAuthorizationInfo(principals.getPrimaryPrincipal().toString(), authorizationInfo);
        return authorizationInfo;
    }

    @Override
    public void onLogout(PrincipalCollection principals) {
        this.provider.onLogout(principals.getPrimaryPrincipal().toString());
    }
}
