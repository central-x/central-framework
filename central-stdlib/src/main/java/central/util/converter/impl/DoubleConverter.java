package central.util.converter.impl;

import central.util.converter.ConvertException;
import central.util.converter.Converter;

/**
 * Double Converter
 *
 * @author Alan Yeh
 * @since 2022/07/11
 */
public class DoubleConverter implements Converter<Double> {
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
    public Double convert(Object source) {
        if (source instanceof Number n) {
            return n.doubleValue();
        } else if (source instanceof String s) {
            return Double.parseDouble(s);
        }

        throw new ConvertException(source, Double.class);
    }
}
