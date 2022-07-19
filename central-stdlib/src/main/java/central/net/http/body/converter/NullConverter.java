package central.net.http.body.converter;

import central.net.http.body.HttpConverter;

import javax.annotation.Nullable;

/**
 * 空转换器
 *
 * @author Alan Yeh
 * @since 2022/07/14
 */
public class NullConverter implements HttpConverter<Object> {

    @Override
    public boolean support(@Nullable Object source) {
        return source == null;
    }

    @Override
    public String convert(Object source) {
        return "";
    }
}
