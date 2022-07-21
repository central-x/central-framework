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

package central.lang.reflect;

import central.util.LazyValue;
import lombok.Getter;
import lombok.SneakyThrows;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * Field Reference
 *
 * @author Alan Yeh
 * @since 2022/07/12
 */
public class FieldReference {
    /**
     * 字段
     */
    @Getter
    private final Field field;

    /**
     * 字段名称
     */
    public String getName() {
        return this.field.getName();
    }

    private final LazyValue<TypeReference<?>> type = new LazyValue<>(() -> TypeReference.of(getField().getGenericType()));

    /**
     * 字段类型
     */
    public TypeReference<?> getType() {
        return this.type.get();
    }

    /**
     * 获取字段注解
     *
     * @param annotation 注解类
     * @param <T>        注解类型
     */
    public <T extends Annotation> T getAnnotation(Class<T> annotation) {
        return this.field.getAnnotation(annotation);
    }

    /**
     * 为字段赋值
     *
     * @param target 待赋值对象
     * @param value  值
     */
    @SneakyThrows
    public void setValue(@Nonnull InstanceReference<?> target, @Nullable Object value) {
        this.field.setAccessible(true);
        this.field.set(target.getInstance(), value);
    }

    private FieldReference(Field field) {
        this.field = field;
    }

    public static FieldReference of(Field field) {
        return new FieldReference(field);
    }
}
