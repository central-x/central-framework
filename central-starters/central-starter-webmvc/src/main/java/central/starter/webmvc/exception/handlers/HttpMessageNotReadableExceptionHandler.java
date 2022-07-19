package central.starter.webmvc.exception.handlers;

import central.starter.webmvc.exception.ExceptionHandler;
import central.util.Mapx;
import central.util.Stringx;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.annotation.Nullable;
import java.util.stream.Collectors;

/**
 * 消息序列化异常
 *
 * @author Alan Yeh
 * @since 2022/07/17
 */
@Component
public class HttpMessageNotReadableExceptionHandler implements ExceptionHandler {

    @Override
    public boolean support(Throwable throwable) {
        return throwable instanceof HttpMessageNotReadableException;
    }

    @Nullable
    @Override
    public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod, Throwable throwable) {
        HttpMessageNotReadableException ex = (HttpMessageNotReadableException) throwable;

        String message;
        if (ex.getCause() != null) {
            if (ex.getCause() instanceof JsonParseException jsonEx) {
                message = "JSON序列化异常: " + jsonEx.getOriginalMessage();
            } else if (ex.getCause() instanceof JsonMappingException jsonEx) {
                String path = jsonEx.getPath().stream().map(JsonMappingException.Reference::getFieldName).collect(Collectors.joining("."));

                message = Stringx.format("JSON序列化异常: 字段[{}]序列化错误，{}", path, jsonEx.getOriginalMessage());
            } else {
                message = "消息序列化异常: " + ex.getCause().getMessage();
            }
        } else {
            message = "消息序列化异常: " + ex.getMessage();
        }

        ModelAndView mv = new ModelAndView(new MappingJackson2JsonView(), Mapx.newHashMap("message", message));
        mv.setStatus(HttpStatus.BAD_REQUEST);
        return mv;
    }
}