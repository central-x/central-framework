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

package central.util.cache;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.time.Duration;
import java.util.List;

/**
 * 缓存队列
 *
 * @author Alan Yeh
 * @since 2023/07/01
 */
public interface CacheQueue {
    /**
     * 获取队列中所有的元素
     *
     * @return 元素集合
     */
    @Nonnull List<String> values();

    /**
     * 在队列的最后追加一个元素
     *
     * @param value 待添加的元素
     * @return 是否添加成功
     */
    boolean push(@Nonnull String value);

    /**
     * 获取并移除队列最前面的元素
     *
     * @return 被移除的元素。如果队列中没有元素，则返回空
     */
    @Nullable String pop();

    /**
     * 获取并移除队列最前面的几个元素
     *
     * @param count 移除的元素数量
     * @return 被移除的元素
     */
    @Nonnull List<String> pop(long count);

    /**
     * 获取并移除队列最前面的元素
     *
     * @param timeout 等待时长
     * @return 被移除的元素。超时后队列中如果没有元素，则返回空
     * @throws InterruptedException 等待时被中断
     */
    @Nullable String take(@Nonnull Duration timeout) throws InterruptedException;

    /**
     * 获取队列最前面的元素，不会移除该元素
     *
     * @return 队列最前面的元素。如果队列中没有元素，则返回空
     */
    @Nullable String peek();

    /**
     * 获取队列最前面的几个元素元素，不会移除该元素
     *
     * @param count 获取元素数量
     * @return 元素集合
     */
    @Nonnull List<String> peek(long count);
}
