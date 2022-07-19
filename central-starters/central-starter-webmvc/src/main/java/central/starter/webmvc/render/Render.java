package central.starter.webmvc.render;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import org.springframework.http.HttpStatusCode;

import java.io.IOException;
import java.util.Objects;

/**
 * Http 响应
 *
 * @author Alan Yeh
 * @since 2022/07/16
 */
public abstract class Render<T extends Render<?>> {
    @Getter
    protected HttpServletRequest request;
    @Getter
    protected HttpServletResponse response;

    @Getter
    protected HttpStatusCode status;

    /**
     * 设置请求上下文
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     */
    public Render(HttpServletRequest request, HttpServletResponse response) {
        this.request = Objects.requireNonNull(request);
        this.response = Objects.requireNonNull(response);
    }

    /**
     * 设置响应头
     */
    public T setHeader(String name, String value) {
        response.setHeader(name, value);
        return (T) this;
    }

    /**
     * 添加 Cookie
     */
    public T addCookie(Cookie cookie) {
        response.addCookie(cookie);
        return (T) this;
    }

    /**
     * 添加 Cookie
     */
    public T addCookie(String name, String value) {
        response.addCookie(new Cookie(name, value));
        return (T) this;
    }

    /**
     * 设置状态码
     */
    public T setStatus(HttpStatusCode status) {
        this.status = status;
        response.setStatus(status.value());
        return (T) this;
    }

    public abstract void render() throws IOException;
}
