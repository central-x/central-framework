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

package central.starter.identity.exception;

import central.lang.Stringx;
import central.starter.identity.IdentityProperties;
import central.starter.webmvc.exception.ExceptionHandler;
import central.starter.webmvc.view.ErrorView;
import central.util.Listx;
import central.web.XForwardedHeaders;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.UnauthenticatedException;
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
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

/**
 * 未登录异常捕捉
 *
 * @author Alan Yeh
 * @since 2023/02/13
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class UnauthenticatedExceptionHandler implements ExceptionHandler {

    @Setter(onMethod_ = @Autowired)
    private IdentityProperties properties;

    @Override
    public boolean support(Throwable throwable) {
        return throwable instanceof UnauthenticatedException;
    }

    @Nullable
    @Override
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

        if (!returnJson) {
            var acceptContentTypes = MediaType.parseMediaTypes(request.getHeader(HttpHeaders.ACCEPT));
            boolean returnHtml = false;
            if (Listx.isNullOrEmpty(acceptContentTypes)) {
                returnHtml = true;
            } else {
                for (var type : acceptContentTypes) {
                    if (MediaType.ALL.equalsTypeAndSubtype(type) || MediaType.TEXT_HTML.equalsTypeAndSubtype(type)) {
                        returnHtml = true;
                        break;
                    }
                }
            }

            if (returnHtml) {
                // 如果客户端没有指定返回类型，一般就是浏览器请求
                // 未登录时，需要重定向到登录界面
                var unauthorizedUrl = properties.getUnauthorizedUrl();
                if (Stringx.isNotBlank(unauthorizedUrl)) {
                    var tenantPath = request.getHeader(XForwardedHeaders.PATH);
                    if (Stringx.isNotBlank(tenantPath)) {
                        unauthorizedUrl = tenantPath + unauthorizedUrl;
                    }

                    log.info("[central-starter-identity] 未登录，重定向到 " + unauthorizedUrl);

                    var requestUrl = request.getHeader(XForwardedHeaders.ORIGIN_URI);
                    if (Stringx.isNullOrBlank(requestUrl)) {
                        requestUrl = request.getRequestURL().toString();
                    }

                    // 如果请求的是页面，则重定向到指定地址
                    var redirect = new RedirectView(Stringx.format("{}?redirect_uri={}", unauthorizedUrl, Stringx.encodeUrl(requestUrl)));
                    return new ModelAndView(redirect);
                }
            }
        }

        // 如果客户端要求返回 JSON，或业务系统没有配置登录地址，由于不知道要重定向到什么地方，只能返回错误信息
        var mv = new ModelAndView(new ErrorView(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未登录")));
        mv.setStatus(HttpStatus.UNAUTHORIZED);
        return mv;
    }
}
