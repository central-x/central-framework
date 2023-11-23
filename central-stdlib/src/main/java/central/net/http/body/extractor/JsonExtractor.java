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

package central.net.http.body.extractor;

import central.lang.reflect.TypeRef;
import central.net.http.body.Body;
import central.net.http.body.BodyExtractor;
import central.util.Jsonx;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * 将响应体解析成对象（JSON）
 *
 * @author Alan Yeh
 * @since 2022/07/17
 */
public class JsonExtractor<T> implements BodyExtractor<T> {
    private final Charset charset;
    private final TypeRef<T> type;

    /**
     * 构造函数
     *
     * @param type    对象类型
     * @param charset 字符集（如果未定指，将使用响应头里指定的字符集）
     */
    public JsonExtractor(@Nonnull TypeRef<T> type, @Nullable Charset charset) {
        this.charset = charset;
        this.type = type;
    }

    /**
     * 创建 JSON 解析器
     *
     * @param type 对象类型
     */
    public static <T> JsonExtractor<T> of(TypeRef<T> type) {
        return new JsonExtractor<>(type, null);
    }

    /**
     * 创建 JSON 解析器
     *
     * @param type    对象类型
     * @param charset 字符集
     */
    public static <T> JsonExtractor<T> of(TypeRef<T> type, Charset charset) {
        return new JsonExtractor<>(type, null);
    }

    @Override
    public T extract(Body body) throws IOException {
        var charset = this.charset;
        if (charset == null) {
            // 如果开发者没有指定字符集，则尝试从响应头获取字符集信息
            charset = body.getContentType().getCharset();
        }
        if (charset == null) {
            // 如果响应头没有指定字符集，则默认使用 UTF-8
            charset = StandardCharsets.UTF_8;
        }

        return Jsonx.Default().deserialize(body.getInputStream(), charset, this.type);
    }
}
