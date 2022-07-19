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
        if (instance instanceof LifeCycle cycle) {
            cycle.created();
        }
    }

    @Override
    public void afterPropertySet(InstanceReference<?> instance) {
        if (instance instanceof LifeCycle cycle) {
            cycle.initialized();
        }
    }

    @Override
    public void beforeDestroy(InstanceReference<?> instance) {
        if (instance instanceof LifeCycle cycle) {
            cycle.destroy();
        }
    }
}
