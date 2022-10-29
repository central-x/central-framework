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

import central.io.IOStreamx;
import central.lang.Stringx;
import central.util.Jsonx;
import central.util.concurrent.BlockedQueue;
import central.util.concurrent.ConsumableQueue;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.zip.GZIPOutputStream;

/**
 * Central Log Appender
 *
 * @author Alan Yeh
 * @since 2022/10/24
 */
public abstract class CentralAppender extends AppenderBase<ILoggingEvent> {
    /**
     * 应用编码
     */
    public abstract String getApplicationCode();

    /**
     * 最大批量发送大小
     */
    public abstract int getBatchSize();

    /**
     * 最大批量发送时间
     */
    public abstract int getBatchTime();

    /**
     * 缓存目录
     */
    public abstract String getTmpPath();

    private ConsumableQueue<LogContext, BlockedQueue<LogContext>> queue;

    @Override
    public void start() {
        super.start();

        this.queue = new ConsumableQueue<>(new BlockedQueue<>(new LinkedBlockingQueue<>()), "central.logging.appender.writing." + this.getApplicationCode());
        this.queue.addConsumer(new Writer(new File(this.getTmpPath(), this.getApplicationCode()), this.getBatchSize(), this.getBatchTime()));
    }

    @Override
    @SneakyThrows
    public void stop() {
        super.stop();

        if (this.queue != null) {
            this.queue.close();
            this.queue = null;
        }
    }

    @Override
    protected void append(ILoggingEvent event) {
        this.queue.offer(new LogContext(event));
    }

    @RequiredArgsConstructor
    private static class Writer implements Consumer<BlockedQueue<LogContext>> {
        /**
         * 日志目录
         */
        private final File dir;
        /**
         * 最大批量发送大小
         */
        private final int batchSize;
        /**
         * 最大批量发送时间
         */
        private final int batchTime;
        /**
         * 随机数，防止文件名冲突
         */
        private final Random random = new Random(System.currentTimeMillis());

        @Override
        public void accept(BlockedQueue<LogContext> queue) {
            try {
                while (true) {
                    var logs = queue.poll(this.batchSize, this.batchTime, TimeUnit.MILLISECONDS);

                    if (logs.isEmpty()) {
                        continue;
                    }

                    try {
                        if (!this.dir.exists()) {
                            if (!this.dir.mkdirs()) {
                                throw new IOException("无法创建指定目录: " + this.dir.getAbsolutePath());
                            }
                        }

                        var tmp = new File(this.dir, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss_SSS")) + "_" + Stringx.paddingLeft(String.valueOf(random.nextInt(100)), 3, '0') + ".logtmp");
                        if (tmp.createNewFile()) {
                            var output = new ByteArrayOutputStream();
                            try (var stream = new GZIPOutputStream(output)) {
                                stream.write(Jsonx.Default().serialize(logs.stream().map(LogContext::getData).toList()).getBytes(StandardCharsets.UTF_8));
                                stream.flush();
                            }

                            var stream = new ByteArrayInputStream(output.toByteArray());
                            IOStreamx.copy(stream, Files.newOutputStream(tmp.toPath(), StandardOpenOption.WRITE));
                        }
                    } catch (IOException ex) {
                        // 没办法写入文件，丢弃
                        System.err.println("日志收集异常: " + ex.getLocalizedMessage());
                    }
                }
            } catch (InterruptedException ex) {
                System.err.println(Stringx.format("日志收集中断: " + ex.getLocalizedMessage()));
                Thread.currentThread().interrupt();
            }
        }
    }
}
