package central.starter.webmvc.exception.handlers;

import central.starter.webmvc.exception.ExceptionHandler;
import central.util.Mapx;
import central.util.Stringx;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.annotation.Nullable;

/**
 * @author Alan Yeh
 * @since 2022/07/17
 */
@Component
public class MethodArgumentTypeMismatchExceptionHandler implements ExceptionHandler {

    @Override
    public boolean support(Throwable throwable) {
        return throwable instanceof MethodArgumentTypeMismatchException;
    }

    @Nullable
    @Override
    public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod, Throwable throwable) {
        MethodArgumentTypeMismatchException ex = (MethodArgumentTypeMismatchException) throwable;

        var body = Mapx.newHashMap("message", Stringx.format("参数[{}]类型不匹配", ex.getParameter().getParameterName()));
        var mv = new ModelAndView(new MappingJackson2JsonView(), body);
        mv.setStatus(HttpStatus.BAD_REQUEST);
        return mv;
    }
}
