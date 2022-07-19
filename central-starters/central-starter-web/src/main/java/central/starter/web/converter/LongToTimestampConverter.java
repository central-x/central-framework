package central.starter.web.converter;

import org.springframework.core.convert.converter.Converter;

import javax.annotation.Nonnull;
import java.sql.Timestamp;

/**
 * 日期转换工具
 *
 * @author Alan Yeh
 * @since 2022/07/15
 */
public class LongToTimestampConverter implements Converter<Long, Timestamp> {

    @Override
    public Timestamp convert(@Nonnull Long source) {
        return new Timestamp(source);
    }
}
