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

package central.net.http.body.extractor;

import central.io.IOStreamx;
import central.lang.Assertx;
import central.lang.Stringx;
import central.net.http.body.Body;
import central.net.http.body.BodyExtractor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

/**
 * 将响应体解析成文件
 *
 * @author Alan Yeh
 * @since 2022/11/18
 */
public class FileExtractor implements BodyExtractor<File> {

    /**
     * 文件保存路径
     */
    private final File location;
    /**
     * 文件名
     */
    private final String filename;

    /**
     * 构造函数
     *
     * @param location 文件保存路径
     * @param filename 文件名（如果未指定，将解析响应头里面指定的文件名）
     */
    public FileExtractor(@Nonnull File location, @Nullable String filename) throws IOException {
        if (location.exists()) {
            Assertx.mustTrue(location.isDirectory(), "参数[location]错误: 请提供文件夹路径");
        } else {
            Assertx.mustTrue(location.mkdirs(), IOException::new, "参数[location]错误: 无法访问指定路径");
        }
        this.location = location;
        this.filename = filename;
    }

    /**
     * 创建文件解析器
     *
     * @param location 文件保存路径
     * @param filename 文件名（如果未指定，将解析响应头里面指定的文件名）
     */
    public static FileExtractor of(@Nonnull File location, @Nullable String filename) throws IOException {
        return new FileExtractor(location, filename);
    }

    /**
     * 创建文件解析器（自动解析文件名）
     *
     * @param location 文件保存路径
     */
    public static FileExtractor of(@Nonnull File location) throws IOException {
        return new FileExtractor(location, null);
    }

    @Override
    public File extract(Body body) throws IOException {
        // 开发者指定的文件名
        var filename = this.filename;

        // 如果开发者没有指定文件名，则尝试从响应头里获取文件名
        if (Stringx.isNullOrBlank(filename)) {
            filename = body.getHeaders().getContentDisposition().getFilename();
        }

        // 如果没有办法解析文件名，就只能使用临时文件名了
        if (Stringx.isNullOrBlank(filename)) {
            filename = UUID.randomUUID().toString() + ".tmp";
        }

        var file = new File(this.location, filename);
        Assertx.mustTrue(file.createNewFile(), IOException::new, "无法访问指定文件: " + file.getAbsolutePath());

        // 保存数据流到指定文件
        IOStreamx.copy(body.getInputStream(), Files.newOutputStream(file.toPath(), StandardOpenOption.WRITE));
        return file;
    }
}
