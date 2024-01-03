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

package central.starter.cache.core.handler;

import central.bean.Null;
import central.lang.Arrayx;
import central.lang.Stringx;
import central.pattern.chain.ProcessChain;
import central.starter.cache.core.CacheProcessor;
import central.starter.cache.core.CacheStorage;
import central.starter.cache.core.annotation.Cacheable;
import central.util.Guidx;
import central.util.Objectx;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.*;

/**
 * 缓存
 *
 * @author Alan Yeh
 * @see Cacheable
 * @since 2022/11/15
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CacheableProcessor extends CacheProcessor {

    @Setter(onMethod_ = @Autowired)
    private CacheStorage storage;

    @Override
    public boolean predicate(MethodInvocation target) {
        return target.getMethod().isAnnotationPresent(Cacheable.class);
    }

    private static final ThreadLocal<Map<String, Set<String>>> ANALYSIS_HOLDER = ThreadLocal.withInitial(HashMap::new);

    @Override
    public Object process(MethodInvocation target, ProcessChain<MethodInvocation, Object> chain) throws Exception {
        var cacheable = target.getMethod().getDeclaredAnnotation(Cacheable.class);

        var analysisKey = Guidx.nextID();
        try {
            // 自动分析依赖
            ANALYSIS_HOLDER.get().put(analysisKey, new HashSet<>());

            // 计算缓存 key
            var key = this.evaluate(cacheable.key(), target, null);

            // 获取缓存
            var cachedResult = storage.get(key);

            // 如果存在缓存，则直接返回，后续的操作不执行
            if (cachedResult != null) {
                log.info("@Cacheable: 在执行方法 {}.{} 前发现缓存[{}]，返回缓存结果", target.getMethod().getDeclaringClass().getSimpleName(), target.getMethod().getName(), key);

                Object result;
                if (cachedResult instanceof Null) {
                    result = null;
                } else {
                    result = cachedResult;
                }

                // 将该缓存的 Key 或依赖加入分析依赖容器
                if (Arrayx.isNotEmpty(cacheable.dependencies())) {
                    // 如果 cacheable 存在 dependencies，需要将计算好的 dependencies 加入自动分析的容器
                    var dependencies = Arrayx.asStream(cacheable.dependencies()).map(it -> this.evaluate(it, target, result)).toList();

                    // 把计算好的依赖加入自动分析容器，供上层的缓存进行分析
                    ANALYSIS_HOLDER.get().values().forEach(analysis -> analysis.addAll(dependencies));
                } else {
                    // 如果 cacheable 的 dependencies 为空，则将自己的缓存 key 加入自动分析容器
                    ANALYSIS_HOLDER.get().values().forEach(analysis -> analysis.add(key));
                }
                return result;
            }

            // 调用下一步，获取执行结果
            var result = chain.process(target);

            // 如果 condition 不为空，则要求 condition 的计算结果为 true 才进行缓存
            if (Stringx.isNotBlank(cacheable.condition())) {
                if (!this.evaluateBoolean(cacheable.condition(), target, result)) {
                    // 不缓存
                    return result;
                }
            }

            // 分析当前 key 需要哪些依赖
            Set<String> analysisDependencies;
            if (Arrayx.isNotEmpty(cacheable.dependencies())) {
                // 如果 cacheable 有 dependencies，需要将计算好的 dependencies 加入到自动分析的容器
                var dependencies = Arrayx.asStream(cacheable.dependencies()).map(it -> this.evaluate(it, target, result)).toList();

                // 把计算好的依赖，加入自动分析容器，供上层的缓存进行分析
                ANALYSIS_HOLDER.get().values().forEach(analysis -> analysis.addAll(dependencies));

                // 将自动分析出来的依赖，和当前指定的依赖加入依赖列表
                analysisDependencies = new HashSet<>(ANALYSIS_HOLDER.get().get(analysisKey));
            } else {
                // 将自动分析出来的依赖，和当前指定的依赖加入依赖列表
                analysisDependencies = new HashSet<>(ANALYSIS_HOLDER.get().get(analysisKey));

                // 如果 cacheable 的 dependencies 为空，则将自己的缓存 key 加入自动分析容器
                ANALYSIS_HOLDER.get().values().forEach(analysis -> analysis.add(key));
            }

            log.info("@Cacheable: 在执行 {}.{} 后，保存执行结果到[{}]", target.getMethod().getDeclaringClass().getSimpleName(), target.getMethod().getName(), key);

            storage.put(key, Objectx.getOrDefault(result, Null::new), Duration.ofMinutes(cacheable.expires()), analysisDependencies.toArray(new String[0]));

            return result;
        } finally {
            // 移除自动分析
            ANALYSIS_HOLDER.get().remove(analysisKey);
        }
    }
}
