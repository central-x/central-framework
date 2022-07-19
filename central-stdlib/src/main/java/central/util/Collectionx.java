package central.util;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * Collection 工具
 *
 * @author Alan Yeh
 * @since 2022/07/12
 */
public class Collectionx {
    /**
     * 判断集合是否为空
     *
     * @param collection 集合
     * @param <T>        元素类型
     */
    public static <T> boolean isNullOrEmpty(@Nullable Collection<T> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * 判断集合是否不为空
     *
     * @param collection 集合
     * @param <T>        元素类型
     */
    public static <T> boolean isNotEmpty(@Nullable Collection<T> collection) {
        return collection != null && !collection.isEmpty();
    }

    /**
     * 获取第一个元素
     *
     * @param collection 集合
     * @param <T>        元素类型
     */
    public static <T> T getFirst(@Nullable Collection<T> collection) {
        return isNullOrEmpty(collection) ? null : collection.iterator().next();
    }
}
