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

import com.google.common.base.CaseFormat;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

/**
 * 下划线参数处理
 * 将下划线命名的参数转成驼峰命名
 *
 * @author Alan Yeh
 * @since 2023/02/20
 */
public class UnderlineParameterNameFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull FilterChain filterChain) throws ServletException, IOException {
        filterChain.doFilter(new UnderlineParameterRequestWrapper(request), response);
    }

    private static class UnderlineParameterRequestWrapper extends HttpServletRequestWrapper {
        private final Vector<String> parameterNames;
        private final Map<String, List<String>> parameterValues = new HashMap<>();

        public UnderlineParameterRequestWrapper(HttpServletRequest request) {
            super(request);
            // 遍历参数，将下划线命名的参数转成驼峰命名
            var parameterNames = super.getParameterNames();
            var names = new HashSet<String>();

            while (parameterNames != null && parameterNames.hasMoreElements()) {
                var name = parameterNames.nextElement();
                var values = super.getParameterValues(name);

                if (name.contains("_")) {
                    name = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, name);
                }

                names.add(name);
                this.parameterValues.computeIfAbsent(name, key -> new ArrayList<>())
                        .addAll(Arrays.asList(values));
            }
            this.parameterNames = new Vector<>(names);
        }

        @Override
        public Enumeration<String> getParameterNames() {
            return this.parameterNames.elements();
        }

        @Override
        public String[] getParameterValues(String name) {
            return this.parameterValues.getOrDefault(name, new ArrayList<>()).toArray(new String[0]);
        }
    }
}
