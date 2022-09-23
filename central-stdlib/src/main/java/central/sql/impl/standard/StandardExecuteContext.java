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

package central.sql.impl.standard;

import central.sql.SqlExecuteContext;
import central.sql.SqlExecutor;
import central.util.Listx;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

/**
 * 标准的执行上下文
 *
 * @author Alan Yeh
 * @since 2022/09/15
 */
public class StandardExecuteContext implements SqlExecuteContext {

    @Getter
    private final SqlExecutor executor;

    @Getter
    private final String sql;

    @Getter
    private final List<List<Object>> args;

    @Getter
    @Setter
    private Object result;

    public StandardExecuteContext(SqlExecutor executor, String sql, List<Object> args) {
        this.executor = executor;
        this.sql = sql;
        if (Listx.isNullOrEmpty(args)) {
            this.args = Collections.emptyList();
        } else if (Listx.getFirst(args) instanceof List<?>) {
            this.args = args.stream().map(it -> Collections.unmodifiableList((List<Object>) it)).toList();
        } else {
            this.args = List.of(Collections.unmodifiableList(args));
        }
    }

    private final Map<String, Object> ctx = new HashMap<>();

    @Override
    public <T> void put(String key, T value) {
        this.ctx.put(key, value);
    }

    @Override
    public <T> T get(String key) {
        return (T) this.ctx.get(key);
    }
}
