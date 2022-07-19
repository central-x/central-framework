package central.util.function;

/**
 * 三员消费者
 *
 * @author Alan Yeh
 * @since 2022/07/12
 */
@FunctionalInterface
public interface TriConsumer <T, U, R>{
    /**
     * 使用指定的参数执行方法
     * @param t 第一个参数
     * @param u 第二个参数
     * @param r 第三个参数
     */
    void accept(T t, U u, R r);
}
