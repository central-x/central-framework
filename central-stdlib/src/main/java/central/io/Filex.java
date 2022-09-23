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

package central.io;

import central.lang.PublicApi;
import central.lang.Arrayx;
import central.lang.Assertx;
import lombok.experimental.UtilityClass;

import javax.annotation.Nonnull;
import java.io.*;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 文件工具
 *
 * @author Alan Yeh
 * @since 2022/07/05
 */
@PublicApi
@UtilityClass
public class Filex {
    /**
     * 获取文件扩展名
     *
     * @param file 待处理的文件
     */
    @Nonnull
    public static String getExtension(@Nonnull File file) {
        if (!file.getName().contains(".")) {
            return "";
        } else if (file.getName().endsWith(".")) {
            return "";
        } else {
            return file.getName().substring(file.getName().lastIndexOf(".") + 1);
        }
    }

    /**
     * 读取文件为文本
     *
     * @param file    待读取的文件
     * @param charset 文本编码
     */
    public static String readText(@Nonnull File file, Charset charset) throws IOException {
        Assertx.mustNotNull(file, "Argument 'file' must not null");
        return new String(readBytes(file), charset);
    }

    /**
     * 向文件写入文本
     *
     * @param file    待写入的文件
     * @param content 文本内容
     */
    public static void writeText(@Nonnull File file, String content) throws IOException {
        Assertx.mustNotNull(file, "Argument 'file' must not null");

        var writer = new BufferedWriter(new FileWriter(file));
        writer.write(content);
        writer.flush();
        writer.close();
    }

    /**
     * 向文件写入字节
     *
     * @param file  待写入的文件
     * @param bytes 字节内容
     */
    public static void writeBytes(@Nonnull File file, byte[] bytes) throws IOException {
        Assertx.mustNotNull(file, "Argument 'file' must not null");

        if (file.exists()) {
            Filex.delete(file);
        }
        Assertx.mustTrue(file.createNewFile(), IOException::new, "Cannot create a new file on path '{}'", file.getAbsolutePath());

        try (var output = new FileOutputStream(file)) {
            IOStreamx.transfer(new ByteArrayInputStream(bytes), output);
        }
    }

    /**
     * 向文件追加写入文本
     *
     * @param file    待写入的文件
     * @param content 文本内容
     */
    public static void appendText(@Nonnull File file, String content) throws IOException {
        Assertx.mustNotNull(file, "Argument 'file' must not null");

        RandomAccessFile randomFile = new RandomAccessFile(file, "rw");
        randomFile.seek(randomFile.length());
        randomFile.writeUTF(content);
        randomFile.close();
    }

    /**
     * 向文件追加写入一行文本
     *
     * @param file    待写入的文件
     * @param content 文本内容
     */
    public static void appendLine(@Nonnull File file, String content) throws IOException {
        appendText(file, content + "\n");
    }

    /**
     * 将文件读成字节
     *
     * @param file 待读取的文件
     */
    @Nonnull
    public static byte[] readBytes(@Nonnull File file) throws IOException {
        Assertx.mustNotNull(file, "Argument 'file' must not null");

        try (var input = new FileInputStream(file)) {
            var offset = 0;

            // 防止内存溢出
            Assertx.mustTrue(file.length() < Integer.MAX_VALUE, "File " + file.getPath() + " is too big (" + file.length() + " bytes) to fit in memory");

            var remaining = Long.valueOf(file.length()).intValue();

            var result = new byte[remaining];
            while (remaining > 0) {
                var read = input.read(result, offset, remaining);
                if (read < 0) {
                    break;
                }
                remaining -= read;
                offset += read;
            }

            if (remaining == 0) {
                return result;
            } else {
                return Arrays.copyOf(result, offset);
            }
        }
    }

    /**
     * 删除文件(夹)
     * 如果是文件夹，会递归删除所有的文件夹里的内容
     *
     * @param file 待删除的文件(夹)
     */
    public static void delete(@Nonnull File file) throws IOException {
        if (!file.exists()) {
            // 如果是 Linux 或 Unit 的软链接，file.exists() 判断的是软链接的目标文件是否存在，而不是软件接文件是否存在。
            // 因此，如果软链接的目标文件已删除，那么 file.exists() 的判断可能就会错误，因此就算文件不存在，也要调用 file.delete() 方法。
            // 由于无法知道结果是否真的删除了，所以返回的结果忽略
            var ignored = file.delete();
            return;
        }

        if (file.isDirectory()) {
            var files = Optional.ofNullable(file.listFiles()).orElseGet(() -> new File[0]);
            for (var it : files) {
                Filex.delete(it);
            }
        }

        Assertx.mustTrue(file.delete(), IOException::new, "Can not delete file: " + file.getAbsolutePath());
    }

    /**
     * 将流复制到目标地址
     *
     * @param input  输入流
     * @param target 输出文件
     */
    public static void copy(@Nonnull InputStream input, @Nonnull File target) throws IOException {
        Assertx.mustNotNull(input, "Argument 'file' must not null");
        Assertx.mustNotNull(target, "Argument 'file' must not null");

        if (!target.getParentFile().exists() && !target.getParentFile().mkdirs()) {
            throw new IOException("can't not create directory in " + target.getParentFile().getAbsolutePath());
        }
        try (var inputStream = input; var output = new FileOutputStream(target)) {
            IOStreamx.copy(inputStream, output);
        }
    }

    /**
     * 复制文件(夹)
     * 如果复制文件夹，会逐级复制子文件
     *
     * @param source 源文件
     * @param target 目标文件
     */
    public static void copy(@Nonnull File source, @Nonnull File target) throws IOException {
        Assertx.mustNotNull(source, "Argument 'file' must not null");
        Assertx.mustNotNull(target, "Argument 'file' must not null");

        // 创建立标目录
        if (!target.getParentFile().exists()) {
            if (!target.getParentFile().mkdirs()) {
                throw new IOException("Can not create directory: " + target.getParent());
            }
        }

        if (source.isFile()) {
            // 文件的情况
            if (target.exists() && target.isFile()) {
                // 目标文件已存在
                if (!target.delete()) {
                    throw new IOException("Can not delete file: " + target.getAbsolutePath());
                }
            }
            // 快速复制
            try (var input = new FileInputStream(source); var inputChannel = input.getChannel(); var output = new FileOutputStream(target); var outputChannel = output.getChannel()) {
                inputChannel.transferTo(0, inputChannel.size(), outputChannel);
            }
        } else {
            // 文件夹的情况
            if (target.exists() && target.isDirectory()) {
                // 目标文件夹已存在，则删除目标文件夹
                Filex.delete(target);
                if (!target.mkdirs()) {
                    throw new IOException("Cannot create directory: " + target.getAbsolutePath());
                }
            }

            var files = source.listFiles();
            if (Arrayx.isNotEmpty(files)) {
                for (var file : files) {
                    copy(file, new File(target, source.getName()));
                }
            }
        }
    }

    /**
     * 压缩文件
     *
     * @param sources 源文件，可以是文件夹或文件
     * @param dist    目标文件
     */
    public static void compress(List<File> sources, File dist) throws IOException {
        Filex.delete(dist);

        try (var zip = new ZipOutputStream(new CheckedOutputStream(new FileOutputStream(dist), new CRC32()))) {
            for (var src : sources) {
                compress(zip, src, src.getName());
            }
        }
    }

    private static void compress(ZipOutputStream zip, File src, String path) throws IOException {
        if (src.isDirectory()) {
            // 如果是目录
            var files = src.listFiles();
            if (Arrayx.isNullOrEmpty(files)) {
                // 如果是个空目录，创建一个写入点即可
                zip.putNextEntry(new ZipEntry(path + "/"));
                zip.closeEntry();
            } else {
                // 如果不是空目录，就递归下一层
                for (var file : files) {
                    compress(zip, file, path + "/" + file.getName());
                }
            }
        } else {
            // 创建一个写入点
            var entry = new ZipEntry(path);
            entry.setSize(src.length());
            // Java 只能获取修改时间，不能获取创建时间
            entry.setTime(src.lastModified());

            zip.putNextEntry(entry);

            // 将文件数据写入 zip 里
            IOStreamx.transfer(new FileInputStream(src), zip);
            zip.closeEntry();
        }
    }
}
