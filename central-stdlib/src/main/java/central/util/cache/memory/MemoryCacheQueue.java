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

import central.util.cache.CacheQueue;
import central.util.cache.DataType;
import central.util.concurrent.BlockedQueue;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 内存缓存队列
 *
 * @author Alan Yeh
 * @since 2023/07/02
 */
@RequiredArgsConstructor
public class MemoryCacheQueue implements CacheQueue {
    private final String key;

    private final MemoryCacheRepository repository;

    @NotNull
    @Override
    public List<String> values() {
        var cache = this.repository.get(this.key);
        if (cache == null) {
            return Collections.emptyList();
        } else {
            var queue = (BlockedQueue<String>) cache.getValue();
            return List.copyOf(queue);
        }
    }

    @Override
    public boolean push(@NotNull String value) {
        var cache = this.repository.get(this.key);
        if (cache == null) {
            this.repository.put(this.key, new BlockedQueue<>(List.of(value)), DataType.QUEUE, null);
            return true;
        } else {
            var queue = (BlockedQueue<String>) cache.getValue();
            return queue.offer(value);
        }
    }

    @Nullable
    @Override
    public String pop() {
        var cache = this.repository.get(this.key);
        if (cache == null) {
            return null;
        } else {
            var queue = (BlockedQueue<String>) cache.getValue();
            return queue.poll();
        }
    }

    @NotNull
    @Override
    public List<String> pop(long count) {
        var cache = this.repository.get(this.key);
        if (cache == null) {
            return Collections.emptyList();
        } else {
            var list = new ArrayList<String>();
            var queue = (BlockedQueue<String>) cache.getValue();

            for (long i = 0; i < count; i++) {
                var removed = queue.poll();
                if (removed != null) {
                    list.add(removed);
                }
            }
            return Collections.unmodifiableList(list);
        }
    }

    @Nullable
    @Override
    public String take(@NotNull Duration timeout) throws InterruptedException {
        var cache = this.repository.get(this.key);
        if (cache == null) {
            this.repository.put(this.key, new BlockedQueue<>(), DataType.QUEUE, null);
            cache = this.repository.get(this.key);
        }
        if (cache == null) {
            return null;
        }
        var queue = (BlockedQueue<String>) cache.getValue();
        return queue.poll(TimeUnit.MILLISECONDS.convert(timeout), TimeUnit.MILLISECONDS);
    }

    @Nullable
    @Override
    public String peek() {
        var cache = this.repository.get(this.key);
        if (cache == null) {
            return null;
        } else {
            var queue = (BlockedQueue<String>) cache.getValue();
            return queue.peek();
        }
    }

    @NotNull
    @Override
    public List<String> peek(long count) {
        var cache = this.repository.get(this.key);
        if (cache == null) {
            return Collections.emptyList();
        } else {
            var list = new ArrayList<String>();
            var queue = (BlockedQueue<String>) cache.getValue();

            var it = queue.iterator();
            for (long i = 0; i < count; i++) {
                if (it.hasNext()) {
                    var next = it.next();
                    list.add(next);
                }
            }

            return list;
        }
    }
}
