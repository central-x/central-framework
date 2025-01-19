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

package central.net.http.executor.local;

import central.net.http.HttpExecutor;
import central.net.http.HttpRequest;
import central.net.http.HttpResponse;

/**
 * Local Executor
 * <p>
 * 本地执行器
 * <p>
 * 不走网络请求，通过模拟 HttpServletRequest 和 HttpServletResponse 来模拟请求和响应从而提升执行速度
 *
 * @author Alan Yeh
 * @since 2025/01/19
 */
public class LocalExecutor implements HttpExecutor {
    @Override
    public String getName() {
        return "Local";
    }

    @Override
    public HttpResponse execute(HttpRequest request) throws Exception {
        return null;
    }
}
