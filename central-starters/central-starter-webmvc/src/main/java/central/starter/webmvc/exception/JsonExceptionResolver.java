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

package central.starter.webmvc.exception;

import central.lang.Stringx;
import central.starter.webmvc.exception.handlers.FallbackHandler;
import central.util.Listx;
import central.web.XTraceHeaders;
import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import java.util.List;

/**
 * 全局异常处理
 *
 * @author Alan Yeh
 * @since 2022/07/16
 */
@Slf4j
@Component
public class JsonExceptionResolver extends ExceptionHandlerExceptionResolver {
    @Setter(onMethod_ = @Autowired)
    private List<ExceptionHandler> handlers;

    @Value("${spring.application.name:}")
    private String applicationName;

    private final ExceptionHandler fallbackHandler = new FallbackHandler();

    @Override
    protected ModelAndView doResolveHandlerMethodException(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, HandlerMethod handlerMethod, @Nonnull Exception exception) {
        ModelAndView result = null;

        if (Listx.isNotEmpty(this.handlers)) {
            for (ExceptionHandler handler : this.handlers) {
                if (handler.support(exception)) {
                    result = handler.handle(request, response, handlerMethod, exception);
                    break;
                }
            }
        }

        if (result == null) {
            result = this.fallbackHandler.handle(request, response, handlerMethod, exception);
        }

        if (Stringx.isNotEmpty(this.applicationName)) {
            response.addHeader(XTraceHeaders.APPLICATION, applicationName);
        }
        return result;
    }
}
