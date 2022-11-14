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

import java.lang.annotation.*;

/**
 * 删除缓存
 *
 * @author Alan Yeh
 * @since 2022/11/14
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(CacheEvict.List.class)
public @interface CacheEvict {
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
    String key() default "";

    /**
     * 缓存键
     */
    CacheKey[] keys() default {};

    /**
     * 是否在执行方法前清除缓存
     * <p>
     * 默认在执行方法之后清除缓存。如果方法在执行过程中抛出了异常，那么将不会执行清除缓存操作。
     */
    boolean beforeInvocation() default false;

    @Documented
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {
        CacheEvict[] value();
    }
}
