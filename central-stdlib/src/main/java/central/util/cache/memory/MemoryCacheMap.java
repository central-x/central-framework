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

import central.util.cache.CacheMap;
import central.util.cache.DataType;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * 内存缓存键值对
 *
 * @author Alan Yeh
 * @since 2023/07/02
 */
@RequiredArgsConstructor
public class MemoryCacheMap implements CacheMap {

    private final String key;

    private final MemoryCacheRepository repository;

    @Override
    public long delete(@NotNull String... keys) {
        var cache = this.repository.get(this.key);
        if (cache == null) {
            return 0;
        } else {
            long count = 0;
            var map = (Map<String, String>) cache.getValue();
            for (var key : keys) {
                count += (map.remove(key) != null ? 1 : 0);
            }
            return count;
        }
    }

    @Override
    public boolean hasKey(@NotNull String key) {
        var cache = this.repository.get(this.key);
        if (cache == null) {
            return false;
        } else {
            var map = (Map<String, String>) cache.getValue();
            return map.containsKey(key);
        }
    }

    @Nullable
    @Override
    public String get(@NotNull String key) {
        var cache = this.repository.get(this.key);
        if (cache == null) {
            return null;
        } else {
            var map = (Map<String, String>) cache.getValue();
            return map.get(key);
        }
    }

    @NotNull
    @Override
    public List<String> get(@NotNull String... keys) {
        var cache = this.repository.get(this.key);
        if (cache == null) {
            return Collections.emptyList();
        } else {
            var list = new ArrayList<String>();
            var map = (Map<String, String>) cache.getValue();
            for (var key : keys) {
                var value = map.get(key);
                if (value != null) {
                    list.add(value);
                }
            }
            return list;
        }
    }

    @NotNull
    @Override
    public List<String> get(@NotNull Collection<String> keys) {
        var cache = this.repository.get(this.key);
        if (cache == null) {
            return Collections.emptyList();
        } else {
            var list = new ArrayList<String>();
            var map = (Map<String, String>) cache.getValue();
            for (var key : keys) {
                var value = map.get(key);
                if (value != null) {
                    list.add(value);
                }
            }
            return list;
        }
    }

    @NotNull
    @Override
    public Set<String> keys() {
        var cache = this.repository.get(this.key);
        if (cache == null) {
            return Collections.emptySet();
        } else {
            var map = (Map<String, String>) cache.getValue();
            return map.keySet();
        }
    }

    @Override
    public long size() {
        var cache = this.repository.get(this.key);
        if (cache == null) {
            return 0;
        } else {
            var map = (Map<String, String>) cache.getValue();
            return map.size();
        }
    }

    @Override
    public void put(@NotNull String key, @NotNull String value) {
        var cache = this.repository.get(this.key);
        if (cache == null) {
            var map = new HashMap<String, String>();
            map.put(key, value);
            this.repository.put(this.key, map, DataType.MAP, null);
        } else {
            var map = (Map<String, String>) cache.getValue();
            map.put(key, value);
        }
    }

    @Override
    public void putAll(@NotNull Map<String, String> values) {
        var cache = this.repository.get(this.key);
        if (cache == null) {
            var map = new HashMap<>(values);
            this.repository.put(this.key, map, DataType.MAP, null);
        } else {
            var map = (Map<String, String>) cache.getValue();
            map.putAll(values);
        }
    }

    @Override
    public boolean putIfAbsent(@NotNull Map<String, String> values) {
        var cache = this.repository.get(this.key);
        if (cache == null) {
            var map = new HashMap<>(values);
            this.repository.put(this.key, map, DataType.MAP, null);
            return true;
        }
        return false;
    }

    @NotNull
    @Override
    public List<String> values() {
        var cache = this.repository.get(this.key);
        if (cache == null) {
            return Collections.emptyList();
        } else {
            var map = (Map<String, String>) cache.getValue();
            return List.copyOf(map.values());
        }
    }

    @NotNull
    @Override
    public Map<String, String> entries() {
        var cache = this.repository.get(this.key);
        if (cache == null) {
            return Collections.emptyMap();
        } else {
            var map = (Map<String, String>) cache.getValue();
            return Collections.unmodifiableMap(map);
        }
    }
}
