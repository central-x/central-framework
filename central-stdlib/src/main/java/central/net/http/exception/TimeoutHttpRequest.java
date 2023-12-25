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

package central.net.http.exception;

import central.lang.Stringx;
import central.net.http.HttpException;
import central.net.http.HttpRequest;
import jakarta.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;

/**
 * Timeout Http Request
 *
 * @author Alan Yeh
 * @since 2023/12/25
 */
public class TimeoutHttpRequest extends HttpException {
    @Serial
    private static final long serialVersionUID = -1002053311889529005L;

    public TimeoutHttpRequest(@NotNull HttpRequest request, @Nullable Throwable throwable) {
        super(Stringx.format("{} {}", request.getMethod().name(), request.getUrl().getValue()), request, null, throwable);
    }

    public TimeoutHttpRequest(@NotNull HttpRequest request) {
        super(Stringx.format("{} {}", request.getMethod().name(), request.getUrl().getValue()), request, null, null);
    }
}
