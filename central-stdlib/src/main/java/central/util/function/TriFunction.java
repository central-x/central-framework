package central.util.function;

import java.util.Objects;
import java.util.function.Function;

/**
 * 三员参数函数
 *
 * @author Alan Yeh
 * @since 2022/07/12
 */
@FunctionalInterface
public interface TriFunction<T, U, K, R> {

    /**
     * 使用指定参数执行函数
     *
     * @param t 第一个参数
     * @param u 第二个参数
     * @param k 第三个参数
     * @return 涵数执行结果
     */
    R apply(T t, U u, K k);

    /**
     * 将本函数的执行结果作为下一个函数的入参
     *
     * @param after 下一个函数
     * @param <V>   下一个函数返回值类型
     * @return 被包装后的函数
     */
    default <V> TriFunction<T, U, K, V> andThen(Function<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        return (T t, U u, K k) -> after.apply(apply(t, u, k));
    }
}
