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

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * Spring 注解
 *
 * @author Alan Yeh
 * @since 2022/07/18
 */
public interface SpringResolver {

    /**
     * 是否支持解析该参数
     *
     * @param parameter 字段信息
     */
    boolean support(Parameter parameter);

    /**
     * 解析参数
     *
     * @param request   请求
     * @param method    方法信息
     * @param parameter 字段信息
     * @param arg       参数值
     * @return 是否已解析，如果返回 false，会交给下一个解析器去处理
     */
    boolean resolve(HttpRequest request, Method method, Parameter parameter, Object arg);
}
