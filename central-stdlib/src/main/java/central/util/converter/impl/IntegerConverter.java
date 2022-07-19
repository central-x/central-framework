package central.util.converter.impl;

import central.util.converter.ConvertException;
import central.util.converter.Converter;

/**
 * Integer Converter
 *
 * @author Alan Yeh
 * @since 2022/07/11
 */
public class IntegerConverter implements Converter<Integer> {
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
    public Integer convert(Object source) {
        if (source instanceof Number n) {
            return n.intValue();
        } else if (source instanceof String s) {
            return Integer.parseInt(s);
        }

        throw new ConvertException(source, Integer.class);
    }
}
