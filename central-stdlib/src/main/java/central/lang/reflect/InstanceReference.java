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

import central.bean.InitializeException;
import lombok.Getter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Instance Reference
 * 实例引用
 *
 * @author Alan Yeh
 * @since 2022/07/13
 */
public abstract class InstanceReference<T> {
    @Getter(onMethod_ = @Nonnull)
    private final TypeReference<T> type;

    @Getter(onMethod_ = @Nullable)
    private final T instance;

    protected InstanceReference(@Nullable T instance) {
        this(null, instance);
    }

    protected InstanceReference(@Nullable TypeReference<T> type, @Nullable T instance) {
        if (type == null) {
            var superClass = (ParameterizedType) this.getClass().getGenericSuperclass();
            Type actualType = superClass.getActualTypeArguments()[0];
            if ("T".equals(actualType.getTypeName())) {
                // 获取类型失败
                if (instance != null) {
                    this.type = (TypeReference<T>) TypeReference.of(instance.getClass());
                } else {
                    throw new InitializeException("无法获取类型");
                }
            } else {
                this.type = TypeReference.of(actualType);
            }
        } else {
            this.type = type;
        }
        this.instance = instance;
    }

    public static <T> InstanceReference<T> of(@Nonnull TypeReference<T> type, @Nullable T instance) {
        return new InstanceReference<T>(type, instance) {
        };
    }

    public static <T> InstanceReference<T> of(@Nonnull T instance) {
        return new InstanceReference<>(instance) {
        };
    }

    /**
     * 实例是否为空
     */
    public boolean isNull() {
        return this.instance == null;
    }

    /**
     * 是否不为空
     */
    public boolean isNotNull() {
        return this.instance != null;
    }

//    /**
//     * 获取类型
//     */
//    public TypeReference<T> getType(){
//        return this.isNull() ? null : TypeReference.forInstance(this.instance);
//    }
}
