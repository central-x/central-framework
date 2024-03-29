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

package central.net.http.proxy.contract.spring;

import central.net.http.HttpRequest;
import central.net.http.HttpUrl;
import central.net.http.proxy.Contract;
import central.net.http.proxy.contract.spring.resolver.*;
import central.lang.Arrayx;
import central.lang.Assertx;
import central.lang.Stringx;
import central.util.Objectx;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Spring 注解
 *
 * @author Alan Yeh
 * @since 2022/07/18
 */
public class SpringContract implements Contract {

    private final List<SpringResolver> resolvers;

    public SpringContract() {
        resolvers = List.of(
                new RequestParamResolver(),
                new RequestBodyResolver(),
                new RequestPartResolver(),
                new PathVariableResolver(),
                new RequestHeaderResolver(),
                new CookieValueResolver(),
                new RequestAttributeResolver()
        );
    }

    @Override
    public HttpRequest parse(Object instance, Method method, Object[] args) {
        var request = new HttpRequest();

        var annotation = AnnotatedElementUtils.findMergedAnnotation(method, RequestMapping.class);

        // HTTP Method
        RequestMethod[] methods = null;
        if (annotation != null) {
            methods = annotation.method();
        }
        if (Arrayx.isNullOrEmpty(methods)) {
            methods = Arrayx.newArray(RequestMethod.GET);
        }
        request.setMethod(HttpMethod.valueOf(Objectx.getOrDefault(methods[0], RequestMethod.GET).name()));

        // Path
        String parentPath = getPathOnClass(instance);
        String methodPath = getPathOnMethod(annotation);
        if (Stringx.isNotBlank(parentPath) && Stringx.isNotBlank(methodPath)) {
            request.setUrl(HttpUrl.of(Stringx.removeSuffix(parentPath, "/") + "/" + Stringx.removePrefix(methodPath, "/")));
        } else if (Stringx.isNotBlank(methodPath)) {
            request.setUrl(HttpUrl.of(methodPath));
        } else if (Stringx.isNotBlank(parentPath)) {
            request.setUrl(HttpUrl.of(parentPath));
        } else {
            request.setUrl(HttpUrl.of(""));
        }

        // 处理 produces，也就是添加 ACCEPT 请求头
        if (annotation != null && Arrayx.isNotEmpty(annotation.produces())) {
            var accepts = Arrayx.asStream(annotation.produces()).filter(Stringx::isNotBlank).map(MediaType::parseMediaType).toList();
            if (!accepts.isEmpty()) {
                request.getHeaders().setAccept(accepts);
            }
        }

        // 处理 consumes，也就是添加 Content-Type 请求头
        if (annotation != null && Arrayx.isNotEmpty(annotation.consumes())) {
            if (Stringx.isNotBlank(annotation.consumes()[0])) {
                var contentType = MediaType.parseMediaType(annotation.consumes()[0]);
                request.getHeaders().setContentType(contentType);
            }
        }

        // 处理 Headers
        if (annotation != null && Arrayx.isNotEmpty(annotation.headers())) {
            for (var header : annotation.headers()) {
                String[] nameValue = header.split("[=]");
                Assertx.mustTrue(nameValue.length == 2, "Invalid header '{}'", header);
            }
        }

        // 处理入参
        var parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            for (var resolver : this.resolvers) {
                if (resolver.support(parameters[i])) {
                    if (resolver.resolve(request, method, parameters[i], args[i])) {
                        break;
                    }
                }
            }
        }

        // 完成所有处理
        return request;
    }

    private String getPathOnClass(Object instance) {
        var mapping = AnnotatedElementUtils.findMergedAnnotation(instance.getClass(), RequestMapping.class);
        if (mapping == null) {
            return "";
        } else {
            return mapping.value()[0];
        }
    }

    private String getPathOnMethod(RequestMapping mapping) {
        if (mapping == null || mapping.value().length == 0) {
            return "";
        } else {
            return mapping.value()[0];
        }
    }
}
