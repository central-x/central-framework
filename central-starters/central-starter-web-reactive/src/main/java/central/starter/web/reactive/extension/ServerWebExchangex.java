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

package central.starter.web.reactive.extension;

import central.lang.Assertx;
import central.lang.Attribute;
import lombok.experimental.UtilityClass;
import org.springframework.web.server.ServerWebExchange;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * ServerWebExchange Extension Methods
 *
 * @author Alan Yeh
 * @since 2022/10/18
 */
@UtilityClass
public class ServerWebExchangex {
    /**
     * 获取交换属性
     *
     * @param exchange  Current Exchange
     * @param attribute Attribute
     * @param <T>       Attribute type
     * @return Attribute value
     */
    public static <T> @Nullable T getAttribute(ServerWebExchange exchange, Attribute<T> attribute) {
        return (T) exchange.getAttributes().computeIfAbsent(attribute.getCode(), code -> attribute.getValue());
    }

    /**
     * 获取交换属性
     *
     * @param exchange     Current Exchange
     * @param attribute    Attribute
     * @param defaultValue Default attribute value
     * @param <T>          Attribute Type
     * @return Attribute Value
     */
    public static <T> @Nonnull T getAttributeOrDefault(ServerWebExchange exchange, Attribute<T> attribute, T defaultValue) {
        return (T) exchange.getAttributeOrDefault(attribute.getCode(), defaultValue);
    }

    /**
     * 获取非空交换属性
     *
     * @param exchange  Current Exchange
     * @param attribute Attribute
     * @param <T>       Attribute type
     * @return Attribute value
     */
    public static <T> @Nonnull T getRequiredAttribute(ServerWebExchange exchange, Attribute<T> attribute) {
        return Assertx.requireNotNull(getAttribute(exchange, attribute), "Require nonnull value for key '{}'", attribute.getCode());
    }

    /**
     * 保存交换属性
     *
     * @param exchange     Current Exchange
     * @param attribute    Attribute
     * @param value        Attribute value
     * @param <T>Attribute type
     */
    public static <T> void setAttribute(ServerWebExchange exchange, Attribute<T> attribute, T value) {
        if (value == null) {
            exchange.getAttributes().remove(attribute.getCode());
        } else {
            exchange.getAttributes().put(attribute.getCode(), value);
        }
    }
}
