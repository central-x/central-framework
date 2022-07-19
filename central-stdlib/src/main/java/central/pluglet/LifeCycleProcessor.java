package central.pluglet;

import central.lang.reflect.InstanceReference;

/**
 * 处理实例生命周期
 *
 * @author Alan Yeh
 * @since 2022/07/13
 */
public interface LifeCycleProcessor {
    /**
     * 实例刚被创建
     */
    default void afterCreated(InstanceReference<?> instance) {
    }

    /**
     * 实例的属性已初始化
     */
    default void afterPropertySet(InstanceReference<?> instance) {
    }

    /**
     * 实例将被销毁
     */
    default void beforeDestroy(InstanceReference<?> instance) {
    }
}
