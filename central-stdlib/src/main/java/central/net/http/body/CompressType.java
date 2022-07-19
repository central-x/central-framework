package central.net.http.body;

import central.data.OptionalEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 压缩类型
 * @author Alan Yeh
 * @since 2022/07/14
 */
@Getter
@AllArgsConstructor
public enum CompressType implements OptionalEnum<String> {
    /**
     * GZIP
     */
    GZIP("GZIP", "gzip"),
    /**
     * DEFLATE
     */
    DEFLATE("DEFLATE", "deflate");

    private final String name;
    private final String value;
}
