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

package central.starter.probe.core.endpoint.host;

import central.lang.Stringx;
import central.starter.probe.core.endpoint.Endpoint;
import central.starter.probe.core.ProbeException;
import central.util.Logx;
import central.validation.Label;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Setter;
import lombok.experimental.ExtensionMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanNameAware;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 主机探测
 *
 * @author Alan Yeh
 * @since 2023/12/29
 */
@Slf4j
@ExtensionMethod(Logx.class)
public class HostEndpoint implements Endpoint, BeanNameAware {

    @Setter
    private String beanName;

    @Setter
    @NotBlank
    @Size(max = 256)
    @Label("主机名")
    private String host;

    @Override
    public void perform() throws ProbeException {
        ProbeException error = null;

        InetAddress[] addresses = new InetAddress[0];
        try {
            addresses = InetAddress.getAllByName(this.host);
        } catch (UnknownHostException cause) {
            error = new ProbeException(Stringx.format("域名[{}]解析失败: {}", this.host, cause.getLocalizedMessage()), cause);
        }

        var builder = new StringBuilder("\n").append("┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ ".wrap(Logx.Color.WHITE)).append("Probe Endpoint".wrap(Logx.Color.PURPLE)).append(" ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━".wrap(Logx.Color.WHITE)).append("\n");
        builder.append("┣ ".wrap(Logx.Color.WHITE)).append("Endpoint".wrap(Logx.Color.BLUE)).append(": ").append(this.beanName).append("\n");
        builder.append("┣ ".wrap(Logx.Color.WHITE)).append("Type".wrap(Logx.Color.BLUE)).append(": ").append("Host\n");
        builder.append("┣ ".wrap(Logx.Color.WHITE)).append("Params".wrap(Logx.Color.BLUE)).append(": ").append("\n");
        builder.append("┃ ".wrap(Logx.Color.WHITE)).append("- host: ").append(this.host).append("\n");
        builder.append("┣━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━".wrap(Logx.Color.WHITE)).append("\n");
        builder.append("┣ ".wrap(Logx.Color.WHITE)).append("Probe Status".wrap(Logx.Color.BLUE)).append(": ").append(error == null ? "SUCCESS".wrap(Logx.Color.GREEN) : "ERROR".wrap(Logx.Color.RED)).append("\n");
        if (error != null) {
            // 探测失败
            builder.append("┣ ".wrap(Logx.Color.WHITE)).append("Error Message".wrap(Logx.Color.BLUE)).append(": ").append(error.getCause().getLocalizedMessage().replace("\n", "\n" + "┃ ".wrap(Logx.Color.WHITE))).append("\n");
        } else {
            builder.append("┣ ".wrap(Logx.Color.WHITE)).append("Lookup Result".wrap(Logx.Color.BLUE)).append(":\n");
            for (var address : addresses) {
                builder.append("┃ ".wrap(Logx.Color.WHITE)).append("- ").append((address instanceof Inet4Address) ? "IPv4: " : "").append((address instanceof Inet6Address) ? "IPv6: " : "").append(address.getHostAddress()).append("\n");
            }
        }
        builder.append("┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━".wrap(Logx.Color.WHITE));

        if (error != null) {
            log.error(builder.toString());
        } else {
            log.debug(builder.toString());
        }
        if (error != null) {
            throw error;
        }
    }
}
