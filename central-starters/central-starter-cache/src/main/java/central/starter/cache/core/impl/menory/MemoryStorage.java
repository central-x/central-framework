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

package central.starter.cache.core.impl.menory;

import central.lang.Arrayx;
import central.starter.cache.core.CacheStorage;
import central.util.Setx;
import central.util.concurrent.ExpiredElement;
import central.util.concurrent.ExpiredMap;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;

import java.time.Duration;
import java.util.*;

/**
 * 内存存储
 *
 * @author Alan Yeh
 * @since 2022/11/15
 */
@ConditionalOnMissingBean(CacheStorage.class)
public class MemoryStorage implements CacheStorage, DisposableBean {

    private final Map<String, Set<String>> dependencies = new HashMap<>();

    private final ExpiredMap<String, ExpiredElement<Object>> caches = new ExpiredMap<>();

    @Override
    public void destroy() throws Exception {
        this.caches.close();
    }

    @Override
    public boolean exists(String key) {
        return caches.containsKey(key);
    }

    @Override
    public <T> T get(String key) {
        var cache = this.caches.get(key);
        if (cache == null) {
            return null;
        } else {
            return (T) cache.getElement();
        }
    }

    @Override
    public void put(String key, Object value, Duration expires, String... dependencies) {
        this.caches.put(key, new ExpiredElement<>(value, expires));
        if (Arrayx.isNotEmpty(dependencies)) {
            for (var dependency : dependencies) {
                this.dependencies.computeIfAbsent(dependency, k -> new HashSet<>()).add(key);
            }
        }
    }

    @Override
    public void evict(String... keys) {
        for (var key : keys) {
            this.caches.remove(key);

            // 删除依赖
            var dependencies = this.dependencies.remove(key);
            if (Setx.isNotEmpty(dependencies)) {
                this.evict(dependencies.toArray(new String[0]));
            }
        }
    }
}
