package central.lang.reflect;

import central.util.function.ThrowableFunction;
import lombok.SneakyThrows;

/**
 * 调用并隐藏异常
 *
 * @author Alan Yeh
 * @since 2022/07/12
 */
public abstract class SneakyInvocation {

    public abstract Object invoke();

    public static <T, R, E extends Exception> SneakyInvocation of(ThrowableFunction<T, R, E> function, T t){
        return new SneakyInvocation() {
            @Override
            @SneakyThrows
            public Object invoke() {
                return function.apply(t);
            }
        };
    }
}
