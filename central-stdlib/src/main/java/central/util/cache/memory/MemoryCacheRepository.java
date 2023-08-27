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

package central.util.cache.memory;

import central.lang.Assertx;
import central.util.cache.*;
import central.util.concurrent.ConsumableQueue;
import central.util.regex.GlobPattern;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;

/**
 * 内存缓存仓库
 *
 * @author Alan Yeh
 * @since 2023/06/10
 */
public class MemoryCacheRepository implements CacheRepository, AutoCloseable {

    private final Map<String, Cache> caches = new HashMap<>();
    private final ConsumableQueue<Cache, DelayQueue<Cache>> timeoutQueue;

    public MemoryCacheRepository() {
        this.timeoutQueue = new ConsumableQueue<>(new DelayQueue<>(), "central.cache-repository.memory.cleaner");
        this.timeoutQueue.addConsumer(queue -> {
            try {
                while (true) {
                    var cache = queue.poll(5, TimeUnit.SECONDS);
                    if (cache != null) {
                        if (cache.isExpired()) {
                            // 缓存已过期
                            this.caches.remove(cache.getKey());
                        } else if (!cache.isPermanent() && !cache.isInvalid()) {
                            // 如果缓存是临时并且有效的，那么需要重新加入队列进行倒计时
                            queue.offer(cache);
                        }
                    }
                }
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        });
    }

    @Override
    public void close() throws IOException {
        this.timeoutQueue.close();
    }

    @Override
    public boolean hasKey(@NotNull String key) {
        return this.caches.containsKey(key);
    }

    @Override
    public boolean delete(@NotNull String key) {
        if (GlobPattern.isGlobPattern(key)){
            // 通过正则匹配删除缓存
            var keys = this.caches.keySet();
            var matcher = GlobPattern.compile(key);
            for (var it : keys) {
                if (matcher.matcher(it).matches()) {
                    var cache = this.caches.remove(it);
                    if (cache == null) {
                        continue;
                    }
                    // 将缓存置为无效
                    cache.invalid();
                }
            }
        } else {
            var cache = this.caches.remove(key);
            if (cache == null) {
                return false;
            }
            // 将缓存置为无效
            cache.invalid();
        }
        return true;
    }

    @Override
    public long delete(@Nonnull Collection<String> keys) {
        long count = 0;
        for (var key : keys) {
            count += (this.delete(key) ? 1 : 0);
        }
        return count;
    }

    @Override
    public @Nonnull DataType type(@Nonnull String key) {
        var cache = this.caches.get(key);
        if (cache == null) {
            return DataType.NONE;
        } else {
            return cache.getType();
        }
    }

    @Override
    public @Nonnull Set<String> keys() {
        return this.caches.keySet();
    }

    @Override
    public boolean expire(@Nonnull String key, @Nonnull Duration timeout) {
        var cache = this.caches.get(key);
        if (cache == null) {
            return false;
        }
        cache.setExpire(new Date(System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(timeout)));
        this.timeoutQueue.offer(cache);
        return true;
    }

    @Override
    public boolean expireAt(@Nonnull String key, @Nonnull Date date) {
        var cache = this.caches.get(key);
        if (cache == null) {
            return false;
        }

        cache.setExpire(date);
        this.timeoutQueue.offer(cache);
        return true;
    }

    @Override
    public boolean persist(@Nonnull String key) {
        var cache = this.caches.get(key);
        if (cache == null) {
            return false;
        }
        cache.persist();
        return true;
    }

    @Override
    public void clear() {
        this.caches.clear();
    }

    @Override
    public Duration getExpire(@Nonnull String key) {
        var cache = this.caches.get(key);
        if (cache == null) {
            return null;
        } else {
            return Duration.ofMillis(cache.getDelay(TimeUnit.MILLISECONDS));
        }
    }

    @Override
    public @Nonnull CacheValue opsValue(@Nonnull String key) throws ClassCastException {
        var cache = this.caches.get(key);
        if (cache != null) {
            Assertx.mustTrue(DataType.STRING.isCompatibleWith(cache.getType()), ClassCastException::new,
                    "缓存[key={}]的类型为{}({})，不支持转换为{}({})类型",
                    key, cache.getType().getName(), cache.getType().getCode(), DataType.STRING.getName(), DataType.STRING.getCode());
        }
        return new MemoryCacheValue(key, this);
    }

    @Override
    public @Nonnull CacheList opsList(@Nonnull String key) throws ClassCastException {
        var cache = this.caches.get(key);
        if (cache != null) {
            Assertx.mustTrue(DataType.LIST.isCompatibleWith(cache.getType()), ClassCastException::new,
                    "缓存[key={}]的类型为{}({})，不支持转换为{}({})类型",
                    key, cache.getType().getName(), cache.getType().getCode(), DataType.LIST.getName(), DataType.LIST.getCode());
        }
        return new MemoryCacheList(key, this);
    }

    @Override
    public @Nonnull CacheQueue opsQueue(@Nonnull String key) throws ClassCastException {
        var cache = this.caches.get(key);
        if (cache != null) {
            Assertx.mustTrue(DataType.QUEUE.isCompatibleWith(cache.getType()), ClassCastException::new,
                    "缓存[key={}]的类型为{}({})，不支持转换为{}({})类型",
                    key, cache.getType().getName(), cache.getType().getCode(), DataType.QUEUE.getName(), DataType.QUEUE.getCode());
        }
        return new MemoryCacheQueue(key, this);
    }

    @Override
    public @Nonnull CacheSet opsSet(@Nonnull String key) throws ClassCastException {
        var cache = this.caches.get(key);
        if (cache != null) {
            Assertx.mustTrue(DataType.SET.isCompatibleWith(cache.getType()), ClassCastException::new,
                    "缓存[key={}]的类型为{}({})，不支持转换为{}({})类型",
                    key, cache.getType().getName(), cache.getType().getCode(), DataType.SET.getName(), DataType.SET.getCode());
        }
        return new MemoryCacheSet(key, this);
    }

    @Override
    public @Nonnull CacheSet opsZSet(@Nonnull String key) throws ClassCastException {
        var cache = this.caches.get(key);
        if (cache != null) {
            Assertx.mustTrue(DataType.ZSET.isCompatibleWith(cache.getType()), ClassCastException::new,
                    "缓存[key={}]的类型为{}({})，不支持转换为{}({})类型",
                    key, cache.getType().getName(), cache.getType().getCode(), DataType.ZSET.getName(), DataType.ZSET.getCode());
        }
        return new MemoryCacheZSet(key, this);
    }

    @Override
    public @Nonnull CacheMap opsMap(@Nonnull String key) throws ClassCastException {
        var cache = this.caches.get(key);
        if (cache != null) {
            Assertx.mustTrue(DataType.MAP.isCompatibleWith(cache.getType()), ClassCastException::new,
                    "缓存[key={}]的类型为{}({})，不支持转换为{}({})类型",
                    key, cache.getType().getName(), cache.getType().getCode(), DataType.MAP.getName(), DataType.MAP.getCode());
        }
        return new MemoryCacheMap(key, this);
    }

    /// internal api
    @Nullable
    Cache put(@Nonnull String key, @Nullable Object value, @Nonnull DataType type, @Nullable Duration timeout) {
        var cache = new Cache(key, value, type);
        var old = this.caches.put(key, new Cache(key, value, type));
        if (timeout != null) {
            cache.setExpire(new Date(System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(timeout)));
            this.timeoutQueue.offer(cache);
        }
        return old;
    }

    @Nullable
    Cache get(@Nonnull String key) {
        return this.caches.get(key);
    }

    @Nullable
    Cache remove(@Nonnull String key) {
        return this.caches.remove(key);
    }
}
