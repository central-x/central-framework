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

package central.starter.probe.core.endpoint.redis;

import central.lang.Stringx;
import central.starter.probe.core.endpoint.Endpoint;
import central.starter.probe.core.ProbeException;
import central.util.Logx;
import central.validation.Label;
import central.validation.Validatex;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisCommandExecutionException;
import io.lettuce.core.RedisURI;
import io.lettuce.core.codec.StringCodec;
import io.lettuce.core.masterreplica.MasterReplica;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Setter;
import lombok.experimental.ExtensionMethod;
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
@ExtensionMethod(Logx.class)
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
        ProbeException error = null;
        String info = null;
        String response = null;

        try (var client = RedisClient.create()) {
            // 构建 Redis 链接
            var uriBuilder = RedisURI.builder().withHost(this.host).withPort(this.port);
            if (Stringx.isNotBlank(this.password)) {
                if (Stringx.isNotBlank(this.username)) {
                    uriBuilder.withAuthentication(this.username, this.password);
                } else {
                    uriBuilder.withPassword(this.password.toCharArray());
                }
            }

            // 连接 Redis
            try (var connection = MasterReplica.connect(client, StringCodec.UTF8, uriBuilder.build())) {
                connection.setTimeout(Duration.ofSeconds(3));

                // 同步执行命令
                var commands = connection.sync();
                info = commands.info("server");
                response = commands.ping();

                if (!"PONG".equalsIgnoreCase(response)) {
                    throw new RedisCommandExecutionException("执行 PING 时没有返回正确的 PONG 结果");
                }
            }
            client.shutdown();
        } catch (Exception cause) {
            error = new ProbeException("Redis 探测异常: " + cause.getLocalizedMessage(), cause);
        }


        var builder = new StringBuilder("\n").append("┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ ".wrap(Logx.Color.WHITE)).append("Probe Endpoint".wrap(Logx.Color.PURPLE)).append(" ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━".wrap(Logx.Color.WHITE)).append("\n");
        builder.append("┣ ".wrap(Logx.Color.WHITE)).append("Endpoint".wrap(Logx.Color.BLUE)).append(": ").append(this.beanName).append("\n");
        builder.append("┣ ".wrap(Logx.Color.WHITE)).append("Type".wrap(Logx.Color.BLUE)).append(": ").append("Redis\n");
        builder.append("┣ ".wrap(Logx.Color.WHITE)).append("Params".wrap(Logx.Color.BLUE)).append(": ").append("\n");

        builder.append("┣ ".wrap(Logx.Color.WHITE)).append("- host: ").append(this.host).append("\n");
        builder.append("┣ ".wrap(Logx.Color.WHITE)).append("- port: ").append(this.port).append("\n");

        if (Stringx.isNotBlank(this.password)) {
            if (Stringx.isNotBlank(this.username)) {
                builder.append("┣ ".wrap(Logx.Color.WHITE)).append("- username: ").append(this.username).append("\n");
            }
            builder.append("┣ ".wrap(Logx.Color.WHITE)).append("- password: ").append(Stringx.paddingLeft("", this.password.length(), '*')).append("\n");
        }
        builder.append("┣ ".wrap(Logx.Color.WHITE)).append("- query: PING").append("\n");
        builder.append("┣━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━".wrap(Logx.Color.WHITE)).append("\n");
        builder.append("┣ ".wrap(Logx.Color.WHITE)).append("Probe Status".wrap(Logx.Color.BLUE)).append(": ").append(error == null ? "SUCCESS".wrap(Logx.Color.GREEN) : "ERROR".wrap(Logx.Color.RED)).append("\n");
        if (error != null) {
            // 探测失败
            builder.append("┣ ".wrap(Logx.Color.WHITE)).append("Error Message".wrap(Logx.Color.BLUE)).append(": ").append(error.getCause().getLocalizedMessage().replace("\n", "\n" + "┃ ".wrap(Logx.Color.WHITE))).append("\n");
        } else {
            builder.append("┣ ".wrap(Logx.Color.WHITE)).append("Server Info".wrap(Logx.Color.BLUE)).append(": \n").append("┃ ".wrap(Logx.Color.WHITE)).append(info.trim().replace("\n", "\n" + "┃ ".wrap(Logx.Color.WHITE))).append("\n");
            builder.append("┣ ".wrap(Logx.Color.WHITE)).append("Query Result".wrap(Logx.Color.BLUE)).append(": ").append(response).append("\n");
        }
        builder.append("┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━".wrap(Logx.Color.WHITE));

        if (error != null) {
            log.error(builder.toString());
        } else {
            log.info(builder.toString());
        }
        if (error != null) {
            throw error;
        }
    }
}
