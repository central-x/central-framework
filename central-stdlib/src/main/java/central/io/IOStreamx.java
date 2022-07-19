package central.io;

import central.util.Assertx;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.nio.charset.Charset;

/**
 * 流工具
 *
 * @author Alan Yeh
 * @since 2022/07/05
 */
public class IOStreamx {
    public static final int BUFFER_SIZE = 4096;

    /**
     * 将输入流复制到输出流
     * 方法完毕后将关闭输入输出流
     *
     * @param input  输入流(执行后将被关闭)
     * @param output 输出流(执行后将被关闭)
     * @return 共复制了多少字节
     */
    public static long copy(@Nonnull InputStream input, @Nonnull OutputStream output) throws IOException {
        Assertx.mustNotNull(input, "Argument 'input' must not null");
        Assertx.mustNotNull(output, "Argument 'output' must not null");

        if (input instanceof FileInputStream inputStream && output instanceof FileOutputStream outputStream) {
            try (var inputChannel = inputStream.getChannel(); var outputChannel = outputStream.getChannel()) {
                inputChannel.transferTo(0, inputChannel.size(), outputChannel);
                return inputChannel.size();
            }
        } else {
            try (var bufferedInput = buffered(input); var bufferedOutput = buffered(output)) {

                var byteCount = 0;

                var buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = bufferedInput.read(buffer)) != -1) {
                    bufferedOutput.write(buffer, 0, bytesRead);
                    byteCount += bytesRead;
                }

                bufferedOutput.flush();
                return byteCount;
            }
        }
    }

    /**
     * 将输入流的内容传输入输出流
     * 方法执行完毕后不关闭输出流，但是会关闭输入流
     *
     * @param input  输入流(执行后将被关闭)
     * @param output 输出流(执行后不关闭)
     * @return 共复制了多少字节
     */
    public static long transfer(@Nonnull InputStream input, @Nonnull OutputStream output) throws IOException {
        Assertx.mustNotNull(input, "Argument 'input' must not null");
        Assertx.mustNotNull(output, "Argument 'output' must not null");

        if (input instanceof FileInputStream && output instanceof FileOutputStream) {
            try (var inputStream = (FileInputStream) input) {
                var outputStream = (FileOutputStream) output;
                var inputChannel = inputStream.getChannel();
                var outputChannel = outputStream.getChannel();
                inputChannel.transferTo(0, inputChannel.size(), outputChannel);
                return inputChannel.size();
            }
        } else {
            try (var bufferedInput = buffered(input)) {
                var bufferedOutput = buffered(output);
                var transferred = 0;

                var buffer = new byte[BUFFER_SIZE];
                int bytesRead;
                while ((bytesRead = bufferedInput.read(buffer, 0, BUFFER_SIZE)) >= 0) {
                    bufferedOutput.write(buffer, 0, bytesRead);
                    transferred += bytesRead;
                }

                bufferedOutput.flush();
                return transferred;
            }
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

        var output = new ByteArrayOutputStream(BUFFER_SIZE);
        copy(input, output);
        return output.toByteArray();
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
            return new BufferedInputStream(input);
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
            return new BufferedOutputStream(output);
        }
    }
}
