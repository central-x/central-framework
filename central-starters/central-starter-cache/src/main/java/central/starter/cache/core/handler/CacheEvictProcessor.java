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

import central.lang.Arrayx;
import central.lang.Assertx;
import central.lang.Stringx;
import central.pattern.chain.ProcessChain;
import central.starter.cache.core.CacheProcessor;
import central.starter.cache.core.CacheStorage;
import central.starter.cache.core.annotation.CacheEvict;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * 清除缓存
 *
 * @author Alan Yeh
 * @see CacheEvict
 * @since 2022/11/15
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class CacheEvictProcessor extends CacheProcessor {

    @Setter(onMethod_ = @Autowired)
    private CacheStorage storage;

    @Override
    public boolean predicate(MethodInvocation target) {
        return target.getMethod().isAnnotationPresent(CacheEvict.class) || target.getMethod().isAnnotationPresent(CacheEvict.List.class);
    }

    @Override
    public Object process(MethodInvocation target, ProcessChain<MethodInvocation, Object> chain) throws Throwable {
        this.evict(true, target, null);

        var result = chain.process(target);

        this.evict(false, target, result);

        return result;
    }

    private void evict(boolean before, MethodInvocation target, Object result) {
        var evicts = target.getMethod().getDeclaredAnnotationsByType(CacheEvict.class);

        for (var evict : evicts) {
            if (evict.beforeInvocation() == before) {
                Assertx.mustFalse(Stringx.isNullOrBlank(evict.key()) && Arrayx.isNullOrEmpty(evict.keys()),
                        "方法 {}.{} 的注解 @CacheEvict 的 key 与 keys 必须不能同时为空", target.getMethod().getDeclaringClass().getSimpleName(), target.getMethod().getName());
                Assertx.mustFalse(Stringx.isNotBlank(evict.key()) && Arrayx.isNotEmpty(evict.keys()),
                        "方法 {}.{} 的注解 @CacheEvict 的 key 与 keys 必须不能同时不为空", target.getMethod().getDeclaringClass().getSimpleName(), target.getMethod().getName());

                var evictKeys = new ArrayList<String>();

                if (Stringx.isNotBlank(evict.key())) {
                    evictKeys.add(this.evaluate(evict.key(), target, null));
                } else {
                    var cacheKeys = evict.keys();
                    for (var cacheKey : cacheKeys) {
                        var keys = this.evaluateKeys(cacheKey.key(), target, null, cacheKey.it());
                        evictKeys.addAll(keys);
                    }
                }

                for (var evictKey : evictKeys) {
                    log.info("@CacheEvict: 在执行方法 {}.{} {}清除缓存[{}]", target.getMethod().getDeclaringClass().getSimpleName(), target.getMethod().getName(), before ? "前" : "后", evictKey);
                }

                storage.evict(evictKeys.toArray(new String[0]));
            }
        }
    }
}
