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

package central.util.cache.redis;

import central.bean.MethodNotImplementedException;
import central.util.cache.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

/**
 * Redis 缓存仓库
 *
 * @author Alan Yeh
 * @since 2023/06/10
 */
public class RedisCacheRepository implements CacheRepository {
    @Override
    public boolean hasKey(@NotNull String key) {
        throw new MethodNotImplementedException();
    }

    @Override
    public boolean delete(@NotNull String key) {
        throw new MethodNotImplementedException();
    }

    @Override
    public long delete(@NotNull Collection<String> keys) {
        throw new MethodNotImplementedException();
    }

    @NotNull
    @Override
    public DataType type(@NotNull String key) {
        throw new MethodNotImplementedException();
    }

    @NotNull
    @Override
    public Set<String> keys() {
        throw new MethodNotImplementedException();
    }

    @Override
    public boolean expire(@NotNull String key, @NotNull Duration timeout) {
        throw new MethodNotImplementedException();
    }

    @Override
    public boolean expireAt(@NotNull String key, @NotNull Date date) {
        throw new MethodNotImplementedException();
    }

    @Override
    public boolean persist(@NotNull String key) {
        throw new MethodNotImplementedException();
    }

    @Override
    public void clear() {
        throw new MethodNotImplementedException();
    }

    @Nullable
    @Override
    public Duration getExpire(@NotNull String key) {
        throw new MethodNotImplementedException();
    }

    @NotNull
    @Override
    public CacheValue opsValue(@NotNull String key) throws ClassCastException {
        throw new MethodNotImplementedException();
    }

    @NotNull
    @Override
    public CacheList opsList(@NotNull String key) throws ClassCastException {
        throw new MethodNotImplementedException();
    }

    @NotNull
    @Override
    public CacheQueue opsQueue(@NotNull String key) throws ClassCastException {
        throw new MethodNotImplementedException();
    }

    @NotNull
    @Override
    public CacheSet opsSet(@NotNull String key) throws ClassCastException {
        throw new MethodNotImplementedException();
    }

    @NotNull
    @Override
    public CacheSet opsZSet(@NotNull String key) throws ClassCastException {
        throw new MethodNotImplementedException();
    }

    @NotNull
    @Override
    public CacheMap opsMap(@NotNull String key) throws ClassCastException {
        throw new MethodNotImplementedException();
    }
}
