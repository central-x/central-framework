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

import java.util.List;

/**
 * Sql 执行上下文
 *
 * @author Alan Yeh
 * @since 2022/09/15
 */
public interface SqlExecuteContext {
    /**
     * Sql 执行器
     */
    SqlExecutor getExecutor();

    /**
     * 待执行 Sql
     */
    String getSql();

    /**
     * Sql 参数
     */
    List<List<Object>> getArgs();

    /**
     * 执行结果
     */
    Object getResult();

    /**
     * 保存执行过程信息
     *
     * @param key   键
     * @param value 值
     * @param <T>   值类型
     */
    <T> void put(String key, T value);

    /**
     * 获取执行过程信息
     *
     * @param key 键
     * @param <T> 值类型
     * @return 值
     */
    <T> T get(String key);
}
