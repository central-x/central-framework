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

package central.starter.webmvc.render;

import central.lang.Stringx;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;

/**
 * 重定向
 *
 * @author Alan Yeh
 * @since 2022/07/16
 */
public class RedirectRender extends Render<RedirectRender> {
    /**
     * 设置请求上下文
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     */
    public RedirectRender(HttpServletRequest request, HttpServletResponse response) {
        super(request, response);
    }

    private URI location;

    public RedirectRender redirect(URI location){
        this.location = location;
        return this;
    }

    private URI getLocation(){
        if (this.location == null){
            throw new IllegalArgumentException("location cannot be null");
        }

        if (this.location.toString().startsWith("/")){
            // 如果不是完整的 HTTP 协议的，就补充为完整的 http uri
            URI requestUri = URI.create(this.getRequest().getRequestURL().toString());

            UriComponentsBuilder builder = UriComponentsBuilder.fromUri(this.location)
                    .scheme(requestUri.getScheme())
                    .host(requestUri.getHost())
                    .port(requestUri.getPort());

            // 检测是否存在 X-Forwarded-Path
            // 如果存在的话，需要在重定向的路径里加上路径，否则会请求错误
            String tenantPath = this.getRequest().getHeader("X-Forwarded-Path");
            if (Stringx.isNotBlank(tenantPath)) {
                builder.replacePath(tenantPath + this.location.getPath());
            }

            return this.location = URI.create(builder.build().toString());
        } else {
            return this.location;
        }
    }

    @Override
    public void render() throws IOException {
        URI location = getLocation();

        this.getResponse().sendRedirect(location.toString());
    }
}
