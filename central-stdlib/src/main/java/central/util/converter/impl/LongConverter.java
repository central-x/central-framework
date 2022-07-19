package central.util.converter.impl;

import central.util.converter.ConvertException;
import central.util.converter.Converter;

import java.util.Date;

/**
 * Long Converter
 *
 * @author Alan Yeh
 * @since 2022/07/11
 */
public class LongConverter implements Converter<Long> {
    @Override
    public boolean support(Class<?> source) {
        if (Number.class.isAssignableFrom(source)) {
            return true;
        } else if (Date.class.isAssignableFrom(source)) {
            return true;
        } else if (String.class.isAssignableFrom(source)) {
            return true;
        }

        return false;
    }

    @Override
    public Long convert(Object source) {
        if (source instanceof Number n) {
            return n.longValue();
        } else if (source instanceof Date d) {
            return d.getTime();
        } else if (source instanceof String s) {
            return Long.parseLong(s);
        }

        throw new ConvertException(source, Long.class);
    }
}
