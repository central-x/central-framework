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

package central.starter.probe.core;

import central.starter.probe.ProbeProperties;
import central.starter.probe.core.cache.Cache;
import central.starter.probe.core.endpoint.Endpoint;
import central.starter.probe.core.authorizer.Authorizer;
import central.util.Mapx;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 探针控制器
 *
 * @author Alan Yeh
 * @since 2023/12/27
 */
@Controller
@RequestMapping("/__probe")
public class ProbeController {
    @Setter(onMethod_ = @Autowired)
    private Authorizer authorizer;

    @Setter(onMethod_ = @Autowired)
    private Cache cache;

    @Setter(onMethod_ = @Autowired(required = false))
    private Map<String, Endpoint> endpoints;

    @Setter(onMethod_ = @Autowired)
    private ProbeProperties properties;

    /**
     * 探针入口地址
     */
    @GetMapping
    public ResponseEntity<Map<String, String>> index(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) throws InterruptedException {
        // 验证保护器
        try {
            authorizer.authorize(authorization);
        } catch (ProbeException cause) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", cause.getLocalizedMessage()));
        }

        // 获取缓存
        // 如果缓存存在，则直接返回缓存
        //
        // 由于探针之间可能会产生依赖，如 gateway、identity、logging、multicast、storage 等都依赖着 provider，因此这些微服务的探针都会探测 provider 的探针，
        // 从而导致 provider 的探测探测过于频繁。
        // 为了解决这个问题，可以通过缓存来解决。provider 设置一个略低于自身探测频率的缓存失效时间，其它服务在调用的过程中，就不再真实探测，而是返回最近一次探测结果
        // 这样就既保证了 provider 探测正常，也保证了其余依赖探测过高频率产生负面影响了
        var cached = this.cache.get();
        if (Mapx.isNotEmpty(cached)) {
            return toEntity(cached);
        }

        // 读取探针内容
        var result = new HashMap<String, String>();

        if (Mapx.isNotEmpty(this.endpoints)) {
            // 预设所有的任务都是超时的
            for (var endpoint : this.endpoints.entrySet()) {
                result.put(endpoint.getKey(), "Timeout");
            }
            // 任务倒计时
            var latch = new CountDownLatch(this.endpoints.size());

            // 所有任务都是异步执行的
            ExecutorService executor = null;
            try {
                executor = Executors.newFixedThreadPool(this.endpoints.size(), new CustomizableThreadFactory("probe"));
                for (var endpointEntry : this.endpoints.entrySet()) {
                    executor.submit(() -> {
                        try {
                            endpointEntry.getValue().perform();
                            // 修改任务状态为 OK
                            result.put(endpointEntry.getKey(), "OK");
                            latch.countDown();
                        } catch (Exception error) {
                            // 登记任务异常
                            result.put(endpointEntry.getKey(), error.getLocalizedMessage());
                        }
                    });
                }
                // 等待指定时间
                // 超时则全部关闭
                latch.await(this.properties.getTimeout(), TimeUnit.MILLISECONDS);
            } finally {
                // 关闭线程池
                if (executor != null) {
                    executor.shutdown();
                }
            }
        }

        this.cache.put(result);
        return toEntity(result);
    }

    private ResponseEntity<Map<String, String>> toEntity(Map<String, String> data) {
        // 如果任务状态里面有不是 OK 的，则认为探测失败
        var isOK = data.values().stream().filter(it -> !"OK".equals(it)).findAny().isEmpty();
        return ResponseEntity.status(isOK ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR).body(data);
    }
}
