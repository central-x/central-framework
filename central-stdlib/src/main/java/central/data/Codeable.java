package central.data;

/**
 * Codeable Entity
 * 可设置标识实体
 *
 * @author Alan Yeh
 * @since 2022/07/05
 */
public interface Codeable {
    /**
     * 获取标识
     */
    String getCode();

    /**
     * 设置标识
     *
     * @param code 标识
     */
    void setCode(String code);
}
