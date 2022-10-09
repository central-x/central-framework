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

package central.starter.webflux.render;

import central.lang.Assertx;
import central.lang.Stringx;
import central.starter.web.http.XForwardedHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;

/**
 * 重定向
 *
 * @author Alan Yeh
 * @since 2022/10/09
 */
public class RedirectRender extends Render<RedirectRender> {
    public RedirectRender(ServerWebExchange exchange) {
        super(exchange);
    }

    public RedirectRender(ServerHttpRequest request, ServerHttpResponse response) {
        super(request, response);
    }

    private URI location;

    public RedirectRender redirect(URI location) {
        this.location = location;
        return this;
    }

    @Override
    public Mono<Void> render() {
        var location = Assertx.requireNotNull(this.location, NullPointerException::new, "location 必须不为空");

        if (location.toString().startsWith("/")) {
            // 如果不是完整的 HTTP 协议，则需要补充为完整的 http uri
            // 在补充的过程中，需要考虑租户路径相关问题

            var requestUri = this.getRequest().getURI();

            var builder = UriComponentsBuilder.fromUri(location);
            builder.scheme(requestUri.getScheme()).host(requestUri.getHost()).port(requestUri.getPort());

            // 检测是否存在租户路径
            var tenantPath = this.getRequest().getHeaders().getFirst(XForwardedHeaders.PATH);
            if (Stringx.isNotBlank(tenantPath)) {
                builder.replacePath(tenantPath + location.getPath());
            }

            location = URI.create(builder.build().toString());
        }

        this.setStatus(HttpStatus.FOUND);
        this.getResponse().getHeaders().setLocation(location);
        return this.response.setComplete();
    }
}
