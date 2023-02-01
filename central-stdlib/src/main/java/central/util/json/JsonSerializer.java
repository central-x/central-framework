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

package central.util.json;

import central.lang.reflect.TypeRef;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Json 序列化工具
 *
 * @author Alan Yeh
 * @since 2022/07/05
 */
public interface JsonSerializer {
    /**
     * 将对象序列化成 Json
     *
     * @param obj 对象
     */
    default String serialize(Object obj) {
        return this.serialize(obj, false);
    }

    /**
     * 将对象序列化成 JSON
     *
     * @param obj       对象
     * @param formatted 是否格式化
     */
    default String serialize(Object obj, boolean formatted) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        this.serialize(output, StandardCharsets.UTF_8, obj, formatted);
        return output.toString(StandardCharsets.UTF_8);
    }

    /**
     * 将对象序列化成 JSON
     *
     * @param output    序列化输出流
     * @param charset   字符集
     * @param obj       对象
     * @param formatted 是否格式化
     */
    void serialize(OutputStream output, Charset charset, Object obj, boolean formatted);

    /**
     * 反序列化
     *
     * @param json      Json 字符串
     * @param reference 类型引用
     */
    default <T> T deserialize(String json, TypeRef<T> reference) {
        return this.deserialize(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8, reference);
    }

    /**
     * 反序列化
     *
     * @param json Json 字符串
     * @param type 类型
     */
    default <T> T deserialize(String json, Class<T> type) {
        return this.deserialize(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8, TypeRef.of(type));
    }

    /**
     * 将 JSON 字节码反序列化成对象
     *
     * @param input     从输入流中序列化
     * @param charset   字符集
     * @param reference 类型引用
     */
    <T> T deserialize(InputStream input, Charset charset, TypeRef<T> reference);
}
