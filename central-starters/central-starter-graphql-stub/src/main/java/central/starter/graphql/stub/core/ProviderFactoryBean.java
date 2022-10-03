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

package central.starter.graphql.stub.core;

import central.lang.Assertx;
import central.starter.graphql.stub.ProviderClient;
import central.starter.graphql.stub.ProviderStub;
import central.util.MarkdownResources;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import java.lang.reflect.Proxy;

/**
 * Provider Factory
 *
 * @author Alan Yeh
 * @since 2022/09/25
 */
public class ProviderFactoryBean<T extends ProviderStub> implements FactoryBean<T>, InitializingBean {

    /**
     * 类型
     */
    @Getter
    private final Class<T> objectType;

    /**
     * 对象名
     */
    @Setter
    private String name;

    /**
     * 客户端
     */
    @Setter
    private ProviderClient client;

    private final MarkdownResources resources = new MarkdownResources();

    public ProviderFactoryBean(Class<T> objectType) {
        this.objectType = objectType;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        var resources = Thread.currentThread().getContextClassLoader().getResources("central/graphql/stub/" + this.name + ".md");
        Assertx.mustTrue(resources.hasMoreElements(), IllegalStateException::new, "找不到资源文件[central/graphql/stub/" + this.name + ".md]");

        while (resources.hasMoreElements()) {
            var resource = resources.nextElement();
            try (var is = resource.openStream()) {
                this.resources.load(is);
            }
        }
    }

    /**
     * 获取代理对象
     */
    @Override
    public T getObject() throws Exception {
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{this.getObjectType()}, new ProviderStubProxy(this.objectType, this.client, this.resources));
    }
}
