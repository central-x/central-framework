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

package central.sql;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * 数据转换器
 * 从数据库中取出来的数据不一定与实体的数据类型相符，因此需要转换后再保存到实体里
 * 在
 *
 * @author Alan Yeh
 * @since 2022/09/14
 */
public interface SqlConverter {
    /**
     * 是否支持将指定的源类型转换成目标类型
     *
     * @param source 源类型
     * @param target 目标类型
     */
    boolean support(@Nonnull Class<?> source, @Nonnull Class<?> target);

    /**
     * 将源对象转换成目标类型对象
     *
     * @param source 源对象
     * @param target 目标类型
     * @return 目标类型对象
     */
    <T> T convert(@Nullable Object source, @Nonnull Class<T> target);
}
