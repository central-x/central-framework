package central.data;

import java.sql.Timestamp;

/**
 * Modifiable Entity
 * 可修改的实体
 *
 * @author Alan Yeh
 * @since 2022/07/10
 */
public interface Modifiable {
    /**
     * 获取修改人唯一标识
     */
    String getModifierId();

    /**
     * 设置修改人唯一标识
     *
     * @param modifierId 修改人唯一标识
     */
    void setModifierId(String modifierId);

    /**
     * 获取修改日期
     */
    Timestamp getModifyDate();

    /**
     * 设置修改日期
     *
     * @param modifyDate 修改日期
     */
    void setModifyDate(Timestamp modifyDate);

    /**
     * 更新实体修改人唯一标识和修改时间
     *
     * @param modifierId 修改人唯一标识
     */
    default void updateModifier(String modifierId) {
        this.setModifierId(modifierId);
        this.setModifyDate(new Timestamp(System.currentTimeMillis()));
    }
}
