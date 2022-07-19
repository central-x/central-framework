package central.net.http.body.converter;

import central.net.http.body.HttpConverter;

import javax.annotation.Nullable;

/**
 * Number 转换器
 *
 * @author Alan Yeh
 * @since 2022/07/14
 */
public class NumberConverter implements HttpConverter<Number> {
    @Override
    public boolean support(@Nullable Object source) {
        return source instanceof Number;
    }

    @Override
    public String convert(Number source) {
        return source.toString();
    }
}
