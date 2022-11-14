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

package central.starter.cache.core.annotation;

/**
 * 缓存
 *
 * @author Alan Yeh
 * @since 2022/11/14
 */
public @interface Cacheable {
    /**
     * 缓存键
     * <p>
     * 支持模板语法
     * <p>
     * 可以使用的上下文对象包括:
     *
     * <ul>
     *     <li>args: Object[] 参数列表</li>
     *     <li>method: Method 方法</li>
     *     <li>target: Object 待执行方法的对象</li>
     * </ul>
     */
    String key();

    /**
     * 缓存有效期（毫秒）
     */
    long expires() default 30 * 60 * 1000L;

    /**
     * 缓存依赖
     * <p>
     * 当保存此缓存时，会依赖指定的缓存键。如果指定的缓存键被手动清除时，本缓存也会跟随清除。
     */
    String[] dependencies() default {};

    /**
     * 当满足条件时才保存缓存。支持通过模板语法，返回 true 时保存缓存
     */
    String condition() default "";
}
