package central.data;

/**
 * Deletable Entity
 * 可标志为已删除（软删除）
 *
 * @author Alan Yeh
 * @since 2022/07/10
 */
public interface Deletable {

    /**
     * 获取删除状态
     */
    Boolean getDeleted();

    /**
     * 设置删除状态
     *
     * @param deleted 是否已删除
     */
    void setDeleted(Boolean deleted);
}
