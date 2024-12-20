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

package central.starter.probe;

import central.starter.probe.properties.CacheProperties;
import central.starter.probe.properties.EndpointProperties;
import central.starter.probe.properties.AuthorizerProperties;
import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * 探针配置属性
 *
 * @author Alan Yeh
 * @since 2023/12/27
 */
@Data
@ConfigurationProperties(prefix = "central.probe")
public class ProbeProperties {
    /**
     * 是否启用探针服务
     */
    private boolean enabled = true;
    /**
     * 每个探针的执行超时时间（毫秒）
     */
    private long timeout = 5000;
    /**
     * 缓存
     */
    @Valid
    private CacheProperties cache = new CacheProperties();
    /**
     * 探测监权
     */
    @Valid
    private AuthorizerProperties authorizer = new AuthorizerProperties();
    /**
     * 探测端点配置
     */
    @Valid
    private List<EndpointProperties> points = new ArrayList<>();
}
