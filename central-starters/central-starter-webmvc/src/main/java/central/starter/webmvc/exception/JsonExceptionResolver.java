package central.starter.webmvc.exception;

import central.util.Listx;
import central.util.Mapx;
import central.util.Stringx;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.annotation.Nullable;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 全局异常处理
 *
 * @author Alan Yeh
 * @since 2022/07/16
 */
@Slf4j
@Component
public class JsonExceptionResolver extends ExceptionHandlerExceptionResolver {
    @Setter(onMethod_ = @Autowired)
    private List<ExceptionHandler> handlers;

    @Value("${spring.application.name:}")
    private String applicationName;

    @Override
    protected ModelAndView doResolveHandlerMethodException(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod, Exception exception) {
        ModelAndView result = null;

        if (Listx.isNotEmpty(handlers)) {
            for (ExceptionHandler handler : handlers) {
                if (handler.support(exception)) {
                    result = handler.handle(request, response, handlerMethod, exception);
                    break;
                }
            }
        }

        if (result == null) {
            result = this.fallbackHandler.handle(request, response, handlerMethod, exception);
        }

        if (Stringx.isNotEmpty(this.applicationName)) {
            response.addHeader("X-", applicationName);
        }
        return result;
    }

    private ExceptionHandler fallbackHandler = new ExceptionHandler() {
        @Override
        public boolean support(Throwable throwable) {
            return true;
        }

        @Override
        public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod, Throwable throwable) {
            // 输出异常日志
            log.error(throwable.getLocalizedMessage(), throwable);

            Map<String, Object> map = Mapx.newHashMap("message", throwable.getMessage());

            // 如果是在 debug 模式下，输出完整的错误信息到前端
            StringWriter writer = new StringWriter();
            throwable.printStackTrace(new PrintWriter(writer));
            Object[] reason = Arrays.stream(writer.toString().split("[\n]")).map(it -> it.replaceFirst("[\t]", "   ")).toArray();
            map.put("stack", reason);

            ModelAndView mv = new ModelAndView(new MappingJackson2JsonView(), map);
            mv.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            return mv;
        }
    };
}
