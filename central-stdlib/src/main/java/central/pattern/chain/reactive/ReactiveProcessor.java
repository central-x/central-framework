package central.pattern.chain.reactive;

import reactor.core.publisher.Mono;

/**
 * 处理器
 *
 * @author Alan Yeh
 * @since 2022/07/14
 */
public interface ReactiveProcessor<T, R> {
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
    Mono<R> process(T target, ReactiveProcessChain<T, R> chain);
}
