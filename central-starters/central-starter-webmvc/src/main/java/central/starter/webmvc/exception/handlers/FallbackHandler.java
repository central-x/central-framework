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
        var writer = new StringWriter();
        throwable.printStackTrace(new PrintWriter(writer));
        var reason = Arrays.stream(writer.toString().split("[\n]")).map(it -> it.replaceFirst("[\t]", "   ")).toArray();
        map.put("stack", reason);

        var mv = new ModelAndView(new MappingJackson2JsonView(), map);
        mv.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        return mv;
    }
}
