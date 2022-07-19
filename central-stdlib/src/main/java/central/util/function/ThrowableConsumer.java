package central.util.function;

import lombok.SneakyThrows;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Throwable Consumer
 *
 * @author Alan Yeh
 * @since 2022/07/11
 */
@FunctionalInterface
public interface ThrowableConsumer<T, E extends Exception> {

    static <T, E extends Exception> ThrowableConsumer<T, E> of(ThrowableConsumer<T, E> consumer) {
        return consumer;
    }

    /**
     * 消费
     *
     * @param t 入参
     */
    void accept(T t) throws E;

    /**
     * 忽略异常
     */
    default Consumer<T> ignoreThrows() {
        final var that = this;
        return (T t) -> {
            try {
                that.accept(t);
            } catch (Exception ignored) {
            }
        };
    }

    /**
     * 隐匿异常
     *
     * @return 被包装后的函数
     */
    default Consumer<T> sneakThrows() {
        final var that = this;
        return new Consumer<T>() {
            @Override
            @SneakyThrows
            public void accept(T t) {
                that.accept(t);
            }
        };
    }

    /**
     * 处理异常，返回异常处理器的结果
     *
     * @param handler 异常处理器
     * @return 被包装后的函数
     */
    default Consumer<T> catchThrows(Consumer<E> handler) {
        return (T t) -> {
            try {
                accept(t);
            } catch (Exception cause) {
                handler.accept((E) cause);
            }
        };
    }

    /**
     * 处理异常，返回异常处理器的结果
     *
     * @param handler 异常处理器
     * @return 被包装后的函数
     */
    default Consumer<T> catchThrows(BiConsumer<T, E> handler) {
        return (T t) -> {
            try {
                accept(t);
            } catch (Exception cause) {
                handler.accept(t, (E) cause);
            }
        };
    }
}
