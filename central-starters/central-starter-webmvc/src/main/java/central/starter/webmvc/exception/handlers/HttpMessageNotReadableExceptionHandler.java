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

import central.lang.Stringx;
import central.starter.webmvc.exception.ExceptionHandler;
import central.util.Mapx;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.util.stream.Collectors;

/**
 * 消息序列化异常
 *
 * @author Alan Yeh
 * @see HttpMessageNotReadableException
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
        var ex = (HttpMessageNotReadableException) throwable;

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

        var mv = new ModelAndView(new MappingJackson2JsonView(), Mapx.newHashMap("message", message));
        mv.setStatus(HttpStatus.BAD_REQUEST);
        return mv;
    }
}