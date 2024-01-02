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

package central.starter.probe.core.redis;

import central.lang.Stringx;
import central.starter.probe.core.Endpoint;
import central.starter.probe.core.ProbeException;
import central.validation.Label;
import central.validation.Validatex;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.codec.StringCodec;
import io.lettuce.core.masterreplica.MasterReplica;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;

import java.time.Duration;

/**
 * Redis 探针
 *
 * @author Alan Yeh
 * @since 2023/12/29
 */
@Slf4j
public class RedisEndpoint implements Endpoint, InitializingBean, BeanNameAware {

    @Setter
    private String beanName;

    @Setter
    @NotBlank
    @Size(max = 256)
    @Label("主机名")
    private String host;

    @Setter
    @Label("端口号")
    private Integer port = 6379;

    @Setter
    @Label("用户名")
    private String username;

    @Setter
    @Label("密码")
    private String password;

    @Override
    public void afterPropertiesSet() throws Exception {
        Validatex.Default().validate(this);
        if (this.port == null) {
            this.port = 6379;
        }
    }

    @Override
    public void perform() throws ProbeException {
        try (var client = RedisClient.create()) {
            var uriBuilder = RedisURI.builder().withHost(this.host)
                    .withPort(this.port);
            if (Stringx.isNotBlank(this.username) && Stringx.isNotBlank(this.password)) {
                uriBuilder.withAuthentication(this.username, this.password);
            } else if (Stringx.isNotBlank(this.password)) {
                uriBuilder.withPassword(this.password.toCharArray());
            }
            try (var connection = MasterReplica.connect(client, StringCodec.UTF8, uriBuilder.build())) {
                connection.setTimeout(Duration.ofSeconds(3));
                var commands = connection.sync();
                var pong = commands.ping();

                if (!"PONG".equalsIgnoreCase(pong)) {
                    throw new ProbeException("执行 PING 时没有返回正确的 PONG 结果");
                }
            }
            client.shutdown();
        }

        var builder = new StringBuilder("┏━━━━━━━━━━━━━━━━━━ Probe ━━━━━━━━━━━━━━━━━━━\n");
        builder.append("┣ Endpoint: ").append(this.beanName).append("\n");
        builder.append("┣ Type: ").append("Redis\n");
        builder.append("┣ Params: \n");
        builder.append("┣ - host: ").append(this.host).append("\n");
        builder.append("┣ - port: ").append(this.port).append("\n");
        if (Stringx.isNotBlank(this.username)) {
            builder.append("┣ - username: ").append(this.username.charAt(0)).append(Stringx.paddingLeft("", this.username.length() - 2, '*')).append(this.username.charAt(this.username.length() - 1)).append("\n");
        }
        if (Stringx.isNotBlank(this.password)) {
            builder.append("┣ - password: ").append(Stringx.paddingLeft("", this.password.length(), '*')).append("\n");
        }
        builder.append("┣ - query: PING").append("\n");
        builder.append("┣ Result: PONG\n");
        builder.append("┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info(builder.toString());
    }
}
