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
