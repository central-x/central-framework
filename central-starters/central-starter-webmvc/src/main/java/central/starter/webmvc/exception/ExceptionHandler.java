package central.starter.webmvc.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Nullable;

/**
 * 异常处理器
 *
 * @author Alan Yeh
 * @since 2022/07/16
 */
public interface ExceptionHandler {
    /**
     * 是否可以处理该异常
     *
     * @param throwable 待处理的异常
     */
    boolean support(Throwable throwable);

    @Nullable
    ModelAndView handle(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod, Throwable throwable);
}
