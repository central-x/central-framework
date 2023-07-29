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

import central.lang.Stringx;
import central.util.cache.CacheValue;
import central.util.cache.DataType;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;

/**
 * 内存缓存值
 *
 * @author Alan Yeh
 * @since 2023/07/02
 */
@RequiredArgsConstructor
public class MemoryCacheValue implements CacheValue {
    private final String key;

    private final MemoryCacheRepository repository;

    @Nullable
    @Override
    public String set(@NotNull String value) {
        var origin = this.repository.put(this.key, value, DataType.STRING, null);
        if (origin != null) {
            return origin.getValue().toString();
        } else {
            return null;
        }
    }

    @Nullable
    @Override
    public String set(@NotNull String value, @Nullable Duration timeout) {
        var origin = this.repository.put(this.key, value, DataType.STRING, timeout);
        if (origin != null) {
            return origin.getValue().toString();
        } else {
            return null;
        }
    }

    @Override
    public boolean setIfAbsent(@NotNull String value) {
        var origin = this.repository.get(this.key);
        if (origin == null) {
            this.repository.put(this.key, value, DataType.STRING, null);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean setIfAbsent(@NotNull String value, @Nullable Duration timeout) {
        var origin = this.repository.get(this.key);
        if (origin == null) {
            this.repository.put(this.key, value, DataType.STRING, timeout);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean setIfPresent(@NotNull String value) {
        var origin = this.repository.get(this.key);
        if (origin != null) {
            origin.setValue(value);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean setIfPresent(@NotNull String value, @Nullable Duration timeout) {
        var origin = this.repository.get(this.key);
        if (origin != null) {
            origin.setValue(value);
            if (timeout != null) {
                this.repository.expire(this.key, timeout);
            }
            return true;
        } else {
            return false;
        }
    }

    @Nullable
    @Override
    public String getValue() {
        var cache = this.repository.get(this.key);
        if (cache == null) {
            return null;
        } else {
            return cache.getValue().toString();
        }
    }

    @Nullable
    @Override
    public String getAndDelete() {
        var cache = this.repository.remove(this.key);
        if (cache == null) {
            return null;
        } else {
            return cache.getValue().toString();
        }
    }

    @NotNull
    @Override
    public Long increment() throws NumberFormatException {
        var cache = this.repository.get(this.key);
        if (cache == null) {
            this.repository.put(this.key, 1L, DataType.STRING, null);
            return 1L;
        } else {
            long number;
            Object value = cache.getValue();
            if (value instanceof String stringValue) {
                number = Long.parseLong(stringValue) + 1;
                cache.setValue(number);
            } else if (value instanceof Long longValue) {
                number = longValue + 1;
                cache.setValue(number);
            } else {
                throw new NumberFormatException(Stringx.format("缓存[key={}]的类型为{}({})，无法自增", this.key, cache.getType().getName(), cache.getType().getCode()));
            }
            return number;
        }
    }

    @NotNull
    @Override
    public Long increment(long delta) throws NumberFormatException {
        var cache = this.repository.get(this.key);
        if (cache == null) {
            this.repository.put(this.key, delta, DataType.STRING, null);
            return delta;
        } else {
            long number;
            Object value = cache.getValue();
            if (value instanceof String stringValue) {
                number = Long.parseLong(stringValue) + delta;
                cache.setValue(number);
            } else if (value instanceof Long longValue) {
                number = longValue + delta;
                cache.setValue(number);
            } else {
                throw new NumberFormatException(Stringx.format("缓存[key={}]的类型为{}({})，无法自增", this.key, cache.getType().getName(), cache.getType().getCode()));
            }
            return number;
        }
    }

    @NotNull
    @Override
    public Long decrement() throws NumberFormatException {
        var cache = this.repository.get(this.key);
        if (cache == null) {
            this.repository.put(this.key, -1L, DataType.STRING, null);
            return -1L;
        } else {
            long number;
            Object value = cache.getValue();
            if (value instanceof String stringValue) {
                number = Long.parseLong(stringValue) - 1;
                cache.setValue(number);
            } else if (value instanceof Long longValue) {
                number = longValue - 1;
                cache.setValue(number);
            } else {
                throw new NumberFormatException(Stringx.format("缓存[key={}]的类型为{}({})，无法自减", this.key, cache.getType().getName(), cache.getType().getCode()));
            }
            return number;
        }
    }

    @NotNull
    @Override
    public Long decrement(long delta) throws NumberFormatException {
        var cache = this.repository.get(this.key);
        if (cache == null) {
            this.repository.put(this.key, delta * -1, DataType.STRING, null);
            return delta * -1;
        } else {
            long number;
            Object value = cache.getValue();
            if (value instanceof String stringValue) {
                number = Long.parseLong(stringValue) - delta;
                cache.setValue(number);
            } else if (value instanceof Long longValue) {
                number = longValue - delta;
                cache.setValue(number);
            } else {
                throw new NumberFormatException(Stringx.format("缓存[key={}]的类型为{}({})，无法自增", this.key, cache.getType().getName(), cache.getType().getCode()));
            }
            return number;
        }
    }

}
