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
import central.util.cache.CacheList;
import central.util.cache.DataType;
import central.util.Range;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * 内存缓存列表
 *
 * @author Alan Yeh
 * @since 2023/07/02
 */
@RequiredArgsConstructor
public class MemoryCacheList implements CacheList {

    private final String key;

    private final MemoryCacheRepository repository;

    @NotNull
    @Override
    public List<String> values() {
        var cache = this.repository.get(this.key);
        if (cache == null) {
            return Collections.emptyList();
        } else {
            var list = (List<String>) cache.getValue();
            return Collections.unmodifiableList(list);
        }
    }

    @NotNull
    @Override
    public List<String> range(long start, long end) {
        var cache = this.repository.get(this.key);
        if (cache == null) {
            return Collections.emptyList();
        } else {
            var list = (List<String>) cache.getValue();
            return list.subList((int) start, (int) end);
        }
    }

    @Override
    public void trim(long start, long end) {
        var cache = this.repository.get(this.key);
        if (cache != null) {
            var list = (List<String>) cache.getValue();
            for (var index : Range.of(start, end)) {
                list.remove(index.intValue());
            }
        }
    }

    @Override
    public long size() {
        var cache = this.repository.get(this.key);
        if (cache == null) {
            return 0;
        } else {
            var list = (List<String>) cache.getValue();
            return list.size();
        }
    }

    @Override
    public long add(@NotNull String... values) {
        var cache = this.repository.get(this.key);
        if (cache == null) {
            this.repository.put(this.key, new ArrayList<>(Arrays.asList(values)), DataType.LIST, null);
        } else {
            var list = (List<String>) cache.getValue();
            list.addAll(Arrays.asList(values));
        }
        return values.length;
    }

    @Override
    public long add(@NotNull Collection<String> values) {
        var cache = this.repository.get(this.key);
        if (cache == null) {
            this.repository.put(this.key, new ArrayList<>(values), DataType.LIST, null);
        } else {
            var list = (List<String>) cache.getValue();
            list.addAll(values);
        }
        return values.size();
    }

    @Override
    public long addIfPresent(@NotNull String... values) {
        var cache = this.repository.get(this.key);
        if (cache != null) {
            var list = (List<String>) cache.getValue();
            list.addAll(Arrays.asList(values));
            return values.length;
        } else {
            return 0L;
        }
    }

    @Override
    public long insert(int index, @NotNull String... values) {
        var cache = this.repository.get(this.key);
        if (cache != null) {
            var list = (List<String>) cache.getValue();
            list.addAll(index, Arrays.asList(values));
            return values.length;
        } else {
            if (index == 0) {
                this.repository.put(this.key, new ArrayList<>(Arrays.asList(values)), DataType.LIST, null);
                return values.length;
            } else {
                throw new IndexOutOfBoundsException(Stringx.format("无法在下标 {} 处插入数据", index));
            }
        }
    }

    @Override
    public void set(long index, @NotNull String value) {
        var cache = this.repository.get(this.key);
        if (cache != null) {
            var list = (List<String>) cache.getValue();
            list.set((int) index, value);
        } else {
            if (index == 0) {
                this.repository.put(this.key, new ArrayList<>(List.of(value)), DataType.LIST, null);
            } else {
                throw new IndexOutOfBoundsException(Stringx.format("无法修改下标 {} 处数据", index));
            }
        }
    }

    @Override
    public long remove(long count, @NotNull String value) {
        var cache = this.repository.get(this.key);
        if (cache == null) {
            return 0L;
        } else {
            long removed = 0;
            var list = (List<String>) cache.getValue();
            var it = list.iterator();
            while (it.hasNext()) {
                var next = it.next();
                if (Objects.equals(next, value)) {
                    it.remove();
                    removed++;
                    count--;
                }
                if (count == 0) {
                    break;
                }
            }
            return removed;
        }
    }

    @Override
    public long remove(long index) {
        var cache = this.repository.get(this.key);
        if (cache == null) {
            return 0;
        } else {
            var list = (List<String>) cache.getValue();
            var removed = list.remove((int) index);
            return removed == null ? 0 : 1;
        }
    }

    @Override
    public long indexOf(@NotNull String value) {
        var cache = this.repository.get(this.key);
        if (cache == null) {
            return 0;
        } else {
            var list = (List<String>) cache.getValue();
            return list.indexOf(value);
        }
    }

    @Override
    public long lastIndexOf(@NotNull String value) {
        var cache = this.repository.get(this.key);
        if (cache == null) {
            return 0;
        } else {
            var list = (List<String>) cache.getValue();
            return list.lastIndexOf(value);
        }
    }
}
