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
import central.util.Range;
import central.lang.Stringx;
import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import org.springframework.http.*;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * 断点下载
 *
 * @author Alan Yeh
 * @since 2022/07/16
 */
public class ResumableFileRender extends Render<ResumableFileRender> {
    public ResumableFileRender(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response) {
        super(request, response);
    }

    /**
     * 待响应的文件
     */
    @Getter
    private File file;

    /**
     * 指定文件名
     * 如果不认置，则使用 file#getName
     */
    @Getter
    private String fileName;

    public ResumableFileRender setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    /**
     * 文件消息摘要
     * 可以用于唯一确认文件
     */
    @Getter
    private String digest;

    public ResumableFileRender setDigest(String digest) {
        this.digest = digest;
        return this;
    }

    private String contentType;

    public ResumableFileRender setContentType(String contentType) {
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

    public ResumableFileRender setContentDisposition(String contentDisposition) {
        if (Stringx.isNotBlank(contentDisposition)) {
            this.contentDisposition = contentDisposition;
        }
        return this;
    }

    @Override
    public ResumableFileRender setStatus(HttpStatusCode status) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + " 不支持设置状态码");
    }

    public ResumableFileRender setFile(File file) {
        this.file = file;
        if (Stringx.isNullOrBlank(this.fileName)) {
            this.fileName = file.getName();
        }
        return this;
    }

    public void render(File file) throws IOException {
        this.setFile(file).render();
    }

    @Override
    public void render() throws IOException {

        HttpServletResponse response = getResponse();
        if (response == null) {
            throw new IllegalStateException("请先通过 setContext 设置 HttpServletRequest 和 HttpServletResponse 上下文");
        }

        setHeaders();
        // 获取这次要写入的区间长度
        Range<Long> range = this.getRange(this.getRequest().getHeader(HttpHeaders.RANGE), file.length());

        if (range == null) {
            try (var input = new FileInputStream(this.file); var output = getResponse().getOutputStream()) {
                IOStreamx.transfer(input, output);
            }
        } else {
            try (RandomAccessFile raf = new RandomAccessFile(this.file, "r"); BufferedOutputStream bufferedOutput = new BufferedOutputStream(getResponse().getOutputStream())) {
                // 缓冲
                byte[] buffer = new byte[IOStreamx.BUFFER_SIZE];

                // 跳到指定的开发的点
                long index = range.getMinimum();
                raf.seek(index);
                do {
                    int length = raf.read(buffer, 0, ((Long) Math.min(buffer.length, range.getMaximum() - index + 1)).intValue());
                    if (length > 0) {
                        bufferedOutput.write(buffer, 0, length);
                    }
                    index += length;
                } while (index < range.getMaximum());
            }
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

        // 设置接收的 range 属性的值
        this.getResponse().setHeader(HttpHeaders.ACCEPT_RANGES, "bytes");
        // 设置 Content-Type 为流形式
        this.getResponse().setHeader(HttpHeaders.CONTENT_TYPE, this.getContentType());

        // 根据是否断点下载的头部请求，设置对应的头部信息
        Range<Long> rangeHeader = getRange(this.getRequest().getHeader(HttpHeaders.RANGE), file.length());

        if (rangeHeader == null) {
            // 如果 Range 请求头为空，则说明是全部下载。全部下载的状态码是 200
            this.getResponse().setStatus(HttpStatus.OK.value());
            // 设置 Content-Length 为整个文件的大小
            this.getResponse().setHeader(HttpHeaders.CONTENT_LENGTH, Long.toString(file.length()));
        } else {
            // 如果 Range 请求头不为空，则说明是断点下载，断点下载的状态码是 206
            this.getResponse().setStatus(HttpStatus.PARTIAL_CONTENT.value());
            // 设置 Range 头部
            this.getResponse().setHeader(HttpHeaders.CONTENT_RANGE, Stringx.format("bytes {}-{}/{}", rangeHeader.getMinimum(), rangeHeader.getMaximum(), Long.toString(file.length())));
            // 设置 Content-Length 头部
            this.getResponse().setHeader(HttpHeaders.CONTENT_LENGTH, Long.toString(rangeHeader.getMaximum() - rangeHeader.getMinimum() + 1L));
        }
    }

    private Range<Long> range = null;

    // 根据请求头获取头部。
    // 包含开头与结束
    // Range: bytes=start-end
    // 可能有三种格式
    // Range: bytes=-500   : 只需要最后500字节的长度
    // Range: bytes=500-   : 只需要从第500索引开始到最后的长度
    // Range: bytes=100-499: 只需要第100到499索引的长度
    //
    // 不支持多区间下载格式 Range: bytes=500-600,601-999
    private Range<Long> getRange(String rangeHeader, Long fileSize) {
        if (this.range != null) {
            return this.range;
        }

        if (Stringx.isNullOrBlank(rangeHeader)) {
            // 没有 Range 头部
            return null;
        }
        if (!rangeHeader.startsWith("bytes=")) {
            // 格式错误
            return null;
        }
        if (!rangeHeader.contains("-")) {
            // 格式错误
            return null;
        }
        if (rangeHeader.contains(",")) {
            // 不支持多区间下载
            return null;
        }
        rangeHeader = Stringx.removePrefix(rangeHeader, "bytes=");

        if (rangeHeader.endsWith("-")) {
            // Range: bytes=500-
            rangeHeader = Stringx.removeSuffix(rangeHeader, "-");
            long start = Long.parseLong(rangeHeader);
            if (start > fileSize) {
                // 超过文件大小了，不支持
                return null;
            }
            this.range = new Range<>(start, fileSize - 1);
        } else if (rangeHeader.startsWith("-")) {
            // Range: bytes=-500
            rangeHeader = Stringx.removePrefix(rangeHeader, "-");
            long size = Long.parseLong(rangeHeader);
            if (size > fileSize) {
                // 超过文件大小了，不支持
                return null;
            } else {
                this.range = new Range<>(fileSize - 1 - size, fileSize - 1);
            }
        } else {
            String[] ranges = rangeHeader.split("[-]");
            if (ranges.length != 2) {
                // 不是有效格式
                return null;
            }
            Long start = Long.parseLong(ranges[0]);
            Long end = Long.parseLong(ranges[1]);

            if (start > end) {
                // 不是有效格式
                return null;
            }

            this.range = new Range<>(start, end);
        }

        return this.range;
    }
}
