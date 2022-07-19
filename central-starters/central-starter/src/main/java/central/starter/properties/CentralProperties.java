package central.starter.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 基础配置
 *
 * @author Alan Yeh
 * @since 2022/07/16
 */
@Data
@ConfigurationProperties(prefix = "central")
public class CentralProperties {
}
