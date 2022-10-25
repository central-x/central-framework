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

package central.starter.logging.logback.appender;

import central.lang.Arrayx;
import central.lang.Stringx;
import central.lang.TraceLocal;
import central.util.Localx;
import ch.qos.logback.classic.spi.ILoggingEvent;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 日志上下文
 *
 * @author Alan Yeh
 * @since 2022/10/24
 */
public class LogContext implements Comparable<LogContext> {

    @Getter
    private final Map<String, Object> data;

    @Getter
    private final ILoggingEvent event;

    /**
     * 日志类型
     */
    public String getType() {
        return this.event.getMDCPropertyMap().getOrDefault("type", "debug");
    }

    /**
     * 租户标识
     */
    public String getTenant() {
        return this.event.getMDCPropertyMap().getOrDefault("tenant", "master");
    }

    /**
     * 跟踪标识
     */
    public String getTraceId() {
        return this.event.getMDCPropertyMap().getOrDefault("traceId", TraceLocal.getTraceId());
    }

    /**
     * 日志来源
     * <p>
     * {@code className#method(lineNumber)} 或 {@code method url}
     */
    public String getSource() {
        return this.event.getMDCPropertyMap().get("source");
    }

    /**
     * 执行时间
     */
    public long getDuration() {
        var time = this.event.getMDCPropertyMap().getOrDefault("duration", "0");
        return Long.parseLong(time);
    }

    /**
     * 应用主键
     */
    public String getApplicationId() {
        return this.event.getMDCPropertyMap().getOrDefault("applicationId", "");
    }

    /**
     * 应用标识
     */
    public String getApplicationCode() {
        return this.event.getMDCPropertyMap().getOrDefault("applicationCode", this.event.getLoggerContextVO().getPropertyMap().get("APPLICATION_CODE"));
    }

    /**
     * 获取日志调用位置
     */
    public String getLocation() {
        var location = this.event.getMDCPropertyMap().get("location");
        if (Stringx.isNullOrBlank(location)) {
            var caller = Arrayx.getFirst(this.event.getCallerData());
            if (caller.isPresent()) {
                location = caller.get().getClassName() + "#" + caller.get().getMethodName() + "(" + caller.get().getLineNumber() + ")";
            }
        }
        return location;
    }

    /**
     * 日志发生时间
     */
    public long getTimestamp() {
        return this.event.getTimeStamp();
    }

    public LogContext(ILoggingEvent event) {
        this.event = event;
        var data = new HashMap<String, Object>();
        // 基础属性
        data.put("type", this.getType());
        data.put("traceId", this.getTraceId());
        data.put("level", this.event.getLevel().levelStr);
        data.put("content", this.event.getFormattedMessage());
        data.put("applicationId", this.getApplicationId());
        data.put("applicationCode", this.getApplicationCode());
        data.put("tenant", this.getTenant());

        // 服务端属性
        data.put("serverHost", Localx.getLocalHost());
        data.put("serverPort", this.event.getLoggerContextVO().getPropertyMap().get("SERVER_PORT"));
        data.put("service", this.event.getLoggerContextVO().getPropertyMap().get("SERVICE_NAME"));
        data.put("version", this.event.getLoggerContextVO().getPropertyMap().get("SERVICE_VERSION"));
        data.put("thread", this.event.getThreadName());
        data.put("pid", Localx.getPid());
        data.put("logger", this.event.getLoggerName());
        data.put("location", this.getLocation());
        data.put("duration", this.getDuration());

        // 客户端属性
        data.put("referer", this.event.getMDCPropertyMap().get("referer"));
        data.put("userAgent", this.event.getMDCPropertyMap().get("userAgent"));
        data.put("remoteHost", this.event.getMDCPropertyMap().get("remoteHost"));
        data.put("method", this.event.getMDCPropertyMap().get("method"));
        data.put("url", this.event.getMDCPropertyMap().get("url"));

        // 用户行为属性
        data.put("module", this.event.getMDCPropertyMap().get("module"));
        data.put("function", this.event.getMDCPropertyMap().get("function"));
        data.put("accountId", this.event.getMDCPropertyMap().get("accountId"));
        data.put("username", this.event.getMDCPropertyMap().get("username"));

        this.data = Collections.unmodifiableMap(data);
    }

    @Override
    public int compareTo(@NotNull LogContext o) {
        return Long.compare(this.event.getTimeStamp(), o.getEvent().getTimeStamp());
    }
}
