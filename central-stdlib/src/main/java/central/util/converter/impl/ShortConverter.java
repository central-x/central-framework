package central.util.converter.impl;

import central.util.converter.ConvertException;
import central.util.converter.Converter;

/**
 * @author Alan Yeh
 * @since 2022/07/11
 */
public class ShortConverter implements Converter<Short> {
    @Override
    public boolean support(Class<?> source) {
        if (Number.class.isAssignableFrom(source)) {
            return true;
        } else if (String.class.isAssignableFrom(source)) {
            return true;
        }

        return false;
    }

    @Override
    public Short convert(Object source) {
        if (source instanceof Number n) {
            return n.shortValue();
        } else if (source instanceof String s) {
            return Short.parseShort(s);
        }

        throw new ConvertException(source, Short.class);
    }
}
