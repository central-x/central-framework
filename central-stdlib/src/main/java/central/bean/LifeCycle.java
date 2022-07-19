package central.bean;

/**
 * Bean Life Cycle
 *
 * @author Alan Yeh
 * @since 2022/07/11
 */
public interface LifeCycle {
    /**
     * 实例刚被创建
     */
    default void created() {
    }

    /**
     * 实例的字段已初始化
     */
    default void initialized() {
    }

    /**
     * 实例被销毁
     */
    default void destroy() {
    }
}
