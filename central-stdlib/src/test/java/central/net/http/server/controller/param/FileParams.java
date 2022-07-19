package central.net.http.server.controller.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * 上传文件
 *
 * @author Alan Yeh
 * @since 2022/07/18
 */
@Data
public class FileParams {
    @NotNull(message = "文件[file]必须不为空")
    private MultipartFile file;
}
