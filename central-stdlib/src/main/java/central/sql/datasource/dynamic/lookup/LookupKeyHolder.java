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

package central.sql.datasource.dynamic.lookup;

import central.lang.Stringx;
import central.util.Context;

/**
 * 持有数据源 Key
 *
 * @author Alan Yeh
 * @since 2022/08/11
 */
public class LookupKeyHolder {
    /**
     * 强制当前线程以此 LookupKey 为准
     */
    private static final ThreadLocal<String> lookupKey = new ThreadLocal<>();

    private static final ThreadLocal<Context> context = ThreadLocal.withInitial(Context::new);

    public static String getLookupKey() {
        return lookupKey.get();
    }

    public static void setLookupKey(String key) {
        if (Stringx.isNullOrBlank(key)) {
            lookupKey.remove();
        } else {
            lookupKey.set(key);
        }
    }

    public static Context getContext() {
        return context.get();
    }

    public static void clear() {
        lookupKey.remove();
        context.remove();
    }
}
