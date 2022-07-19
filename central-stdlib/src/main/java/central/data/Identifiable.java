package central.data;

import javax.annotation.Nonnull;

/**
 * Identifiable Entity
 * 可唯一标识的实体
 *
 * @author Alan Yeh
 * @since 2022/07/10
 */
public interface Identifiable {

    /**
     * 获取唯一标识
     */
    @Nonnull String getId();

    /**
     * 设置唯一标识
     *
     * @param id 唯一标识
     */
    void setId(@Nonnull String id);
}
