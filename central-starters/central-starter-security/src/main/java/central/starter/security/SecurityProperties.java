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

import central.validation.Label;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 安全认证属性
 *
 * @author Alan Yeh
 * @since 2023/02/13
 */
@Data
@ConfigurationProperties("central.security")
public class SecurityProperties {
    /**
     * 是否启用认证
     */
    private boolean enabled = true;
    /**
     * 检测到未登录时，跳转的地址
     * 如果为空，则返回 json
     * 如果请求的 Content-Type 为 application/json，也直接返回 json
     */
    @Label("未登录地址")
    @NotBlank
    private String unauthorizedUrl;
    /**
     * 检测到没有权限时，跳转到此链接
     * 如果为空，则返回 json
     * 如果请求的 Content-Type 为 application/json，也直接返回 json
     */
    @Label("禁用地址")
    @NotBlank
    private String forbiddenUrl;
    /**
     * 退出登录使用的链接
     */
    @Label("退出地址")
    @NotBlank
    private String logoutUrl = "/__logout";
    /**
     * Cookie 字段
     */
    @Label("Cookie 名")
    @NotBlank
    private String cookie = "Authorization";
    /**
     * 去服务端校验 Cookie 有效性的间隔（毫秒）
     */
    @Label("校验间隔")
    @Min(0)
    @Max(10000)
    private long verifyInterval = 5000;
}
