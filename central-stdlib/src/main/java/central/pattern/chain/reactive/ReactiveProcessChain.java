package central.pattern.chain.reactive;

import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 处理责任链
 *
 * @author Alan Yeh
 * @since 2022/07/14
 */
public class ReactiveProcessChain<T, R> {

    /**
     * 处理链
     */
    private final List<ReactiveProcessor<T, R>> processors;

    /**
     * 当前执行的下标
     */
    private final int index;

    public ReactiveProcessChain(List<ReactiveProcessor<T, R>> processors) {
        this.processors = processors;
        this.index = 0;
    }

    public ReactiveProcessChain(ReactiveProcessChain<T, R> parent, int index) {
        this.processors = parent.processors;
        this.index = index;
    }

    public Mono<R> process(T target){
        return Mono.defer(() -> {
            if (this.index < this.processors.size()){
                var processor = this.processors.get(this.index);
                var next = new ReactiveProcessChain<>(this, this.index + 1);
                if (processor.predicate(target)){
                    // 断言成功，则执行处理器
                    return processor.process(target, next);
                } else {
                    // 断言不成功，则直接执行下一个处理器
                    return next.process(target);
                }
            }
            return Mono.empty();
        });
    }
}
