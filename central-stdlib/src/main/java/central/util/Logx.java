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

package central.util;

import central.bean.OptionalEnum;
import central.lang.Stringx;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.UtilityClass;

/**
 * 日志工具
 *
 * @author Alan Yeh
 * @since 2024/01/02
 */
@UtilityClass
public class Logx {
    @Getter
    @AllArgsConstructor
    public enum Color implements OptionalEnum<String> {
        BLACK("黑", "0"),
        RED("红", "1"),
        GREEN("绿", "2"),
        YELLOW("黄", "3"),
        BLUE("蓝", "4"),
        PURPLE("紫", "5"),
        CYAN("青", "6"),
        WHITE("白", "7");

        private final String name;
        private final String value;
    }

    /**
     * 设置日志的文字颜色
     *
     * @param text       日志
     * @param foreground 文字颜色
     */
    public String wrap(String text, Color foreground) {
        return Stringx.format("\u001B[3{}m{}\u001B[0m", foreground.getValue(), text);
    }

    /**
     * 设置日志的文字颜色和背景颜色
     *
     * @param text       日志
     * @param foreground 文字颜色
     * @param background 背景颜色
     */
    public String wrap(String text, Color foreground, Color background) {
        return Stringx.format("\u001B[4{};3{}m{}\u001B[0m", background.getValue(), foreground.getValue(), text);
    }
}
