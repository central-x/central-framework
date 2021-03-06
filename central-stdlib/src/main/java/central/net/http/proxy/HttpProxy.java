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

package central.net.http.proxy;

import central.io.IOStreamx;
import central.lang.reflect.TypeReference;
import central.net.http.*;
import central.net.http.body.extractor.JsonExtractor;
import central.net.http.body.extractor.StringExtractor;
import central.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;

import java.io.File;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

/**
 * Http Proxy
 *
 * @author Alan Yeh
 * @since 2022/07/19
 */
@RequiredArgsConstructor
public class HttpProxy implements InvocationHandler {

    private final Class<?> proxyType;

    private final HttpClient client;

    private final Contract contract;

    protected MethodHandles.Lookup lookup;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.isDefault()) {
            // interface ??? default ??????
            var caller = method.getDeclaringClass();
            if (lookup == null) {
                var constructor = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class, int.class);
                constructor.setAccessible(true);
                lookup = constructor.newInstance(caller, MethodHandles.Lookup.PUBLIC | MethodHandles.Lookup.PRIVATE);
            }
            // ?????? lookup ???????????????????????????
            return lookup.unreflectSpecial(method, caller).bindTo(proxy).invokeWithArguments(args);
        } else if (Objects.equals("toString", method.getName())) {
            return Stringx.format("{}@({})", this.proxyType.getSimpleName(), this.client.getBaseUrl());
        } else {
            HttpRequest request = null;
            if (args != null && args.length == 1 && args[0] instanceof HttpRequest req) {
                // ??????????????????????????? HttpRequest
                request = req;
            }

            if (request == null) {
                // ????????????????????????
                request = contract.parse(proxy, method, args);
            }

            request.getUrl().setBaseUrl(this.client.getBaseUrl());
            request.setAttribute(HttpAttributes.PROXY_METHOD, method);

            // ????????????
            var response = client.execute(request);

            // ????????????

            // ??????????????????????????? HttpResponse
            // ???????????????????????????????????? HttpResponse
            if (HttpResponse.class.isAssignableFrom(method.getReturnType())) {
                return response;
            }

            // ???????????????????????????????????????
            // Response ???????????????????????????????????????
            if (!response.isSuccess()){
                try (response){
                    throw new HttpException(response.getRequest(), response);
                }
            }

            // ???????????????????????????????????????????????????
            if (InputStream.class.isAssignableFrom(method.getReturnType())) {
                return response.getBody().getInputStream();
            }

            try (response) {
                if (void.class == method.getReturnType()) {
                    // ??????????????????????????????
                    return null;
                }

                if (String.class.isAssignableFrom(method.getReturnType())) {
                    // ?????????????????????
                    return response.getBody().extract(new StringExtractor());
                }

                if (File.class.isAssignableFrom(method.getReturnType())) {
                    // ??????????????????
                    // ???????????????
                    var filename = response.getHeaders().getContentDisposition().getFilename();

                    var tmp = new File("tmp", Objectx.get(filename, Guidx.nextID() + ".tmp"));

                    IOStreamx.copy(response.getBody().getInputStream(), Files.newOutputStream(tmp.toPath(), StandardOpenOption.WRITE));
                    return tmp;
                }

                // ?????? JSON ????????????
                if (MediaType.APPLICATION_JSON.isCompatibleWith(response.getHeaders().getContentType())) {
                    return response.getBody().extract(new JsonExtractor<>(TypeReference.of(method.getGenericReturnType())));
                }

                throw new HttpException(request, response, "?????????????????????");
            }
        }
    }
}
