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

import central.lang.Stringx;
import central.net.http.exception.*;
import central.util.Mapx;
import central.util.function.TriFunction;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.io.Serial;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Http Exception
 *
 * @author Alan Yeh
 * @since 2022/07/14
 */
@Getter
public class HttpException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 8580026645615849189L;

    private transient @Nonnull
    final HttpRequest request;

    private transient @Nullable
    final HttpResponse response;

    private static final Map<HttpStatus, BiFunction<HttpRequest, HttpResponse, ? extends HttpException>> STATUS_ERRORS = Mapx.of(
            Mapx.entry(HttpStatus.BAD_REQUEST, BadRequestHttpException::new),
            Mapx.entry(HttpStatus.UNAUTHORIZED, UnauthorizedHttpException::new),
            Mapx.entry(HttpStatus.FORBIDDEN, ForbiddenHttpException::new),
            Mapx.entry(HttpStatus.NOT_FOUND, NotFoundHttpException::new),
            Mapx.entry(HttpStatus.METHOD_NOT_ALLOWED, MethodNotAllowedHttpException::new),
            Mapx.entry(HttpStatus.REQUEST_TIMEOUT, RequestTimeoutHttpException::new),
            Mapx.entry(HttpStatus.TOO_MANY_REQUESTS, TooManyRequestsHttpException::new),
            Mapx.entry(HttpStatus.INTERNAL_SERVER_ERROR, InternalServerErrorHttpException::new),
            Mapx.entry(HttpStatus.SERVICE_UNAVAILABLE, ServiceUnavailableHttpException::new),
            Mapx.entry(HttpStatus.BAD_GATEWAY, BadGatewayHttpException::new)
    );

    private static final Map<HttpStatus.Series, TriFunction<HttpStatus, HttpRequest, HttpResponse, ? extends HttpException>> SERIES_ERRORS = Mapx.of(
            Mapx.entry(HttpStatus.Series.CLIENT_ERROR, ClientSeriesHttpException::new),
            Mapx.entry(HttpStatus.Series.SERVER_ERROR, ServerSeriesHttpException::new)
    );

    /**
     * 快速构建 Http 异常
     *
     * @param request  请求
     * @param response 响应
     */
    public static HttpException of(@Nonnull HttpRequest request, @Nullable HttpResponse response) {
        if (response != null) {
            {
                var error = STATUS_ERRORS.get(response.getStatus());
                if (error != null) {
                    return error.apply(request, response);
                }
            }

            {
                var error = SERIES_ERRORS.get(HttpStatus.Series.resolve(response.getStatus().value()));
                if (error != null) {
                    return error.apply(response.getStatus(), request, response);
                }
            }
            return new HttpException(request, response);
        } else {
            return new HttpException(request);
        }
    }

    public HttpException(String message, @Nonnull HttpRequest request, @Nullable HttpResponse response, Throwable throwable) {
        super(message);
        this.request = request;
        this.response = response;
    }

    public HttpException(@Nonnull HttpRequest request, @Nonnull HttpResponse response) {
        super(Stringx.format("[{} {}] {} {}", response.getStatus().value(), response.getStatus().getReasonPhrase(), request.getMethod().name(), request.getUrl()));
        this.request = request;
        this.response = response;
    }

    public HttpException(@Nonnull HttpRequest request) {
        super(Stringx.format("{} {}", request.getMethod().name(), request.getUrl()));
        this.request = request;
        this.response = null;
    }
}
