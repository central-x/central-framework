package central.data;

/**
 * Tenantable Entity
 * 可划分租户的数据
 *
 * @author Alan Yeh
 * @since 2022/07/10
 */
public interface Tenantable {
    /**
     * 获取租户标识
     */
    String getTenantCode();

    /**
     * 设置租户标识
     *
     * @param tenantCode 租户标识
     */
    void setTenantCode(String tenantCode);
}
