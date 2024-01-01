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

package central.starter.probe.core.host;

import central.lang.Stringx;
import central.starter.probe.core.Endpoint;
import central.starter.probe.core.ProbeException;
import central.validation.Label;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Setter;
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
public class HostEndpoint implements Endpoint, BeanNameAware {

    @Setter
    private String beanName;

    @Setter
    @NotBlank
    @Size(max = 256)
    @Label("主机名")
    private String host;

    @Override
    public void perform() throws Exception {
        InetAddress[] addresses;
        try {
            addresses = InetAddress.getAllByName(host);
        } catch (UnknownHostException error) {
            throw new ProbeException(Stringx.format("解析域名[{}]失败", this.host));
        }

        var builder = new StringBuilder("┏━━━━━━━━━━━━━━━━━━ Probe ━━━━━━━━━━━━━━━━━━━\n");
        builder.append("┣ Endpoint: ").append(this.beanName).append("\n");
        builder.append("┣ Type: ").append("Host\n");
        builder.append("┣ Params: \n");
        builder.append("┣ - host: ").append(this.host).append("\n");
        builder.append("┣ Result: \n");
        for (var address : addresses) {
            builder.append("┣ - ").append((address instanceof Inet4Address) ? "IPv4: " : "").append((address instanceof Inet6Address) ? "IPv6: " : "").append(address.getHostAddress()).append("\n");
        }
        builder.append("┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info(builder.toString());
    }
}
