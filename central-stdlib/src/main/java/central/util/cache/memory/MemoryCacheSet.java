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

import central.util.cache.CacheSet;
import central.util.cache.DataType;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * 内存缓存集合(HashSet)
 *
 * @author Alan Yeh
 * @since 2023/07/02
 */
@RequiredArgsConstructor
public class MemoryCacheSet implements CacheSet {

    private final String key;

    private final MemoryCacheRepository repository;

    @Nullable
    @Override
    public Set<String> values() {
        var cache = this.repository.get(this.key);
        if (cache == null) {
            return Collections.emptySet();
        } else {
            var set = (Set<String>) cache.getValue();
            return Collections.unmodifiableSet(set);
        }
    }

    @Override
    public long add(@NotNull String... values) {
        var cache = this.repository.get(this.key);
        if (cache == null) {
            this.repository.put(this.key, new HashSet<>(Arrays.asList(values)), DataType.SET, null);
        } else {
            var set = (Set<String>) cache.getValue();
            set.addAll(Arrays.asList(values));
        }
        return values.length;
    }

    @Override
    public long add(@NotNull Collection<String> values) {
        var cache = this.repository.get(this.key);
        if (cache == null) {
            this.repository.put(this.key, new HashSet<>(values), DataType.SET, null);
        } else {
            var set = (Set<String>) cache.getValue();
            set.addAll(values);
        }
        return values.size();
    }

    @Override
    public long remove(@NotNull String... values) {
        var cache = this.repository.get(this.key);
        if (cache == null) {
            return 0L;
        } else {
            long removed = 0;
            var set = (Set<String>) cache.getValue();
            for (String value : values) {
                if (set.remove(value)) {
                    removed++;
                }
            }
            return removed;
        }
    }

    @Override
    public long remove(@NotNull Collection<String> values) {
        var cache = this.repository.get(this.key);
        if (cache == null) {
            return 0L;
        } else {
            long removed = 0;
            var set = (Set<String>) cache.getValue();
            for (String value : values) {
                if (set.remove(value)) {
                    removed++;
                }
            }
            return removed;
        }
    }

    @Nullable
    @Override
    public String pop() {
        var cache = this.repository.get(this.key);
        if (cache == null) {
            return null;
        } else {
            var set = (Set<String>) cache.getValue();

            var iterator = set.iterator();
            if (!iterator.hasNext()) {
                return null;
            } else {
                var next = iterator.next();
                iterator.remove();
                return next;
            }
        }
    }

    @NotNull
    @Override
    public List<String> pop(long count) {
        var cache = this.repository.get(this.key);
        if (cache == null) {
            return Collections.emptyList();
        } else {
            var list = new ArrayList<String>((int) count);
            var set = (Set<String>) cache.getValue();

            var iterator = set.iterator();
            for (int i = 0; i < count; i++) {
                if (!iterator.hasNext()) {
                    return Collections.emptyList();
                } else {
                    var next = iterator.next();
                    iterator.remove();
                    list.add(next);
                }
            }
            return Collections.unmodifiableList(list);
        }
    }

    @Override
    public long size() {
        var cache = this.repository.get(this.key);
        if (cache == null) {
            return 0L;
        } else {
            var set = (Set<String>) cache.getValue();
            return set.size();
        }
    }

    @Override
    public boolean contains(@NotNull String value) {
        var cache = this.repository.get(this.key);
        if (cache == null) {
            return false;
        } else {
            var set = (Set<String>) cache.getValue();
            return set.contains(value);
        }
    }
}
