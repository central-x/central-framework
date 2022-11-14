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
 * 缓存键，对指定的对象进行枚举，主要用于生成多条 Key
 * <p>
 * it 用于指定被枚举的对象，该对象可以是 Array、Collection 两种类型
 * <p>
 * key 用于对指定枚举对象进行计算表达式
 * <p>
 * 例:
 * <pre>
 * {@code @CacheEvict(keys = @CacheKey(key = "central:${it}", it = "args[0]"))
 * public long deleteByIds(List<String> ids){
 *     ...
 * }}</pre>
 *
 * @author Alan Yeh
 * @since 2022/11/14
 */
@Documented
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheKey {
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
     *     <li>it: Object 被枚举对象的每条记录</li>
     * </ul>
     */
    String key();

    /**
     * 被枚举的对象
     */
    String it();
}
