package central.starter.webmvc.exception.handlers;

import central.starter.webmvc.exception.ExceptionHandler;
import central.util.Stringx;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * 参数校验错误异常处理
 *
 * @author Alan Yeh
 * @since 2022/07/16
 */
@Component
public class BindExceptionHandler implements ExceptionHandler {

    @Override
    public boolean support(Throwable throwable) {
        return throwable instanceof BindException;
    }

    @Nullable
    @Override
    public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod, Throwable throwable) {
        BindException ex = (BindException) throwable;

        ObjectError error = ex.getBindingResult().getAllErrors().get(0);

        Map<String, String> body = new HashMap<>(1);

        if (error instanceof FieldError fieldError){
            if (Stringx.isNullOrBlank(fieldError.getDefaultMessage())){
                body.put("message", Stringx.format("参数[{}]类型不匹配", fieldError.getField()));
            } else {
                body.put("message", fieldError.getDefaultMessage());
            }
        } else {
            body.put("message", error.getDefaultMessage());
        }

        ModelAndView mv = new ModelAndView(new MappingJackson2JsonView(), body);
        mv.setStatus(HttpStatus.BAD_REQUEST);
        return mv;
    }
}
