package central.util.converter.impl;

import central.util.converter.ConvertException;
import central.util.converter.Converter;

/**
 * Boolean Converter
 *
 * @author Alan Yeh
 * @since 2022/07/11
 */
public class BooleanConverter implements Converter<Boolean> {
    @Override
    public boolean support(Class<?> source) {
        if (Boolean.class.isAssignableFrom(source)) {
            return true;
        } else if (String.class.isAssignableFrom(source)) {
            return true;
        } else if (Number.class.isAssignableFrom(source)) {
            return true;
        }

        return false;
    }

    @Override
    public Boolean convert(Object source) {
        if (source instanceof Boolean b) {
            return b;
        } else if (source instanceof String s) {
            if ("true".equalsIgnoreCase(s)) {
                return Boolean.TRUE;
            } else if ("1".equals(s)) {
                return Boolean.TRUE;
            } else {
                return false;
            }
        } else if (source instanceof Number n) {
            return n.intValue() != 0;
        }

        throw new ConvertException(source, Boolean.class);
    }
}
