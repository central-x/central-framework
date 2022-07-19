package central.net.http.body.converter;

import central.net.http.body.HttpConverter;

import javax.annotation.Nullable;

/**
 * Boolean 转换器
 *
 * @author Alan Yeh
 * @since 2022/07/14
 */
public class BooleanConverter implements HttpConverter<Boolean> {
    @Override
    public boolean support(@Nullable Object source) {
        return source instanceof Boolean;
    }

    @Override
    public String convert(Boolean source) {
        return Boolean.toString(source);
    }
}
