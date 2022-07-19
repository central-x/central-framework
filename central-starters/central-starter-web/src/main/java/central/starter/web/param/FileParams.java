package central.starter.web.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serial;
import java.io.Serializable;

/**
 * 上传文件
 *
 * @author Alan Yeh
 * @since 2022/07/15
 */
@Data
public class FileParams implements Serializable {
    @Serial
    private static final long serialVersionUID = 1246745196976725735L;

    /**
     * 文件参数
     */
    @NotNull(message = "文件[file]必须不为空")
    private MultipartFile file;
}
