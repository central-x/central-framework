package central.starter.webmvc.render;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * 文本响应
 *
 * @author Alan Yeh
 * @since 2022/07/16
 */
public class TextRender extends Render<TextRender> {
    @Getter
    private String text;

    public TextRender(HttpServletRequest request, HttpServletResponse response) {
        super(request, response);
    }

    public TextRender setText(String text) {
        this.text = text;
        return this;
    }

    public void render(String text) throws IOException {
        this.setText(text).render();
    }

    @Override
    public void render() throws IOException {
        response.setHeader(HttpHeaders.PRAGMA, "no-cache");
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-cache");
        response.setDateHeader(HttpHeaders.EXPIRES, 0);
        response.setContentType("text/html; charset=utf-8");
        PrintWriter writer = response.getWriter();
        writer.write(text);
    }
}
