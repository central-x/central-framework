package central.starter.web.query;

import central.util.Arrayx;
import central.util.Stringx;
import lombok.Data;

import javax.annotation.Nonnull;
import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 模板查询
 *
 * @author Alan Yeh
 * @since 2022/07/15
 */
@Data
public class KeywordQuery implements Serializable {
    @Serial
    private static final long serialVersionUID = -1863820286887438610L;

    /**
     * 通过模糊查询
     * 使用空格分隔多个关键字
     */
    private String keyword;

    public @Nonnull List<String> getKeywords() {
        if (Stringx.isNullOrBlank(this.keyword)) {
            return Collections.emptyList();
        } else {
            return Arrayx.asStream((keyword.trim().split("[ ]")))
                    .map(String::trim)
                    .filter(Stringx::isNotBlank)
                    .map(it -> "%" + it + "%")
                    .toList();
        }
    }
}
