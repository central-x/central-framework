package central.util.function;

import central.util.Assertx;
import lombok.SneakyThrows;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Throwable Supplier
 *
 * @author Alan Yeh
 * @since 2022/07/11
 */
@FunctionalInterface
public interface ThrowableSupplier<V, E extends Exception> {
    static <V, E extends Exception> ThrowableSupplier<V, E> of(ThrowableSupplier<V, E> supplier) {
        return supplier;
    }

    /**
     * 获取值
     */
    V get() throws E;

    /**
     * 将本函数的返回值作为下一个函数的参数
     *
     * @param after 下一个函数
     * @param <R>   下一个函数的返回值类型
     * @param <RE>  下一个函数可能抛出的异常
     * @return 被包装后的函数
     */
    default <R, RE extends Exception> ThrowableSupplier<R, RE> andThen(ThrowableFunction<? super V, ? extends R, RE> after) {
        Assertx.mustNotNull(after, "Argument 'after' must not null");

        return () -> after.apply(sneakThrows().get());
    }

    /**
     * 忽略异常
     * 当出现异常时，将返回 null
     */
    default Supplier<V> ignoreThrows() {
        final var that = this;
        return () -> {
            try {
                return that.get();
            } catch (Exception ignored) {
                return null;
            }
        };
    }

    /**
     * 隐匿异常
     *
     * @return 被包装后的函数
     */
    default Supplier<V> sneakThrows() {
        final var that = this;
        return new Supplier<V>() {
            @Override
            @SneakyThrows
            public V get() {
                return that.get();
            }
        };
    }

    /**
     * 处理异常，返回异常处理器的结果
     *
     * @param handler 异常处理器
     */
    default Supplier<V> catchThrows(Supplier<V> handler) {
        return () -> {
            try {
                return get();
            } catch (Exception ignored) {
                return handler.get();
            }
        };
    }

    /**
     * 处理异常，返回异常处理器的结果
     *
     * @param handler 异常处理器，第一个参数是异常
     */
    default Supplier<V> catchThrows(Function<E, V> handler) {
        return () -> {
            try {
                return get();
            } catch (Exception cause) {
                return handler.apply((E) cause);
            }
        };
    }
}
