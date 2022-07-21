/*
 * MIT License
 *
 * Copyright (c) 2022-present Alan Yeh <alan@yeh.cn>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package central.pattern.chain;

import java.util.List;

/**
 * 处理责任链
 *
 * @author Alan Yeh
 * @since 2022/07/14
 */
public class ProcessChain<T, R> {

    /**
     * 处理链
     */
    private final List<? extends Processor<T, R>> processors;

    /**
     * 当前执行的下标
     */
    private final int index;

    public ProcessChain(List<? extends Processor<T, R>> processors) {
        this.processors = processors;
        this.index = 0;
    }

    public ProcessChain(ProcessChain<T, R> parent, int index) {
        this.processors = parent.processors;
        this.index = index;
    }

    public R process(T target) throws Exception {
        if (this.index < this.processors.size()) {
            var processor = this.processors.get(this.index);
            var next = new ProcessChain<>(this, this.index + 1);
            if (processor.predicate(target)) {
                // 断言成功，则执行处理器
                return processor.process(target, next);
            } else {
                // 断言不成功，则直接执行下一个处理器
                return next.process(target);
            }
        }
        return null;
    }
}
