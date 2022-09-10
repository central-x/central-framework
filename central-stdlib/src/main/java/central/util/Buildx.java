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

package central.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 通用 Builder
 *
 * @author Alan Yeh
 * @since 2022/08/23
 */
public class Buildx<T> {
    private final Supplier<T> instance;
    private final List<Consumer<T>> modifiers = new ArrayList<>();

    public Buildx(Supplier<T> supplier) {
        this.instance = supplier;
    }

    public static <T> Buildx<T> of(Supplier<T> supplier) {
        return new Buildx<>(supplier);
    }

    // builder 方式
    public <P> Buildx<T> with(Setter<T, P> setter, P value) {
        modifiers.add(obj -> setter.set(obj, value));
        return this;
    }

    public <P1, P2> Buildx<T> with(Setter2<T, P1, P2> setter, P1 param1, P2 param2) {
        modifiers.add(obj -> setter.set(obj, param1, param2));
        return this;
    }

    public <P1, P2, P3> Buildx<T> with(Setter3<T, P1, P2, P3> setter, P1 param1, P2 param2, P3 param3) {
        modifiers.add(obj -> setter.set(obj, param1, param2, param3));
        return this;
    }

    public <P1, P2, P3, P4> Buildx<T> with(Setter4<T, P1, P2, P3, P4> setter, P1 param1, P2 param2, P3 param3, P4 param4) {
        modifiers.add(obj -> setter.set(obj, param1, param2, param3, param4));
        return this;
    }

    // build
    public T build() {
        var value = instance.get();
        modifiers.forEach(modifier -> modifier.accept(value));
        return value;
    }

    @FunctionalInterface
    public interface Setter<T, P> {
        void set(T t, P p);
    }

    @FunctionalInterface
    public interface Setter2<T, P1, P2> {
        void set(T t, P1 p1, P2 p2);
    }

    @FunctionalInterface
    public interface Setter3<T, P1, P2, P3> {
        void set(T t, P1 p1, P2 p2, P3 p3);
    }

    @FunctionalInterface
    public interface Setter4<T, P1, P2, P3, P4> {
        void set(T t, P1 p1, P2 p2, P3 p3, P4 p4);
    }
}
