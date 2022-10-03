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

package central.test.orm.database;

import central.sql.SqlInterceptor;
import central.sql.SqlMetaManager;
import central.sql.SqlSource;
import central.sql.impl.standard.StandardMetaManager;
import central.sql.interceptor.LogInterceptor;
import central.starter.orm.SqlExecutorConfigurer;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Sql 执行器配置
 *
 * @author Alan Yeh
 * @since 2022/09/26
 */
@Component
public class ApplicationExecutorConfigurer implements SqlExecutorConfigurer {
    @Setter(onMethod_ = @Autowired)
    private ApplicationSource source;

    @Override
    public SqlSource getSource() {
        return this.source;
    }

    @Override
    public SqlMetaManager getMetaManager() {
        return new StandardMetaManager(name -> name.startsWith("XT_"));
    }

    @Override
    public List<SqlInterceptor> getInterceptors() {
        return List.of(new LogInterceptor());
    }
}
