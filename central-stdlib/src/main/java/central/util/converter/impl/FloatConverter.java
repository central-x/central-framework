package central.util.converter.impl;

import central.util.converter.ConvertException;
import central.util.converter.Converter;

/**
 * @author Alan Yeh
 * @since 2022/07/11
 */
public class FloatConverter implements Converter<Float> {
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
    public Float convert(Object source) {
        if (source instanceof Number n) {
            return n.floatValue();
        } else if (source instanceof String s) {
            return Float.parseFloat(s);
        }

        throw new ConvertException(source, Float.class);
    }
}
