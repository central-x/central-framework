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

package central.starter.security.exception;

import central.lang.Stringx;
import central.starter.security.SecurityProperties;
import central.starter.webmvc.exception.ExceptionHandler;
import central.starter.webmvc.render.ErrorRender;
import central.web.XForwardedHeaders;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.UnauthorizedException;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.io.IOException;
import java.util.Map;

/**
 * 无权限异常捕捉
 *
 * @author Alan Yeh
 * @since 2023/02/13
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class UnauthorizedExceptionHandler implements ExceptionHandler {

    @Setter(onMethod_ = @Autowired)
    private SecurityProperties properties;

    @Override
    public boolean support(Throwable throwable) {
        return throwable instanceof UnauthorizedException;
    }

    @Nullable
    @Override
    @SneakyThrows(IOException.class)
    public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod, Throwable throwable) {
        boolean returnJson = false;

        if (handlerMethod.getMethodAnnotation(ResponseBody.class) != null) {
            // 如果方法本身要求返回 Json
            returnJson = true;
        }
        if (!returnJson && handlerMethod.getBeanType().getAnnotation(RestController.class) != null) {
            // 如果 Controller 需要返回 Json
            returnJson = true;
        }
        if (!returnJson && MediaType.APPLICATION_JSON.isCompatibleWith(MediaType.parseMediaType(request.getHeader(HttpHeaders.ACCEPT)))) {
            // 如果客户端要求返回 Json
            returnJson = true;
        }

        if (returnJson) {
            ModelAndView mv = new ModelAndView(new MappingJackson2JsonView(), Map.of("message", throwable.getLocalizedMessage()));
            mv.setStatus(HttpStatus.FORBIDDEN);
            return mv;
        } else {
            if (Stringx.isNullOrBlank(this.properties.getUnauthorizedUrl())) {
                // 如果没有配置未授权地址，由于不知道要重定向到什么地方，只能返回错误信息
                new ErrorRender(request, response).render(HttpStatus.UNAUTHORIZED, "未授权");
                return null;
            } else {
                var unauthorizedUrl = properties.getUnauthorizedUrl();

                var tenantPath = request.getHeader(XForwardedHeaders.PATH);
                if (Stringx.isNotBlank(tenantPath)) {
                    unauthorizedUrl = tenantPath + unauthorizedUrl;
                }

                log.info("[central-starter-security] 未授权，重定向到 " + unauthorizedUrl);

                var requestUrl = request.getHeader(XForwardedHeaders.ORIGIN_URI);
                if (Stringx.isNotBlank(requestUrl)) {
                    requestUrl = request.getRequestURL().toString();
                }

                // 如果请求的是页面，则重定向到指定地址
                var redirect = new RedirectView(Stringx.format("{}?redirect_uri={}", unauthorizedUrl, Stringx.encodeUrl(requestUrl)));
                return new ModelAndView(redirect);
            }
        }
    }
}
