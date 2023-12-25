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

import central.lang.Assertx;
import central.lang.Stringx;
import central.net.http.HttpException;
import central.net.http.HttpRequest;
import central.net.http.HttpResponse;
import jakarta.annotation.Nonnull;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.io.Serial;

/**
 * Client Exception
 * <p>
 * 客户端异常
 * <p>
 * 当服务器端返回的状态码是 400~499 范围时，将抛此异常
 *
 * @author Alan Yeh
 * @since 2023/12/25
 */
@Getter
public class ClientSeriesHttpException extends HttpException {
    @Serial
    private static final long serialVersionUID = -3991361930158736573L;

    private transient final HttpStatus status;

    public ClientSeriesHttpException(@Nonnull HttpStatus status, @Nonnull HttpRequest request, @Nonnull HttpResponse response) {
        super(Stringx.format("[{} {}] {} {}", status.value(), status.getReasonPhrase(), request.getMethod().name(), request.getUrl().getValue()), request, response, null);
        Assertx.mustEquals(HttpStatus.Series.CLIENT_ERROR, HttpStatus.Series.resolve(status.value()), "Parameter 'status' must be Server Series Status");
        this.status = status;
    }
}
