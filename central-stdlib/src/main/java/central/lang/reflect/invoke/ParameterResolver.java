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

package central.lang.reflect.invoke;

import central.bean.Orderable;
import central.util.Context;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * 参数解析器
 *
 * @author Alan Yeh
 * @since 2022/10/01
 */
public interface ParameterResolver extends Orderable<ParameterResolver> {

    /**
     * 参数解析器的顺序
     * 数值越大，排序越靠后
     */
    @Override
    default Integer getOrder() {
        return 0;
    }

    /**
     * 判断是否支持解析该参数
     *
     * @param clazz     方法所在的类
     * @param method    方法
     * @param parameter 参数
     * @return 如果返回 true，则使用此参数解析器解析参数
     */
    boolean support(@Nonnull Class<?> clazz, @Nonnull Method method, @Nonnull Parameter parameter);

    /**
     * 解析参数
     *
     * @param clazz     方法所在的类
     * @param method    方法
     * @param parameter 参数
     * @param context   上下文
     * @return 解析后的参数
     */
    @Nullable
    Object resolve(@Nonnull Class<?> clazz, @Nonnull Method method, @Nonnull Parameter parameter, @Nonnull Context context);
}
