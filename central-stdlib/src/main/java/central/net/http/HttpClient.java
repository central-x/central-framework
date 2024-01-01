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

package central.net.http;

import central.io.Filex;
import central.lang.Assertx;
import central.lang.Stringx;
import central.net.http.processor.HttpProcessor;
import central.pattern.chain.ProcessChain;
import central.util.Guidx;
import central.util.Listx;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;

/**
 * HttpClient
 *
 * @author Alan Yeh
 * @since 2022/07/17
 */
public class HttpClient {

    /**
     * Http 请求执行器
     * 可以由各类第三方框架提供请求执行能力
     */
    @Getter
    private final HttpExecutor executor;

    public HttpClient(HttpExecutor executor) {
        this.executor = executor;
    }

    /**
     * 基础 URL
     * HttpClient 会处理所有请求，如果请求是一个相对路径请求，则会使用基础 URL 作为其基础服务信息
     */
    @Getter
    private String baseUrl;

    public void setBaseUrl(String baseUrl) {
        if (Stringx.isNotBlank(baseUrl)) {
            URI uri = URI.create(baseUrl);
            Assertx.mustTrue(Stringx.isNotBlank(uri.getScheme()) && Stringx.isNotBlank(uri.getHost()), "Parameter 'baseUrl' is not a valid url: Missing schema or host.");
            this.baseUrl = baseUrl;
        } else {
            this.baseUrl = null;
        }
    }

    /**
     * 缓存目录
     */
    @Getter
    private File tmp = new File("./tmp");

    public void setTmp(File tmp) throws IOException {
        if (!tmp.exists()) {
            if (!tmp.mkdirs()) {
                throw new AccessDeniedException("无法访问目录: " + tmp.getAbsolutePath());
            }
        }

        // 尝试写缓存
        var test = new File(tmp, Guidx.nextID() + ".t");
        try {
            if (!test.createNewFile()) {
                throw new AccessDeniedException("无法访问目录: " + tmp.getAbsolutePath());
            }
        } finally {
            Filex.delete(test);
        }
        this.tmp = tmp;
    }

    @Getter
    private final List<HttpProcessor> processors = new ArrayList<>();

    /**
     * 添加请求处理器
     *
     * @param processor 请求处理器
     */
    public void addProcessor(HttpProcessor processor) {
        this.processors.add(processor);
    }

    /**
     * 添加请求处理器
     *
     * @param processors 请求处理器
     */
    public void addProcessors(List<HttpProcessor> processors) {
        if (Listx.isNotEmpty(processors)) {
            this.processors.addAll(processors);
        }
    }

    /**
     * 执行请求
     *
     * @param request 执行请求
     * @return 响应
     */
    public HttpResponse execute(HttpRequest request) throws Exception {
        request.getUrl().setBaseUrl(this.getBaseUrl());

        List<HttpProcessor> processors = new ArrayList<>(this.processors);
        // 最后一个处理链使用 executor 发送请求
        processors.add(this.executeProcessor);

        // 构建处理链
        var chain = new ProcessChain<>(processors);

        // 处理请求
        return chain.process(request);
    }

    private final HttpProcessor executeProcessor = (target, chain) -> getExecutor().execute(target);
}
