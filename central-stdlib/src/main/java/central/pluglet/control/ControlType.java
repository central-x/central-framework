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

package central.pluglet.control;

/**
 * 控件类型
 *
 * @author Alan Yeh
 * @since 2022/07/12
 */
public enum ControlType {
    /**
     * 文本控件
     */
    LABEl,
    /**
     * 文本输入框
     */
    TEXT,
    /**
     * 密码输入框
     */
    PASSWORD,
    /**
     * 下拉列表
     * 属性类型必须使用 {@code ? extend Enum} 且继承于 OptionalEnum
     */
    RADIO,
    /**
     * 下拉列表
     * 属性类型必须使用 {@code List<? extend Enum>} 且继承于 OptionalEnum
     */
    CHECKBOX,
    /**
     * 整数类型
     */
    NUMBER,
    /**
     * 时间类型（年月日）
     */
    DATE,
    /**
     * 时间类型（年月日时分秒）
     */
    DATETIME,
    /**
     * 时间类型（时分秒）
     */
    TIME
}
