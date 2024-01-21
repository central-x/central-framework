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

import central.starter.logging.logback.appender.CentralAppender;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 使用 Http 的方式将日志发送到采集中心进行收集
 *
 * @author Alan Yeh
 * @since 2022/10/24
 */
public class HttpAppender extends CentralAppender {

    /**
     * 采集器服务
     */
    @Getter
    @Setter
    private String collectorServer;

    /**
     * 采集器路径
     */
    @Getter
    @Setter
    private String collectorPath;

    /**
     * 微服务名
     */
    @Getter
    @Setter
    private String serviceName;

    /**
     * 微服务版本号
     */
    @Getter
    @Setter
    private String serviceVersion;

    /**
     * 微服务端口
     */
    @Getter
    @Setter
    private String servicePort;

    /**
     * 应用标识（应用服务名）
     */
    @Getter
    @Setter
    private String applicationCode;

    /**
     * 应用密钥
     */
    @Getter
    @Setter
    private String applicationSecret;

    /**
     * 最大批量发送大小
     */
    @Getter
    @Setter
    private int batchSize = 100000;

    /**
     * 最大批量发送时间
     */
    @Getter
    @Setter
    private int batchTime = 2000;

    @Override
    public String getTmpPath() {
        return "log_tmp";
    }

    private ExecutorService executor;

    @Override
    public void start() {
        super.start();

        var sender = new HttpSender(new File(this.getTmpPath(), this.getApplicationCode()), this.applicationCode, this.applicationSecret, this.collectorServer, this.collectorPath);
        this.executor = Executors.newCachedThreadPool(new CustomizableThreadFactory("central.logging.http.appender.sender"));
        this.executor.submit(() -> {
            try {
                sender.afterPropertiesSet();
            } catch (Exception ex) {
                throw new BeanInitializationException("LoggerSender 初始化异常: " + ex.getLocalizedMessage(), ex);
            }

            sender.run();
        });
    }

    @Override
    public void stop() {
        super.stop();

        if (this.executor != null) {
            this.executor.shutdownNow();
            this.executor = null;
        }
    }
}
