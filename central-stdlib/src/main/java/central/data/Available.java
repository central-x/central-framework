package central.data;

/**
 * Available Entity
 * 启用/禁用
 *
 * @author Alan Yeh
 * @since 2022/07/05
 */
public interface Available {
    /**
     * 获取启用/禁用状态
     */
    Boolean getEnabled();

    /**
     * 设置启用/禁用状态
     *
     * @param enabled 启用/禁用状态
     */
    void setEnabled(Boolean enabled);
}
