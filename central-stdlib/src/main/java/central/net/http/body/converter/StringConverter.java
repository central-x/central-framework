package central.net.http.body.converter;

import central.net.http.body.HttpConverter;

import javax.annotation.Nullable;

/**
 * 字符串转换器
 *
 * @author Alan Yeh
 * @since 2022/07/14
 */
public class StringConverter implements HttpConverter<String> {
    @Override
    public boolean support(@Nullable Object source) {
        return source instanceof String;
    }

    @Override
    public String convert(String source) {
        return source;
    }
}
