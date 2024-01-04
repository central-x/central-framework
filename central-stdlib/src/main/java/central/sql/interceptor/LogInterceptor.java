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

package central.sql.interceptor;

import central.bean.OptionalEnum;
import central.sql.SqlContext;
import central.sql.SqlInterceptor;
import central.lang.Arrayx;
import central.util.Listx;
import central.util.Logx;
import lombok.experimental.ExtensionMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Sql 日志
 *
 * @author Alan Yeh
 * @since 2022/08/05
 */
@ExtensionMethod(Logx.class)
public class LogInterceptor implements SqlInterceptor {
    private final Logger logger;

    public LogInterceptor() {
        this.logger = LoggerFactory.getLogger(LogInterceptor.class);
    }

    public LogInterceptor(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void before(SqlContext context) {
        context.put(LogInterceptor.class.getName() + ".begin", System.currentTimeMillis());
    }

    @Override
    public void after(SqlContext context) {
        long end = System.currentTimeMillis();
        long begin = context.get(LogInterceptor.class.getName() + ".begin");

        String lineSeparator = System.getProperty("line.separator", "\n");

        var builder = new StringBuilder("┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ ".wrap(Logx.Color.WHITE)).append("Sql".wrap(Logx.Color.PURPLE)).append(" ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━".wrap(Logx.Color.WHITE)).append(lineSeparator);
        builder.append("┣ ".wrap(Logx.Color.WHITE)).append("SQL".wrap(Logx.Color.BLUE)).append("    : ").append(Arrayx.asStream(context.getSql().split("[\n]")).collect(Collectors.joining(lineSeparator + "┃".wrap(Logx.Color.WHITE) + "          "))).append(lineSeparator);
        if (Listx.isNotEmpty(context.getArgs())) {
            builder.append("┣ ".wrap(Logx.Color.WHITE)).append("Params".wrap(Logx.Color.BLUE)).append(" : ").append(this.formatArgs(context.getArgs(), lineSeparator)).append(lineSeparator);
        }
        builder.append("┣ ".wrap(Logx.Color.WHITE)).append("Cost".wrap(Logx.Color.BLUE)).append("   : ").append(end - begin).append("ms").append(lineSeparator);
        builder.append("┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━".wrap(Logx.Color.WHITE));
        logger.info(builder.toString());
    }

    @Override
    public void error(SqlContext context, Throwable throwable) {
        long end = System.currentTimeMillis();
        long begin = context.get(LogInterceptor.class.getName() + ".begin");

        String lineSeparator = System.getProperty("line.separator", "\n");

        var builder = new StringBuilder("┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ ".wrap(Logx.Color.WHITE)).append("Sql".wrap(Logx.Color.PURPLE)).append(" ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━".wrap(Logx.Color.WHITE)).append(lineSeparator);
        builder.append("┣ ".wrap(Logx.Color.WHITE)).append("SQL".wrap(Logx.Color.BLUE)).append("    : ").append(Arrayx.asStream(context.getSql().split("[\n]")).collect(Collectors.joining(lineSeparator + "┃".wrap(Logx.Color.WHITE) + "          "))).append(lineSeparator);
        if (Listx.isNotEmpty(context.getArgs())) {
            builder.append("┣ ".wrap(Logx.Color.WHITE)).append("Params".wrap(Logx.Color.BLUE)).append(" : ").append(this.formatArgs(context.getArgs(), lineSeparator)).append(lineSeparator);
        }
        builder.append("┣ ".wrap(Logx.Color.WHITE)).append("Cost".wrap(Logx.Color.BLUE)).append("   : ").append(end - begin).append("ms").append(lineSeparator);
        builder.append("┣ ".wrap(Logx.Color.WHITE)).append("Error".wrap(Logx.Color.BLUE)).append("  : ").append(context.getResult()).append(lineSeparator);

        StringWriter writer = new StringWriter();
        throwable.printStackTrace(new PrintWriter(writer));
        Arrayx.asStream(writer.toString().split("[\n]"))
                .map(it -> it.replaceFirst("[\t]", lineSeparator + "┃".wrap(Logx.Color.WHITE) + "      "))
                .forEach(builder::append);

        builder.append("┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━".wrap(Logx.Color.WHITE));
        logger.info(builder.toString());
    }

    private String formatArgs(List<List<Object>> args, String lineSeparator) {
        if (args.isEmpty()) {
            return "";
        } else if (args.size() == 1) {
            return this.formatArgs(args.get(0));
        } else {
            var builder = new StringBuilder();
            for (var it : args) {
                if (!builder.isEmpty()){
                    builder.append(lineSeparator).append("┃".wrap(Logx.Color.WHITE)).append("      ");
                }
                builder.append(this.formatArgs(it));
            }
            return builder.toString();
        }
    }

    private static final ThreadLocal<SimpleDateFormat> formatters = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"));

    private String formatArgs(List<Object> args) {
        var builder = new StringBuilder("[");

        for (var arg : args) {
            if (builder.length() > 1) {
                builder.append(", ");
            }

            if (arg == null) {
                builder.append("<null>");
            } else if (arg instanceof Number) {
                builder.append(arg);
            } else if (arg instanceof Timestamp timestamp) {
                builder.append(formatters.get().format(timestamp));
            } else if (arg instanceof OptionalEnum<?> optional) {
                builder.append(optional.getValue());
            } else if (arg instanceof String string) {
                if (string.isEmpty()) {
                    builder.append("<empty>");
                } else if (string.isBlank()) {
                    builder.append("<blank>");
                } else if (string.length() <= 64) {
                    builder.append(string);
                } else {
                    builder.append(string, 0, 10).append("...");
                }
            } else if (arg instanceof byte[]){
                builder.append("<bytes>");
            } else {
                builder.append(arg);
            }
        }

        builder.append("]");
        return builder.toString();
    }
}
