package central.net.http.server.controller.data;

import lombok.Data;

/**
 * 文件信息
 *
 * @author Alan Yeh
 * @since 2022/07/15
 */
@Data
public class Upload {

    private String name;

    private String originalFilename;

    private long size;

    private String contentType;
}
