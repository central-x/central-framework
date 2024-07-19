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

package central.starter.webmvc.servlet;

import central.lang.Arrayx;
import central.lang.Assertx;
import central.lang.Attribute;
import central.lang.Stringx;
import central.util.Mapx;
import central.util.Objectx;
import central.web.XForwardedHeaders;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebMvcRequest 实现
 *
 * @author Alan Yeh
 * @since 2023/03/08
 */
class DefaultWebMvcRequest extends HttpServletRequestWrapper implements WebMvcRequest {

    public DefaultWebMvcRequest(HttpServletRequest request) {
        super(request);
    }

    public static WebMvcRequest of(HttpServletRequest request) {
        return new DefaultWebMvcRequest(request);
    }

    @NotNull
    @Override
    public URI getUri() {
        var originUri = this.getHeader(XForwardedHeaders.ORIGIN_URI);
        if (originUri != null) {
            return URI.create(originUri);
        } else {
            var parameters = this.getParameterMap();
            var builder = UriComponentsBuilder.fromUriString(this.getRequestURI())
                    .scheme(this.getScheme())
                    .host(this.getServerName());

            // 处理默认端口
            if ("http".equalsIgnoreCase(this.getScheme()) && 80 != this.getServerPort()) {
                builder.port(this.getServerPort());
            } else if ("https".equalsIgnoreCase(this.getScheme()) && 443 != this.getServerPort()) {
                builder.port(this.getServerPort());
            }

            // 处理参数
            if (Mapx.isNotEmpty(parameters)) {
                for (var entry : parameters.entrySet()) {
                    builder.queryParam(entry.getKey(), Arrayx.asStream(entry.getValue()).map(Stringx::encodeUrl).toList());
                }
            }

            return builder.build(true).toUri();
        }
    }

    @Override
    public String getCookie(String name) {
        return Arrayx.asStream(this.getCookies())
                .filter(it -> Objects.equals(name, it.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }

    @Override
    public boolean isAcceptContentType(MediaType contentType) {
        var accept = MediaType.parseMediaType(Objectx.getOrDefault(this.getHeader(HttpHeaders.ACCEPT), MediaType.ALL_VALUE));
        return contentType.includes(accept);
    }

    @Override
    public String getTenantCode() {
        return this.getHeader(XForwardedHeaders.TENANT);
    }

    @Override
    public String getTenantPath() {
        return Objectx.getOrDefault(this.getHeader(XForwardedHeaders.PATH), "/");
    }

    private final Map<String, Object> attributes = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public <T> @Nullable T getAttribute(@Nonnull Attribute<T> attribute) {
        return (T) attributes.computeIfAbsent(attribute.getCode(), code -> attribute.getValue());
    }

    public <T> @Nonnull T getRequiredAttribute(@Nonnull Attribute<T> attribute) {
        return Assertx.requireNotNull(getAttribute(attribute), "Require nonnull value for key '{}'", attribute.getCode());
    }

    @SuppressWarnings("unchecked")
    public <T> @Nonnull T getAttributeOrDefault(@Nonnull Attribute<T> attribute, @Nonnull T defaultValue) {
        return (T) this.attributes.getOrDefault(attribute.getCode(), defaultValue);
    }

    public <T> void setAttribute(@Nonnull Attribute<T> attribute, @Nullable T value) {
        if (value == null) {
            this.attributes.remove(attribute.getCode());
        } else {
            this.attributes.put(attribute.getCode(), value);
        }
    }
}
