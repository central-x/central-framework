package central.pattern.chain;

/**
 * 处理器
 *
 * @author Alan Yeh
 * @since 2022/07/14
 */
public interface Processor<T, R> {
    /**
     * 判断是否处理
     *
     * @param target 待处理对象
     */
    default boolean predicate(T target) {
        return true;
    }

    /**
     * 处理
     *
     * @param target 待处理对象
     * @param chain  下一处理链
     */
    R process(T target, ProcessChain<T, R> chain) throws Exception;
}
