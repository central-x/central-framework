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

package central.starter.probe.core.endpoint;

import central.bean.OptionalEnum;
import central.starter.probe.core.endpoint.datasource.DataSourceEndpoint;
import central.starter.probe.core.endpoint.host.HostEndpoint;
import central.starter.probe.core.endpoint.redis.RedisEndpoint;
import central.starter.probe.core.endpoint.service.ServiceEndpoint;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 端点类型
 *
 * @author Alan Yeh
 * @since 2023/12/31
 */
@Getter
@AllArgsConstructor
public enum EndpointType implements OptionalEnum<Class<? extends Endpoint>> {
    /**
     * 数据源探测
     */
    DATASOURCE("datasource", DataSourceEndpoint.class),
    /**
     * 主机名探测
     */
    HOST("host", HostEndpoint.class),
    /**
     * Redis 探测
     */
    REDIS("redis", RedisEndpoint.class),
    /**
     * 服务探测
     */
    SERVICE("service", ServiceEndpoint.class);

    private final String name;
    private final Class<? extends Endpoint> value;
}
