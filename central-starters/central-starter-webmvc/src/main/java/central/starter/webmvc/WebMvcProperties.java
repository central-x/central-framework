package central.starter.webmvc;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * WebMvc Properties
 *
 * @author Alan Yeh
 * @since 2022/07/15
 */
@Data
@Validated
@ConfigurationProperties(prefix = "central.webmvc")
public class WebMvcProperties {
}
