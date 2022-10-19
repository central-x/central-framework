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
import central.lang.Stringx;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.annotation.Nullable;

/**
 * MissingServletRequestParameterException Handler
 *
 * @author Alan Yeh
 * @see MissingServletRequestParameterException
 * @since 2022/07/17
 */
@Component
public class MissingServletRequestParameterExceptionHandler implements ExceptionHandler {

    @Override
    public boolean support(Throwable throwable) {
        return throwable instanceof MissingServletRequestParameterException;
    }

    @Nullable
    @Override
    public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod, Throwable throwable) {
        var ex = (MissingServletRequestParameterException) throwable;

        var body = Mapx.newHashMap("message", Stringx.format("参数[{}]必须不为空", ex.getParameterName()));
        var mv = new ModelAndView(new MappingJackson2JsonView(), body);
        mv.setStatus(HttpStatus.BAD_REQUEST);
        return mv;
    }
}
