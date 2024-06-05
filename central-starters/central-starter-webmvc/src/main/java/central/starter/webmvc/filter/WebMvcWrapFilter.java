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

package central.starter.webmvc.filter;

import central.lang.Stringx;
import central.starter.webmvc.servlet.WebMvcRequest;
import central.starter.webmvc.servlet.WebMvcResponse;
import central.web.XForwardedHeaders;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/**
 * WebMvcRequest Wrap Filter
 * <p>
 * 将 HttpServletRequest 封装成 WebMvcRequest
 *
 * @author Alan Yeh
 * @since 2023/03/08
 */
public class WebMvcWrapFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull FilterChain filterChain) throws ServletException, IOException {
        filterChain.doFilter(WebMvcRequest.of(new HttpServletRequestWrapper(request) {
            @Override
            public String getHeader(String name) {
                var header = super.getHeader(name);
                if (Stringx.isNullOrBlank(header)) {
                    if (XForwardedHeaders.TENANT.equalsIgnoreCase(name)) {
                        return "master";
                    }
                }
                return header;
            }

            @Override
            public Enumeration<String> getHeaders(String name) {
                var headers = super.getHeaders(name);
                if (!headers.hasMoreElements()) {
                    if (XForwardedHeaders.TENANT.equalsIgnoreCase(name)) {
                        return Collections.enumeration(List.of("master"));
                    }
                }
                return headers;
            }
        }), WebMvcResponse.of(response));
    }
}
