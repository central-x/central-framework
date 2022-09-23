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

package central.pluglet.lifecycle;

import central.bean.LifeCycle;
import central.lang.reflect.InstanceReference;
import central.pluglet.LifeCycleProcessor;

/**
 * 处理实现了 LifeCycle 接口的实体
 *
 * @author Alan Yeh
 * @since 2022/07/13
 */
public class NormalLifeCycleProcessor implements LifeCycleProcessor {

    @Override
    public void afterCreated(InstanceReference<?> instance) {
        if (instance.getInstance() instanceof LifeCycle cycle) {
            cycle.created();
        }
    }

    @Override
    public void afterPropertySet(InstanceReference<?> instance) {
        if (instance.getInstance() instanceof LifeCycle cycle) {
            cycle.initialized();
        }
    }

    @Override
    public void beforeDestroy(InstanceReference<?> instance) {
        if (instance.getInstance() instanceof LifeCycle cycle) {
            cycle.destroy();
        }
    }
}
