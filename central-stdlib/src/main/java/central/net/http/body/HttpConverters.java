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

package central.net.http.body;

import central.net.http.body.converter.*;
import central.util.LazyValue;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 值转字符串
 *
 * @author Alan Yeh
 * @since 2022/07/14
 */
public class HttpConverters implements HttpConverter<Object> {
    private final List<HttpConverter<?>> instances;

    public HttpConverters() {
        this.instances = List.of(
                new NullConverter(),
                new StringConverter(),
                new BooleanConverter(),
                new NumberConverter(),
                new TimestampConverter(),
                new OptionalEnumConverter()
        );
    }

    private static final LazyValue<HttpConverters> instance = new LazyValue<>(HttpConverters::new);

    public static HttpConverters Default() {
        return instance.get();
    }

    @Override
    public boolean support(@Nullable Object source) {
        return this.instances.stream().anyMatch(it -> it.support(source));
    }

    @Override
    @SuppressWarnings("rawtypes")
    public String convert(Object source) {
        for (HttpConverter instance : this.instances) {
            if (instance.support(source)) {
                return instance.convert(source);
            }
        }
        throw new IllegalArgumentException("不支持的数据类型: " + source.getClass().getName());
    }
}
