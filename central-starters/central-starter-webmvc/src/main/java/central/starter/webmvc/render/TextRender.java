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

package central.starter.webmvc.render;

import central.lang.Stringx;
import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * 文本响应
 *
 * @author Alan Yeh
 * @since 2022/07/16
 */
public class TextRender extends Render<TextRender> {
    @Getter
    private String text;

    public TextRender(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response) {
        super(request, response);
    }

    /**
     * 设置文本
     *
     * @param text 响应文本
     */
    public TextRender setText(String text) {
        this.text = text;
        return this;
    }

    /**
     * 渲染文本
     *
     * @param text 响应文本
     */
    public void render(String text) throws IOException {
        this.setText(text).render();
    }

    @Override
    public void render() throws IOException {
        this.getResponse().setHeader(HttpHeaders.PRAGMA, "no-cache");
        this.getResponse().setHeader(HttpHeaders.CACHE_CONTROL, "no-cache");
        this.getResponse().setDateHeader(HttpHeaders.EXPIRES, 0);
        if (Stringx.isNullOrEmpty(this.getResponse().getContentType())) {
            this.getResponse().setContentType(new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8).toString());
        }

        try (PrintWriter writer = this.getResponse().getWriter()) {
            writer.write(text);
        }
    }
}
