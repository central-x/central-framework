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

package central.starter.webmvc.servlet;

import central.lang.Attribute;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Attributed HttpServletRequest
 * 支持属性操作
 *
 * @author Alan Yeh
 * @since 2023/03/08
 */
public interface AttributedHttpServletRequest extends HttpServletRequest {
    /**
     * 获取属性
     *
     * @param attribute 属性
     * @param <T>       属性类型
     * @return 属性值
     */
    <T> @Nullable T getAttribute(@Nonnull Attribute<T> attribute);

    /**
     * 获取属性
     *
     * @param attribute 属性
     * @param <T>       属性类型
     * @return 属性值
     */
    <T> @Nonnull T getRequiredAttribute(@Nonnull Attribute<T> attribute);

    /**
     * 获取属性
     *
     * @param attribute    属性
     * @param defaultValue 默认值
     * @param <T>          属性类型
     * @return 属性值
     */
    <T> @Nonnull T getAttributeOrDefault(@Nonnull Attribute<T> attribute, @Nonnull T defaultValue);

    /**
     * 保存属性
     *
     * @param attribute 属性
     * @param value     属性值
     * @param <T>       属性类型
     */
    <T> void setAttribute(@Nonnull Attribute<T> attribute, @Nullable T value);
}
