package central.data;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 数据实体
 *
 * @author Alan Yeh
 * @since 2022/07/10
 */
@Data
public class Entity implements Identifiable, Serializable {
    @Serial
    private static final long serialVersionUID = 3246576373725082312L;

    /**
     * 唯一标识
     */
    private String id;

    /**
     * 创建人唯一标识
     */
    private String creatorId;

    /**
     * 实体创建时间
     */
    private Timestamp createDate;

    /**
     * 初始化实体的创建人唯一标识与创建时间
     *
     * @param creatorId 创建人唯一标识
     */
    public void updateCreator(String creatorId) {
        this.creatorId = creatorId;
        this.createDate = new Timestamp(System.currentTimeMillis());
    }
}
