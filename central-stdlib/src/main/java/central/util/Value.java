package central.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;
import java.io.Serial;
import java.io.Serializable;

/**
 * Value
 *
 * @author Alan Yeh
 * @since 2022/07/17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Value<T extends Serializable> implements Serializable {
    @Serial
    private static final long serialVersionUID = 3043802081813135212L;

    @Nullable
    private volatile T value;

    /**
     * 获取当前的值
     */
    @Nullable
    public T get() {
        return this.value;
    }

    /**
     * 获取值，如果当前的值为空，则返回 other
     *
     * @param other 另一个值
     */
    public T get(T other) {
        return isNotNull() ? value : other;
    }

    /**
     * 判断当前的值是否为 null
     */
    public boolean isNull() {
        return this.value == null;
    }

    /**
     * 判断当前的值是否不为 null
     */
    public boolean isNotNull() {
        return this.value != null;
    }

    /**
     * 快速创建 Value 对象
     *
     * @param value 值
     * @param <T>   值类型
     */
    public static <T extends Serializable> Value<T> of(@Nullable T value) {
        return new Value<>(value);
    }
}
