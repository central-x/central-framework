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

package central.lang;

import central.util.Guidx;

import java.util.HashMap;
import java.util.Map;

/**
 * 用于跟踪上下文
 *
 * @author Alan Yeh
 * @since 2022/10/25
 */
public class TraceLocal {
    private final static String TRACE_ID = "traceId";

    private final static ThreadLocal<Map<String, String>> traceLocal = ThreadLocal.withInitial(HashMap::new);

    /**
     * 开始追踪
     * <p>
     * 此方法用于在不同的微服务里追踪同一链路
     */
    public static String trace(String traceId) {
        if (Stringx.isNullOrBlank(traceId)) {
            traceId = Guidx.nextID();
        }
        setTraceId(traceId);
        return traceId;
    }

    /**
     * 开始追踪
     */
    public static String trace() {
        var traceId = Guidx.nextID();
        setTraceId(traceId);
        return traceId;
    }

    /**
     * 获取追踪标识
     */
    public static String getTraceId() {
        return traceLocal.get().computeIfAbsent(TRACE_ID, key -> Guidx.nextID());
    }

    /**
     * 设置上下文标识
     */
    public static void setTraceId(String traceId) {
        traceLocal.get().put(TRACE_ID, traceId);
    }

    /**
     * 结束追踪
     */
    public static void end() {
        traceLocal.remove();
    }
}
