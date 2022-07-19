package central.net.http.body.converter;

import central.data.OptionalEnum;
import central.net.http.body.HttpConverter;

import javax.annotation.Nullable;

/**
 * OptionalEnum 转换器
 *
 * @author Alan Yeh
 * @since 2022/07/14
 */
public class OptionalEnumConverter implements HttpConverter<OptionalEnum<?>> {
    @Override
    public boolean support(@Nullable Object source) {
        return source instanceof OptionalEnum<?>;
    }

    @Override
    public String convert(OptionalEnum<?> source) {
        return source.getValue().toString();
    }
}
