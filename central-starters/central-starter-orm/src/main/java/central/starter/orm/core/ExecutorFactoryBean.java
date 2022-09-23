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

package central.starter.orm.core;

import central.sql.SqlExecutor;
import central.starter.orm.SqlExecutorConfigurer;
import lombok.Setter;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;

/**
 * SqlExecutor Factory
 *
 * @author Alan Yeh
 * @since 2022/09/22
 */
public class ExecutorFactoryBean<T extends SqlExecutor> implements FactoryBean<T>, InitializingBean {

    @Setter(onMethod_ = @Autowired)
    private SqlExecutorConfigurer configurer;

    @Setter(onMethod_ = @Autowired)
    private DataSource dataSource;

    public ExecutorFactoryBean() {

    }

    private T executor;

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    @Override
    public Class<?> getObjectType() {
        return SqlExecutor.class;
    }

    @Override
    public T getObject() throws Exception {
        return executor;
    }
}
