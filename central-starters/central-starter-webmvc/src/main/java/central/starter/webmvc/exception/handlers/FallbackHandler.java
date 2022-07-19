package central.starter.webmvc.exception.handlers;

import central.starter.webmvc.exception.ExceptionHandler;
import central.util.Mapx;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.annotation.Nullable;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Map;

/**
 * 最后
 *
 * @author Alan Yeh
 * @since 2022/07/16
 */
@Slf4j
public class FallbackHandler implements ExceptionHandler {

    @Override
    public boolean support(Throwable throwable) {
        return true;
    }

    @Nullable
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
}
