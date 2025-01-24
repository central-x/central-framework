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

import central.lang.Assertx;
import central.lang.Attribute;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Http Attributes
 * <p>
 * 请求属性
 *
 * @author Alan Yeh
 * @since 2025/05/23
 */
public class HttpAttributes {

    private final Map<String, Object> attributes = new ConcurrentHashMap<>();

    /**
     * 获取属性
     *
     * @param attribute 属性
     * @param <T>       属性类型
     * @return 属性值
     */
    @SuppressWarnings("unchecked")
    public <T> @Nullable T getAttribute(@Nonnull Attribute<T> attribute) {
        return (T) attributes.computeIfAbsent(attribute.getCode(), code -> attribute.getValue());
    }

    /**
     * 获取属性
     *
     * @param attribute 属性
     * @param <T>       属性类型
     * @return 属性值
     */
    public <T> @Nonnull T getRequiredAttribute(@Nonnull Attribute<T> attribute) {
        return Assertx.requireNotNull(getAttribute(attribute), "Require nonnull value for key '{}'", attribute.getCode());
    }

    /**
     * 获取属性
     *
     * @param attribute    属性
     * @param defaultValue 默认值
     * @param <T>          属性类型
     * @return 属性值
     */
    @SuppressWarnings("unchecked")
    public <T> @Nonnull T getAttributeOrDefault(@Nonnull Attribute<T> attribute, @Nonnull T defaultValue) {
        return (T) this.attributes.getOrDefault(attribute.getCode(), defaultValue);
    }

    /**
     * 保存属性
     *
     * @param attribute 属性
     * @param value     属性值
     * @param <T>       属性类型
     */
    public <T> void setAttribute(@Nonnull Attribute<T> attribute, @Nullable T value) {
        if (value == null) {
            this.attributes.remove(attribute.getCode());
        } else {
            this.attributes.put(attribute.getCode(), value);
        }
    }
}
