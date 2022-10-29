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

package central.starter.logging.logback.appender.http;

import central.io.Filex;
import central.io.IOStreamx;
import central.lang.Arrayx;
import central.lang.Stringx;
import central.net.http.body.request.FileBody;
import central.net.http.executor.okhttp.OkHttpExecutor;
import central.net.http.processor.impl.AddHeaderProcessor;
import central.net.http.proxy.HttpProxyFactory;
import central.net.http.proxy.contract.spring.SpringContract;
import central.starter.logging.logback.appender.http.client.CollectClient;
import central.util.Guidx;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpHeaders;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 日志发送器
 *
 * @author Alan Yeh
 * @since 2022/10/24
 */
@RequiredArgsConstructor
public class LogSender implements Runnable, InitializingBean {
    /**
     * 日志目录
     */
    private final File dir;
    /**
     * 应用标识
     */
    private final String code;
    /**
     * 应用密钥
     */
    private final String secret;
    /**
     * 日志服务器路径
     */
    private final String server;
    /**
     * 采集器路径
     */
    private final String path;

    private CollectClient client;

    @Override
    public void afterPropertiesSet() throws Exception {
        var builder = HttpProxyFactory.builder(OkHttpExecutor.Default())
                .contact(new SpringContract())
                .processor(new AddHeaderProcessor(HttpHeaders.CONTENT_ENCODING, "gzip"));
        if (Stringx.isNotBlank(code) && Stringx.isNotBlank(secret)) {
            builder.processor((target, chain) -> {
                target.addHeader("X-Forwarded-Token", JWT.create()
                        .withJWTId(Guidx.nextID())
                        .withIssuer(code)
                        .withExpiresAt(new Date(System.currentTimeMillis() + Duration.ofMinutes(3).toMillis()))
                        .sign(Algorithm.HMAC256(secret)));
                return chain.process(target);
            });
        }
        this.client = builder.baseUrl(server).target(CollectClient.class);
    }

    // 上次发送日志的时间
    private long lastSend;

    // 失败次数
    private long failTimes = 0;

    @Override
    public void run() {
        try {
            while (true) {
                // 每隔 1 秒查询是否有新的日志
                Thread.sleep(Duration.ofSeconds(1).toMillis());

                try {
                    if (failTimes >= 10) {
                        throw new RuntimeException("网络请求失败次数过多");
                    }
                    // 记录最后一次发送时间
                    lastSend = System.currentTimeMillis();

                    // 查询指定目录下是否存在需要发送的文件
                    if (this.dir.exists()) {
                        // 有可能多个线程去处理遗留的日志时，可能会导致冲突
                        // 这里使用文件作为锁
                        var lock = new File(this.dir, ".lock");

                        if (lock.exists()) {
                            // 锁已经存在，则检查锁的时间
                            var dateStr = IOStreamx.readText(Files.newInputStream(lock.toPath(), StandardOpenOption.READ), StandardCharsets.UTF_8);
                            if (Stringx.isNullOrBlank(dateStr)) {
                                // 锁里面没有内容
                                Filex.delete(lock);
                            } else {
                                try {
                                    // 如果文件锁存在，检查一下锁的创建时间
                                    var date = OffsetDateTime.parse(dateStr, DateTimeFormatter.ISO_DATE_TIME);
                                    var now = OffsetDateTime.now();

                                    if (date.plusMinutes(10).isBefore(now)) {
                                        // 锁创建时间超过 10 分钟了，删除该锁
                                        Filex.delete(lock);
                                    }
                                    if (date.isAfter(now)) {
                                        // 这个创建时间比当前时间还晚，说明锁无效
                                        Filex.delete(lock);
                                    }
                                } catch (IOException ignored) {
                                    // 解析日志异常
                                    Filex.delete(lock);
                                }
                            }
                        }

                        if (!lock.exists()) {
                            boolean obtained = false;

                            try {
                                obtained = lock.createNewFile();
                                if (obtained) {
                                    Filex.writeText(lock, OffsetDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));

                                    var tmps = Arrayx.asStream(this.dir.listFiles()).filter(it -> it.getName().endsWith(".logtmp")).toList();

                                    for (var tmp : tmps) {
                                        if (tmp.length() > 0) {
                                            // 文件有内容才发送
                                            this.client.collect(this.path, new FileBody(tmp));
                                        }
                                        Filex.delete(tmp);
                                        // 稍微卡一下再发送，避免一下子涌入日志中心
                                        Thread.sleep(100);
                                    }

                                    // 执行成功，将失败次数置为 0
                                    failTimes = 0;
                                }
                            } finally {
                                if (obtained) {
                                    Filex.delete(lock);
                                }
                            }
                        }
                    }
                } catch (Throwable throwable) {
                    failTimes++;
                    if (System.currentTimeMillis() - lastSend > 10000) {
                        // 每 10 秒试一下能不能访问
                        failTimes = Math.min(failTimes, 9);
                    }

                    throwable.printStackTrace(System.err);
                }
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}
