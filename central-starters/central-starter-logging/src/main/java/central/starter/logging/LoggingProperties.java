package central.starter.logging;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Logging Properties
 *
 * @author Alan Yeh
 * @since 2022/07/17
 */
@Data
@ConfigurationProperties(prefix = "central.logging")
public class LoggingProperties {
}
