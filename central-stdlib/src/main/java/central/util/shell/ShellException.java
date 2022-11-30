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

package central.util.shell;

import lombok.Getter;

import java.io.IOException;
import java.io.Serial;

/**
 * Shell 异常
 *
 * @author Alan Yeh
 * @since 2022/11/25
 */
public class ShellException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 366159005708119523L;

    @Getter
    private final int res;

    /**
     * 创建 Shell 异常
     *
     * @param res     返回码
     * @param message 错误信息
     */
    public ShellException(int res, String message) {
        this(res, message, null);
    }

    /**
     * 创建 Shell 异常
     *
     * @param res     返回码
     * @param message 错误信息
     * @param cause   原因
     */
    public ShellException(int res, String message, Throwable cause) {
        super(message, cause);
        this.res = res;
    }

    /**
     * 创建 Shell 异常
     *
     * @param message 错误信息
     */
    public ShellException(String message) {
        this(-1, message, null);
    }

    /**
     * 创建 Shell 异常
     *
     * @param message 错误信息
     * @param cause   原因
     */
    public ShellException(String message, Throwable cause) {
        this(-1, message, cause);
    }
}
