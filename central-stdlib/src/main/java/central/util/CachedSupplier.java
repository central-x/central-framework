package central.util;

import java.io.Serializable;
import java.util.function.Supplier;

/**
 * 带缓存的 Supplier
 *
 * @author Alan Yeh
 * @since 2022/07/13
 */
public class CachedSupplier<T extends Serializable> implements Supplier<T> {
    /**
     * 缓存时间
     * 如果 timout < 0，则获取一次后永远不再更新
     * 如果 timeout == 0，则每次都从 supplier 中重新获取新的值
     * 如果 timeout > 0，则在 timeout 过期前不再获取新值，过期后重新从 supplier 中获取新值
     */
    private final long timeout;
    /**
     * 缓存过期过，从 Supplier 中获取新的值
     */
    private final Supplier<T> supplier;
    /**
     * 上一次更新值的时间戳
     */
    private volatile long last = 0;
    /**
     * 已缓存的值
     */
    private volatile Value<T> value;

    public CachedSupplier(long timeout, Supplier<T> supplier) {
        this.timeout = timeout;
        this.supplier = supplier;
    }

    /**
     * 获取值
     */
    @Override
    public T get() {
        if (value == null) {
            // 还没获取值，因此需要先获取值
            synchronized (this) {
                if (value == null) {
                    value = Value.of(supplier.get());
                    last = System.currentTimeMillis();
                }
            }
            return value.get();
        } else {
            if (timeout < 0) {
                // 永不更新
                return value.get();
            } else if (timeout == 0) {
                // 永不缓存
                return supplier.get();
            } else if (System.currentTimeMillis() - this.last > this.timeout) {
                // 缓存失效
                synchronized (this) {
                    if (System.currentTimeMillis() - this.last > this.timeout) {
                        value = Value.of(supplier.get());
                        last = System.currentTimeMillis();
                    }
                }
                return value.get();
            } else {
                // 缓存未失效
                return value.get();
            }
        }
    }

    /**
     * 清除已缓存的值
     */
    public void clear() {
        synchronized (this) {
            this.value = null;
            this.last = 0;
        }
    }
}
