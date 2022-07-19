package central.util.converter.impl;

import central.util.converter.ConvertException;
import central.util.converter.Converter;

/**
 * ByteConverter
 *
 * @author Alan Yeh
 * @since 2022/07/11
 */
public class ByteConverter implements Converter<Byte> {
    @Override
    public boolean support(Class<?> source) {
        if (Number.class.isAssignableFrom(source)) {
            return true;
        }

        return false;
    }

    @Override
    public Byte convert(Object source) {
        if (source instanceof Number n) {
            return n.byteValue();
        }

        throw new ConvertException(source, Byte.class);
    }
}
