package central.util.function;

import central.util.Assertx;
import lombok.SneakyThrows;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Throwable Function
 *
 * @author Alan Yeh
 * @since 2022/07/11
 */
@FunctionalInterface
public interface ThrowableFunction<T, R, E extends Exception> {
    static <T, R, E extends Exception> ThrowableFunction<T, R, E> of(ThrowableFunction<T, R, E> function) {
        return function;
    }

    /**
     * 调用方法
     */
    R apply(T t) throws E;

    /**
     * 将本函数的返回值作为下一个函数的参数
     *
     * @param after 下一个函数
     * @param <V>   下一个函数的返回值类型
     * @param <RE>  下一个函数可能抛出的异常
     * @return 被包装后的函数
     */
    default <V, RE extends Exception> ThrowableFunction<T, V, RE> andThen(ThrowableFunction<? super R, ? extends V, RE> after) {
        Assertx.mustNotNull("after", after);

        return (T t) -> after.apply(sneakThrows().apply(t));
    }

    /**
     * 将本函数的返回值作为下一个函数的参数
     *
     * @param after 下一个函数
     * @param <RE>  下一个函数可能抛出的异常
     * @return 被包装后的函数
     */
    default <RE extends Exception> ThrowableConsumer<T, RE> andThen(ThrowableConsumer<? super R, RE> after) {
        Assertx.mustNotNull("after", after);

        return (T t) -> after.accept(sneakThrows().apply(t));
    }

    /**
     * 忽略异常
     *
     * @return 被包装后的函数
     */
    default Function<T, R> ignoreThrows() {
        return (T t) -> {
            try {
                return apply(t);
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
    default Function<T, R> sneakThrows() {
        final var that = this;
        return new Function<T, R>() {
            @Override
            @SneakyThrows
            public R apply(T t) {
                return that.apply(t);
            }
        };
    }

    /**
     * 处理异常，返回异常处理器的结果
     *
     * @param handler 异常处理器，第一个参数是异常信息
     * @return 被包装后的函数
     */
    default Function<T, R> catchThrows(Function<E, R> handler) {
        return (T t) -> {
            try {
                return apply(t);
            } catch (Exception cause) {
                return handler.apply((E) cause);
            }
        };
    }

    /**
     * 处理异常，返回异常处理器的结果
     *
     * @param handler 异常处理器，第一个参数是原调用参数，第二个参数是异常信息
     * @return 被包装后的函数
     */
    default Function<T, R> catchThrows(BiFunction<T, E, R> handler) {
        return (T t) -> {
            try {
                return apply(t);
            } catch (Exception cause) {
                return handler.apply(t, (E) cause);
            }
        };
    }
}
