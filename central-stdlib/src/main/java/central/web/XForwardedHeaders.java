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

package central.web;

/**
 * X-Forwarded-*
 * 这些请求头用于在微服务端流转
 *
 * @author Alan Yeh
 * @since 2022/07/16
 */
public interface XForwardedHeaders {
    /**
     * 被代理的主机信息
     */
    String HOST = "X-Forwarded-Host";
    /**
     * 被代理的端口信息
     */
    String PORT = "X-Forwarded-Port";
    /**
     * 被代理的协议信息
     */
    String SCHEMA = "X-Forwarded-Proto";
    /**
     * 代理客户端信息
     */
    String FOR = "X-Forwarded-For";
    /**
     * 租户标识
     */
    String TENANT = "X-Forwarded-Tenant";
    /**
     * 租户路径
     */
    String PATH = "X-Forwarded-Path";
    /**
     * 凭证信息
     */
    String TOKEN = "X-Forwarded-Token";
    /**
     * 原始请求信息
     */
    String ORIGIN_URI = "X-Forwarded-OriginUri";
    /**
     * 请求版本信息
     * 用于控制后端微服务的灰度版本信息，让请求在指定版本的微服务中流转
     */
    String VERSION = "X-Forwarded-Version";
    /**
     * 追踪信息
     */
    String TRACE = "X-Forwarded-Trace";
}
