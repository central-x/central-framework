package central.util;

import central.lang.reflect.TypeReference;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 对象工具
 *
 * @author Alan Yeh
 * @since 2022/07/05
 */
public class Objectx {

    /**
     * 判断对象是否为空
     *
     * @param obj 待判断对象
     */
    public static boolean isNull(@Nullable Object obj) {
        return obj == null;
    }

    /**
     * 判断对象是否不为空
     *
     * @param obj 待判断对象
     */
    public static boolean isNotNull(@Nullable Object obj) {
        return obj != null;
    }

    /**
     * 调用 toString，如果 obj 为空，返回空
     */
    @Nullable
    public static String toString(@Nullable Object obj) {
        return obj == null ? null : obj.toString();
    }

    /**
     * 获取值
     *
     * @param value    可空值
     * @param fallback 取值失败时的储备
     * @param <T>      类型
     */
    public static @Nonnull <T> T get(@Nullable T value, @Nonnull T fallback) {
        if (value instanceof String string) {
            if (Stringx.isNullOrEmpty(string)) {
                return fallback;
            } else {
                return value;
            }
        } else if (value instanceof Collection<?> collection) {
            if (Collectionx.isNullOrEmpty(collection)) {
                return fallback;
            } else {
                return value;
            }
        } else {
            return value == null ? fallback : value;
        }
    }

    /**
     * 获取值
     *
     * @param value    可空值
     * @param supplier 如果值为空时，通过 supplier 获取
     * @param <T>      类型
     */
    public static @Nonnull <T> T get(@Nullable T value, @Nonnull Supplier<T> supplier) {
        Assertx.mustNotNull(supplier, "Argument 'supplier' must not null");
        if (value instanceof String string) {
            if (Stringx.isNullOrEmpty(string)) {
                return supplier.get();
            } else {
                return value;
            }
        } else if (value instanceof Collection<?> collection) {
            if (Collectionx.isNullOrEmpty(collection)) {
                return supplier.get();
            } else {
                return value;
            }
        } else {
            return value == null ? supplier.get() : value;
        }
    }


    /**
     * 根据传入的对象转成 Map
     */
    public static Map<String, Object> toMap(@Nullable Object obj) {
        if (obj == null) {
            return Mapx.newHashMap();
        }
        return Jsonx.Default().deserialize(Jsonx.Default().serialize(obj), TypeReference.forMapType(String.class, Object.class));
    }

    /**
     * List 数组对象转 {@code List<Map>}
     */
    public static List<Map<String, Object>> toList(List<?> list) {
        return Listx.asStream(list).map(Objectx::toMap).collect(Collectors.toList());
    }

    /**
     * 获取类型
     *
     * @param instance 获取对象类型
     * @return 对象的类型
     */
    public static Class<?> getClass(@Nullable Object instance) {
        if (instance == null) {
            return null;
        } else {
            return instance.getClass();
        }
    }
}
