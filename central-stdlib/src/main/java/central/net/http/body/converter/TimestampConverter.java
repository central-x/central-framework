package central.net.http.body.converter;

import central.net.http.body.HttpConverter;

import javax.annotation.Nullable;
import java.sql.Timestamp;

/**
 * Timestamp 转换器
 *
 * @author Alan Yeh
 * @since 2022/07/14
 */
public class TimestampConverter implements HttpConverter<Timestamp> {
    @Override
    public boolean support(@Nullable Object source) {
        return source instanceof Timestamp;
    }

    @Override
    public String convert(Timestamp source) {
        return String.valueOf(source.getTime());
    }
}
