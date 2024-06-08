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

package central.lang;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.slf4j.helpers.MessageFormatter;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Locale;

/**
 * String 工具包
 *
 * @author Alan Yeh
 * @since 2022/07/05
 */
@UtilityClass
public class Stringx {
    private static final char[] DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * 字节码转 HEX
     *
     * @param data 字节码
     * @return HEX String
     */
    public static String encodeHex(byte[] data) {
        final var l = data.length;
        final var out = new char[l << 1];
        // two characters form the hex value.
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = DIGITS[(0xF0 & data[i]) >>> 4];
            out[j++] = DIGITS[0x0F & data[i]];
        }
        return new String(out);
    }

    /**
     * 将 HEX 字符串还原成字节码
     *
     * @param hex HEX String
     * @return 字节码
     * @throws ParseException 转换异常
     */
    public static byte[] decodeHex(String hex) throws ParseException {
        var data = hex.toCharArray();
        final var len = data.length;

        if ((len & 0x01) != 0) {
            throw new ParseException("Hex 字符串必须是偶数", 0);
        }

        final var out = new byte[len >> 1];

        // two characters form the hex value.
        for (int i = 0, j = 0; j < len; i++) {
            int f = toDigit(data[j], j) << 4;
            j++;
            f = f | toDigit(data[j], j);
            j++;
            out[i] = (byte) (f & 0xFF);
        }

        return out;
    }

    /**
     * URL Encode
     */
    @SneakyThrows
    public static String encodeUrl(String text) {
        return URLEncoder.encode(text, StandardCharsets.UTF_8);
    }

    /**
     * URL Encode
     */
    @SneakyThrows
    public static String encodeUrl(String text, Charset charset) {
        return URLEncoder.encode(text, charset);
    }

    /**
     * URL Decode
     */
    @SneakyThrows
    public static String decodeUrl(String text) {
        return URLDecoder.decode(text, StandardCharsets.UTF_8);
    }

    /**
     * URL Decode
     */
    @SneakyThrows
    public static String decodeUrl(String text, Charset charset) {
        return URLDecoder.decode(text, charset);
    }

    private static int toDigit(final char ch, final int index) throws ParseException {
        final var digit = Character.digit(ch, 16);
        if (digit == -1) {
            throw new ParseException("Illegal hexadecimal character " + ch, index);
        }
        return digit;
    }

    /**
     * 生成固定长度的字符串
     * <p>
     * 如果长度过长，则截取；如果长度过短，则在后面填充指定字符
     *
     * @param text   字符串
     * @param length 目标长度
     * @param pad    填充字符
     */
    public static String paddingRight(@Nonnull String text, int length, char pad) {
        if (text.length() > length) {
            return text.substring(0, length);
        } else {
            var padding = new char[length - text.length()];
            Arrays.fill(padding, pad);
            return text + new String(padding);
        }
    }

    /**
     * 生成固定长度的字符串
     * <p>
     * 如果长度过长，则截取；如果长度过短，则在前面填充指定字符
     *
     * @param text   字符串
     * @param length 目标长度
     * @param pad    填充字符
     */
    public static String paddingLeft(@Nonnull String text, int length, char pad) {
        if (text.length() > length) {
            return text.substring(0, length);
        } else {
            var padding = new char[length - text.length()];
            Arrays.fill(padding, pad);
            return new String(padding) + text;
        }
    }

    /**
     * 生成固定长度的字符串
     * <p>
     * 如果长度过长，则截取；如果长度过短，则两端填充指定字符
     *
     * @param text   字符串
     * @param length 目标长度
     * @param pad    填充字符
     */
    public static String paddingBoth(@Nonnull String text, int length, char pad) {
        if (text.length() > length) {
            return text.substring(0, length);
        } else {
            var leftPadding = new char[(length - text.length()) / 2];
            Arrays.fill(leftPadding, pad);

            var rightPadding = new char[length - text.length() - leftPadding.length];
            Arrays.fill(rightPadding, pad);

            return new String(leftPadding) + text + new String(rightPadding);
        }
    }

    /**
     * 判断文本是否为空或空字符串
     */
    public static boolean isNullOrEmpty(@Nullable String text) {
        if (text == null) {
            return true;
        }
        return text.isEmpty();
    }

    /**
     * 判断文本列表是否全部为空或空字符串
     */
    public static boolean isAllNullOrEmpty(@Nullable String first, String... others) {
        if (first != null && !first.isEmpty()) {
            return false;
        }
        for (var str : others) {
            if (str != null && !str.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断文本是否不为空
     */
    public static boolean isNotEmpty(@Nullable String text) {
        if (text == null) {
            return false;
        }
        return !text.isEmpty();
    }

    /**
     * 判断文本是否为空，或 trim 后是否为空
     */
    public static boolean isNullOrBlank(@Nullable String text) {
        if (text == null) {
            return true;
        }
        return text.trim().isEmpty();
    }

    /**
     * 判断文本是否不为空白字符串
     */
    public static boolean isNotBlank(@Nullable String text) {
        if (text == null) {
            return false;
        }
        return !text.trim().isEmpty();
    }

    /**
     * 移除前缀
     */
    public static String removePrefix(String text, @Nonnull String prefix) {
        if (isNullOrEmpty(text)) {
            return text;
        }

        if (text.startsWith(prefix)) {
            return text.substring(prefix.length());
        }
        return text;
    }

    /**
     * 添加前缀
     * <p>
     * 如果字符串已经存在前缀，则不会再添加
     */
    public static String addPrefix(String text, @Nonnull String prefix) {
        if (isNullOrEmpty(text)) {
            return text;
        }

        if (text.startsWith(prefix)) {
            return text;
        }

        return prefix + text;
    }

    /**
     * 添加后缀
     * <p>
     * 如果字符串已经存在后缀，则不会再添加
     */
    public static String addSuffix(String text, @Nonnull String suffix) {
        if (isNullOrEmpty(text)) {
            return text;
        }

        if (text.endsWith(suffix)) {
            return text;
        }

        return text + suffix;
    }

    /**
     * 移除后缀
     */
    public static String removeSuffix(String text, @Nonnull String suffix) {
        if (isNullOrEmpty(text)) {
            return text;
        }

        if (!text.endsWith(suffix)) {
            return text;
        }

        return text.substring(0, text.length() - suffix.length());
    }


    /**
     * 截取指定字符串之前的字符
     */
    public static String substringBefore(@Nonnull String text, @Nonnull String delimiter) {
        return text.substring(0, text.indexOf(delimiter));
    }

    /**
     * 截取指定字符串之后的字符
     */
    public static String substringAfter(@Nonnull String text, @Nonnull String delimiter) {
        return text.substring(text.indexOf(delimiter));
    }

    public static String emptyToNull(String string) {
        return isNullOrEmpty(string) ? null : string;
    }

//    /**
//     * 格式化字符串，使用 {name} 作为占位符，从 args 取值替换
//     * <p>
//     * 例：
//     * <pre>{@code
//     * // Hello Alan
//     * var content = Stringx.format("Hello {name}", Map.of("name", "Alan", "user", "alan"));
//     * }</pre>
//     *
//     * 如果未找到可以替换的，将保留原来的内容，例：
//     * <pre>{@code
//     * // Hello {name}
//     * var content = Stringx.format("Hello {name}", Map.of("user", "Alan"));
//     * }</pre>
//     *
//     * @param format 待格式化字符串
//     * @param args   占位值
//     * @return 已格式化字符串
//     */
//    public static @Nonnull String format(@Nonnull String format, Map<String, Object> args) {
//
//    }

//    /**
//     * 格式化字符串，使用 {num} 作为占位符
//     *
//     * 例：
//     * <pre>{@code
//     * // Hello Alan, how are you
//     * var content = Stringx.format("Hello {0}, how are {1}", "Alan", "you")
//     * }</pre>
//     *
//     * 如果指定下标没有参数，则保留原参数，例：
//     * <pre>{@code
//     * // Hello {3}
//     * var content = Stringx.format("Hello {3}, how are you {1}", "Alan", "you")
//     * }</pre>
//     */
//    public static @Nonnull String format(@Nonnull String format, Object... args) {
//
//    }

    /**
     * 格式化字符串，使用 {} 作为占位符
     * <p>
     * 例:
     * <pre>{@code
     * // hello world
     * var content = Stringx.format("{} {}", "hello", "world");}
     * </pre>
     */
    public static @Nonnull String format(@Nonnull String format, Object... args) {
        return MessageFormatter.arrayFormat(format, args).getMessage();
//        if (args == null || args.length < 1) {
//            return format;
//        }
//
//        var result = new StringBuilder(format.length() + 50);
//
//        int i = 0;
//        for (int l = 0; l < args.length; l++) {
//            var j = format.indexOf("{}", i);
//            if (j == -1) {
//                // 没有找到
//                if (i == 0) {
//                    // format 是一个普通的字符串
//                    return format;
//                } else {
//                    result.append(format, i, format.length());
//                    i = format.length();
//                }
//            } else {
//                result.append(format, i, j);
//                result.append(args[l] == null ? "" : args[l]);
//                i = j + 2;
//            }
//        }
//
//        if (i < format.length()) {
//            result.append(format, i, format.length());
//        }
//
//        return result.toString();
    }

    /**
     * 将集合合并成字符串
     *
     * @param array     集合
     * @param separator 元素分隔符
     */
    public static @Nonnull String join(Object[] array, String separator) {
        if (array != null && array.length > 0) {
            var builder = new StringBuilder();
            for (int i = 0, length = array.length; i < length; i++) {
                if (i != 0) {
                    builder.append(separator);
                }
                builder.append(array[i]);
            }
            return builder.toString();
        } else {
            return "";
        }
    }

    /**
     * 首字母小写
     */
    public static String lowerCaseFirstLetter(String text) {
        if (isNullOrEmpty(text)) {
            return text;
        }

        return text.substring(0, 1).toLowerCase(Locale.ENGLISH) + text.substring(1);
    }

    /**
     * 首字母大写
     */
    public static String upperCaseFirstLetter(String text) {
        if (isNullOrEmpty(text)) {
            return text;
        }

        return text.substring(0, 1).toUpperCase(Locale.ENGLISH) + text.substring(1);
    }
}
