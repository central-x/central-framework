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

import central.io.IOStreamx;
import central.lang.Stringx;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import org.springframework.http.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * 文件响应
 * 不支持断点下载
 *
 * @author Alan Yeh
 * @since 2022/07/16
 */
public class FileRender extends Render<FileRender> {
    private InputStream input;

    public FileRender(HttpServletRequest request, HttpServletResponse response) {
        super(request, response);
    }

    /**
     * 指定文件名
     * 如果不认置，则使用 file#getName
     */
    @Getter
    private String fileName;

    public FileRender setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    /**
     * 文件消息摘要
     * 可以用于唯一确认文件
     */
    @Getter
    private String digest;

    public FileRender setDigest(String digest) {
        this.digest = digest;
        return this;
    }

    private String contentType;

    public FileRender setContentType(String contentType) {
        if (Stringx.isNotBlank(contentType)) {
            this.contentType = contentType;
        }
        return this;
    }

    public String getContentType() {
        if (Stringx.isNotBlank(this.contentType)) {
            return this.contentType;
        }
        if (Stringx.isNullOrBlank(this.fileName)) {
            return MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        return MediaTypeFactory.getMediaType(this.fileName).orElse(MediaType.APPLICATION_OCTET_STREAM).toString();
    }


    /**
     * 用于下载时指定文件名
     */
    @Getter
    private String contentDisposition;

    public FileRender setContentDisposition(String contentDisposition) {
        if (Stringx.isNotBlank(contentDisposition)) {
            this.contentDisposition = contentDisposition;
        }
        return this;
    }

    @Getter
    private Long contentLength = -1L;

    public FileRender setContentLength(Long contentLength) {
        if (contentLength != null) {
            this.contentLength = contentLength;
        }
        return this;
    }

    public FileRender setFile(File file) throws IOException {
        this.input = new FileInputStream(file);
        return this;
    }

    public FileRender setInputStream(InputStream input) {
        this.input = input;
        return this;
    }

    @Override
    public FileRender setStatus(HttpStatusCode status) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + " 不支持设置状态码");
    }

    public void render(File file) throws IOException {
        this.setFile(file).render();
    }

    public void render(InputStream input) throws IOException {
        this.setInputStream(input).render();
    }

    @Override
    public void render() throws IOException {
        setHeaders();

        try (var input = this.input; var output = getResponse().getOutputStream()) {
            IOStreamx.transfer(input, output);
        }
    }

    private void setHeaders() throws IOException {
        // 设置 Content-Disposition 响应头
        if (Stringx.isNotBlank(this.contentDisposition)) {
            // 由于部份客户端没有遵循 Http 协议的要求来解决文件名，因此这里开放接口，允许设置不标准的 Content-Disposition 响应头
            this.getResponse().setHeader(HttpHeaders.CONTENT_DISPOSITION, this.contentDisposition);
        } else {
            // 以下代码经过如下浏览器测试均正常
            // 移动：Safari(iOS)、UC浏览器、QQ浏览器、360浏览器、搜狗浏览器、2345浏览器、猎豹浏览器、Chrome、欧朋浏览器、夸克浏览器、CM Browser、魅族浏览器、小米浏览器、华为浏览器、E人E本浏览器、FireFox(下载提示时文件名不正常，下载完了之后是正常的)
            // PC：Safari(Mac)、Chrome、IE8、IE11、Edge、360浏览器（兼容和极速模式均可）、QQ浏览器、FireFox、世界之窗、Opera、Safari(Win)、傲游浏览器
            String userAgent = this.getRequest().getHeader(HttpHeaders.USER_AGENT).toLowerCase();
            do {
                if (userAgent.contains("applewebkit")) {
                    if (userAgent.contains("macintosh")) {
                        // Mac 的 Safari 在 15.0 之后，使用标准的文件名解析方法
                        String[] parts = userAgent.split(" ");
                        Double version = null;
                        for (String part : parts) {
                            if (part.startsWith("version/")) {
                                try {
                                    version = Double.parseDouble(part.replace("version/", ""));
                                } catch (Exception ignored) {
                                }
                                break;
                            }
                        }
                        if (version != null && version >= 15.0f) {
                            this.getResponse().setHeader(HttpHeaders.CONTENT_DISPOSITION, Stringx.format("attachment;filename=\"{}\";filename*=utf-8''{}", Stringx.encodeUrl(this.getFileName()), Stringx.encodeUrl(this.getFileName())));
                            break;
                        }

                        // webkit 内核，和 Safari 15.0 之前的浏览器，文件名的解析有点问题
                        this.getResponse().setHeader(HttpHeaders.CONTENT_DISPOSITION, Stringx.format("attachment;filename=\"{}\"", new String(this.getFileName().getBytes(), StandardCharsets.ISO_8859_1)));
                        break;
                    }
                }
                // 其余的浏览器都使用标准的协议
                this.getResponse().setHeader(HttpHeaders.CONTENT_DISPOSITION, Stringx.format("attachment;filename=\"{}\";filename*=utf-8''{}", Stringx.encodeUrl(this.getFileName()), Stringx.encodeUrl(this.getFileName())));
            } while (false);
        }

        // 设置 Etag，文件唯一标识，可以使用文件的消息摘要
        if (Stringx.isNotBlank(getDigest())) {
            this.getResponse().setHeader(HttpHeaders.ETAG, this.getDigest());
        }

        // 设置 Content-Type 为流形式
        this.getResponse().setHeader(HttpHeaders.CONTENT_TYPE, this.getContentType());

        this.getResponse().setStatus(200);
        // 设置 Content-Length 为整个文件的大小
        this.getResponse().setHeader(HttpHeaders.CONTENT_LENGTH, this.contentLength.toString());
    }
}
