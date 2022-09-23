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

import central.net.http.HttpClient;
import central.net.http.HttpExecutor;
import central.net.http.processor.HttpProcessor;
import central.net.http.processor.impl.LoggerProcessor;
import central.net.http.proxy.contract.internal.InternalContract;
import central.lang.Arrayx;
import central.lang.Assertx;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

/**
 * HttpClient Factory
 *
 * @author Alan Yeh
 * @since 2022/07/13
 */
public class HttpProxyFactory {

    public static Builder builder(HttpExecutor executor) {
        return new Builder(executor);
    }

    public static class BaseBuilder<T extends BaseBuilder<T>> {
        protected String baseUrl;

        /**
         * 设置基础 URL
         */
        public T baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return (T) this;
        }

        protected Contract contract = new InternalContract();

        /**
         * 设置代理解析契约
         *
         * @param contract 契约
         */
        public T contact(Contract contract) {
            this.contract = contract;
            return (T) this;
        }
    }

    @RequiredArgsConstructor
    public static class Builder extends BaseBuilder<Builder> {
        private final HttpExecutor executor;

        /**
         * 请求处理器
         */
        private final List<HttpProcessor> processors = new ArrayList<>();

        /**
         * 添加请求处理器
         *
         * @param processor 处理器
         */
        public Builder processor(HttpProcessor processor) {
            this.processors.add(processor);
            return this;
        }

        /**
         * 添加请求处理器
         *
         * @param processors 处理器
         */
        public Builder processors(List<HttpProcessor> processors) {
            this.processors.addAll(processors);
            return this;
        }

        /**
         * 添加日志处理器
         */
        public Builder log() {
            this.processors.add(new LoggerProcessor());
            return this;
        }

        public <T> T target(Class<T> target) {
            Assertx.mustTrue(target.isInterface(), "Required Interface");
            return (T) Proxy.newProxyInstance(target.getClassLoader(), Arrayx.newArray(target), new HttpProxy(target, this.build(), this.contract));
        }

        public HttpClient build() {
            var client = new HttpClient(this.executor);
            client.setBaseUrl(this.baseUrl);
            client.addProcessors(this.processors);
            return client;
        }
    }
}
