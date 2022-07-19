package central.util;

import central.lang.reflect.InstanceReference;

import java.util.function.Supplier;

/**
 * 延迟初始化
 *
 * @author Alan Yeh
 * @since 2022/07/13
 */
public class LazyValue<T> {
    private volatile InstanceReference<T> instance;

    private final Supplier<T> supplier;

    public LazyValue(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public T get() {
        if (this.instance == null) {
            synchronized (this) {
                if (this.instance == null) {
                    this.instance = InstanceReference.of(supplier.get());
                }
            }
        }
        return this.instance.getInstance();
    }
}
