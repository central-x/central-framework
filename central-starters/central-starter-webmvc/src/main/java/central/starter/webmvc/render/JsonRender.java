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

import central.util.Jsonx;
import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Json Render
 *
 * @author Alan Yeh
 * @since 2022/07/16
 */
public class JsonRender extends Render<JsonRender> {
    @Getter
    private Map<String, Object> json = new HashMap<>();

    public JsonRender(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response) {
        super(request, response);
    }

    public JsonRender set(String name, Object value) {
        this.json.put(name, value);
        return this;
    }

    public JsonRender remove(String name) {
        this.json.remove(name);
        return this;
    }

    public JsonRender clear() {
        this.json.clear();
        return this;
    }

    public JsonRender setJson(Map<String, Object> json) {
        this.json.putAll(json);
        return this;
    }

    public void render(Map<String, Object> json) throws IOException {
        this.setJson(json).render();
    }

    private static final String contentType = new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8).toString();

    @Override
    public void render() throws IOException {
        String text = Jsonx.Default().serialize(this.json);
        this.getResponse().setHeader(HttpHeaders.PRAGMA, "no-cache");
        this.getResponse().setHeader(HttpHeaders.CACHE_CONTROL, "no-cache");
        this.getResponse().setDateHeader(HttpHeaders.EXPIRES, 0);

        this.getResponse().setContentType(contentType);
        try (PrintWriter writer = this.getResponse().getWriter()) {
            writer.write(text);
        }
    }
}