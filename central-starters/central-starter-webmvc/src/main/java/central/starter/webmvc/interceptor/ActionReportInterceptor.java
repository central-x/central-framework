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

package central.starter.webmvc.interceptor;

import central.web.XForwardedHeaders;
import central.lang.Stringx;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Nonnull;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 打印请求信息
 *
 * @author Alan Yeh
 * @since 2022/07/16
 */
@Slf4j
public class ActionReportInterceptor implements HandlerInterceptor {

    private static final String ACCEPT_TIME = ActionReportInterceptor.class.getName() + ".AcceptTime";

    private final ThreadLocal<SimpleDateFormat> sdf = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"));

    @Override
    public boolean preHandle(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            // 只拦载 Controller 方法，不拦截静态资源
            request.setAttribute(ACCEPT_TIME, new Date());
        }
        return true;
    }

    @Override
    public void afterCompletion(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull Object handler, Exception ex) throws Exception {
        if (handler instanceof HandlerMethod method) {
            // 计算执行时长
            var completeTime = new Date();
            var acceptTime = (Date) request.getAttribute(ACCEPT_TIME);
            long execTime = completeTime.getTime() - acceptTime.getTime();

            // 记录请求耗时
            try {
                MDC.put("type", "execution");
                MDC.put("time", String.valueOf(execTime));
                MDC.put("source", method.getBeanType().getName() + "#" + method.getMethod().getName());
                log.info("执行方法 {}#{}, 耗时: {}ms", method.getBeanType().getName(), method.getMethod().getName(), execTime);
            } finally {
                MDC.remove("type");
                MDC.remove("time");
                MDC.remove("source");
            }

            StringBuilder builder = new StringBuilder("---------------------------------------------------------------------------------\r\n");
            builder.append("Tenant          : ").append(request.getHeader(XForwardedHeaders.TENANT)).append("\r\n");
            String tenantPath = request.getHeader(XForwardedHeaders.PATH);
            if (Stringx.isNotBlank(tenantPath)) {
                builder.append("Tenant Path     : ").append(tenantPath).append("\r\n");
            }
            builder.append("Action          : ").append(request.getMethod()).append(" ").append(request.getRequestURI()).append("\r\n");
            builder.append("Controller      : ").append(method.getBeanType().getName()).append(".(").append(method.getBeanType().getSimpleName()).append(".java:1)\r\n");
            builder.append("Method          : ").append(method.getMethod().getName()).append("\r\n");
            builder.append("Response Status : ").append(response.getStatus()).append("(").append(HttpStatus.valueOf(response.getStatus()).name()).append(")\r\n");
            builder.append("AcceptTime      : ").append(sdf.get().format(acceptTime)).append("\r\n");
            builder.append("Cost            : ").append(execTime).append("ms\r\n");

            if (request.getParameterNames() != null && request.getParameterNames().hasMoreElements()) {
                builder.append("Parameter       : ");
                var names = request.getParameterNames();
                while (names.hasMoreElements()){
                    String name = names.nextElement();
                    String[] values = request.getParameterValues(name);
                    if (values.length == 1) {
                        builder.append(name).append("=").append(values[0]);
                    } else {
                        builder.append(name).append("=[").append(Stringx.join(values, ",")).append("]");
                    }
                    builder.append("  ");
                }
                builder.append("\r\n");
            }

            if (ex != null) {
                builder.append("Exception   : ").append("\r\n");

                StringWriter writer = new StringWriter();
                ex.printStackTrace(new PrintWriter(writer));
                builder.append(writer.toString()).append("\r\n");
            }

            builder.append("---------------------------------------------------------------------------------");
            log.info(builder.toString());
        }
    }
}
