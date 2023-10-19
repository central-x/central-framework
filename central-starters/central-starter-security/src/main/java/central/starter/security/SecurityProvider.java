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

package central.starter.security;

import org.apache.shiro.authz.SimpleAuthorizationInfo;

/**
 * 应用认证信息提供
 * <p>
 * 外部项目如果需要自定义登录过程，需要继承此类，并需要标注 @Component 和 @Primary
 *
 * @author Alan Yeh
 * @since 2023/02/13
 */
public interface SecurityProvider {
    /**
     * 当应用接入到会话凭证信息时，应用需要对该凭证进行鉴权
     *
     * @param token 会话凭证
     */
    void onReceiveAuthenticationToken(String token);

    /**
     * 对帐户进行授权
     * 如果不需要进行授权，可以不处理
     *
     * @param token             会话凭证
     * @param authorizationInfo 授权信息
     */
    default void onReceiveAuthorizationInfo(String token, SimpleAuthorizationInfo authorizationInfo) {
    }

    /**
     * 注销
     *
     * @param token 会话凭证
     */
    default void onLogout(String token) {
    }
}
