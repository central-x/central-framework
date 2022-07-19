package central.data;

/**
 * Versional Entity
 * 版本化的实体
 *
 * @author Alan Yeh
 * @since 2022/07/10
 */
public interface Versional {
    /**
     * 获取实体版本号
     */
    Integer getVersion();

    /**
     * 设置实体版本号
     *
     * @param version 版本号
     */
    void setVersion(Integer version);

    /**
     * 增长版本号
     */
    default void increaseVersion() {
        if (this.getVersion() == null) {
            this.setVersion(1);
        } else {
            this.setVersion(this.getVersion() + 1);
        }
    }
}
