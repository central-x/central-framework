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

import central.lang.Arrayx;
import central.lang.Assertx;
import central.lang.PublicApi;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.experimental.UtilityClass;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * 流工具
 *
 * @author Alan Yeh
 * @since 2022/07/05
 */
@PublicApi
@UtilityClass
public class IOStreamx {
    public static final int BUFFER_SIZE = 8 * 1204;

    /**
     * 传输数据流
     *
     * @param input  输入流
     * @param output 输出流
     * @return 共复制了多少字节
     */
    public static long transfer(@Nonnull InputStream input, @Nonnull OutputStream output) throws IOException {
        return transfer(input, output, -1);
    }

    /**
     * 传输数据流
     *
     * @param input  输入流
     * @param output 输出流
     * @param length 传输长度（-1 传输直至关闭）
     */
    public static long transfer(@Nonnull InputStream input, @Nonnull OutputStream output, long length) throws IOException {
        Assertx.mustNotNull(input, "Argument 'input' must not null");
        Assertx.mustNotNull(output, "Argument 'output' must not null");
        if (length == 0) {
            // 不需要传输
            return 0;
        }

        if (input instanceof FileInputStream fileInput && output instanceof FileOutputStream fileOutput) {
            // 如果两个都是文件流的话，使用 Channel 传输会更快
            var inputChannel = fileInput.getChannel();
            var outputChannel = fileOutput.getChannel();

            if (length < 0) {
                // 传输直至完毕
                inputChannel.transferTo(0, inputChannel.size(), outputChannel);
                return inputChannel.size();
            } else {
                // 传输指定长度
                inputChannel.transferTo(0, length, outputChannel);
                return length;
            }
        } else {
            var transferred = 0;

            var buffer = new byte[BUFFER_SIZE];
            int bytesRead;

            if (length < 0) {
                // 传输至完毕
                while ((bytesRead = input.read(buffer, 0, BUFFER_SIZE)) >= 0) {
                    output.write(buffer, 0, bytesRead);
                    transferred += bytesRead;
                }
            } else {
                // 传输指定长度
                while ((bytesRead = input.read(buffer, 0, (int) Math.min(BUFFER_SIZE, length))) >= 0) {
                    output.write(buffer, 0, bytesRead);
                    transferred += bytesRead;

                    length -= bytesRead;
                    if (length <= 0) {
                        // 传输完毕
                        break;
                    }
                }
            }

            output.flush();
            return transferred;
        }
    }

    /**
     * 将输入流读成字节码
     *
     * @param input 输入流
     */
    public static byte[] readBytes(@Nullable InputStream input) throws IOException {
        if (input == null) {
            return new byte[0];
        }

        try (input; var output = new ByteArrayOutputStream(BUFFER_SIZE)) {
            transfer(input, output);
            return output.toByteArray();
        }
    }

    /**
     * 将输入流转为文本
     *
     * @param input   输入流
     * @param charset 字符编码
     * @return 文本，如果 input 为空时，返回空字符串
     */
    @Nonnull
    public static String readText(InputStream input, Charset charset) throws IOException {
        if (input == null) {
            return "";
        }

        var result = new StringBuilder();

        var separator = System.getProperty("line.separator");

        try (var reader = new BufferedReader(new InputStreamReader(input, charset))) {
            String line;
            boolean flag = false;
            while ((line = reader.readLine()) != null) {
                result.append(flag ? separator : "").append(line);
                flag = true;
            }
            return result.toString();
        }
    }

    /**
     * 将 InputStream 转成 BufferedInputStream
     *
     * @param input InputStream
     * @return BufferedInputStream
     */
    public static BufferedInputStream buffered(InputStream input) {
        if (input instanceof BufferedInputStream buffered) {
            return buffered;
        } else {
            return new BufferedInputStream(input, IOStreamx.BUFFER_SIZE);
        }
    }

    /**
     * 将 OutputStream 转成 BufferedOutputStream
     *
     * @param output OutputStream
     * @return BufferedOutputStream
     */
    public static BufferedOutputStream buffered(OutputStream output) {
        if (output instanceof BufferedOutputStream buffered) {
            return buffered;
        } else {
            return new BufferedOutputStream(output, IOStreamx.BUFFER_SIZE);
        }
    }

    /**
     * 读取一行文本
     *
     * @param input   输入流
     * @param charset 字符集
     */
    public static String readLine(InputStream input, Charset charset) throws IOException {
        var arrays = new byte[0];

        var buffer = new byte[IOStreamx.BUFFER_SIZE];

        int index = 0;
        while (true) {
            int length = input.read(buffer, index, 1);

            if (buffer[index] == '\n' || length <= 0) {
                // 如果遇到 \n 表示换一行，如果 length <= 0 表示读完了
                arrays = Arrayx.concat(arrays, Arrays.copyOfRange(buffer, 0, index));
                return new String(arrays, charset);
            }

            index++;

            if (index >= buffer.length) {
                // 缓存已满
                index = 0;
                arrays = Arrayx.concat(arrays, buffer);
            }
        }
    }

    /**
     * 读取一行文本
     *
     * @param input 输入流
     */
    public static String readLine(InputStream input) throws IOException {
        return readLine(input, StandardCharsets.UTF_8);
    }

    /**
     * 向输出流写入一行文本
     *
     * @param output  输入流
     * @param line    文本
     * @param charset 字符集
     */
    public static void writeLine(OutputStream output, String line, Charset charset) throws IOException {
        output.write(line.getBytes(charset));
        output.write('\n');
        output.flush();
    }

    /**
     * 向输出流写入一行文本
     *
     * @param output 输入流
     * @param line   文本
     */
    public static void writeLine(OutputStream output, String line) throws IOException {
        writeLine(output, line, StandardCharsets.UTF_8);
    }

    /**
     * 关闭指定对象
     */
    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException ignored) {
            }
        }
    }
}
