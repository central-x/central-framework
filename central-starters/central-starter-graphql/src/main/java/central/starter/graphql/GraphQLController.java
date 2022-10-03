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

package central.starter.graphql;

import central.starter.graphql.core.GraphQLExecutor;
import central.util.Context;
import central.util.Jsonx;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * GraphQL 入口
 *
 * @author Alan Yeh
 * @since 2022/09/09
 */
@RestController
@RequestMapping("/api/graphql")
public class GraphQLController implements ApplicationContextAware {

    @Setter
    private ApplicationContext applicationContext;

    @Setter(onMethod_ = @Autowired)
    private GraphQLExecutor executor;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public String query(@RequestBody @Validated GraphQLRequest request,
                        HttpServletRequest servletRequest,
                        HttpServletResponse servletResponse) {

        var context = new Context();
        context.set(GraphQLRequest.class, request);
        context.set(HttpServletRequest.class, servletRequest);
        context.set(ServletRequest.class, servletRequest);
        context.set(HttpServletResponse.class, servletResponse);
        context.set(ServletResponse.class, servletResponse);
        context.set(ApplicationContext.class, this.applicationContext);

        Object result = executor.execute(request, context);
        return Jsonx.Default().serialize(result);
    }

}
