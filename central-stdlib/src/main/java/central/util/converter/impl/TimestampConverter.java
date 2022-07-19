package central.util.converter.impl;

import central.util.converter.ConvertException;
import central.util.converter.Converter;

import java.sql.Timestamp;

/**
 * Timestamp Converter
 *
 * @author Alan Yeh
 * @since 2022/07/10
 */
public class TimestampConverter implements Converter<Timestamp> {
    @Override
    public boolean support(Class<?> source) {
        if (Timestamp.class.isAssignableFrom(source)) {
            return true;
        } else if (Long.class.isAssignableFrom(source)) {
            return true;
        }

        return false;
    }

    @Override
    public Timestamp convert(Object source) {
        if (source instanceof Timestamp t) {
            return t;
        } else if (source instanceof Long l) {
            return new Timestamp(l);
        } else if (source instanceof Integer i) {
            return new Timestamp(i);
        }

        throw new ConvertException(source, Timestamp.class);
    }
}
