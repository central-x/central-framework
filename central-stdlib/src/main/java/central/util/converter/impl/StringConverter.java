package central.util.converter.impl;

import central.util.converter.Converter;

import java.util.Objects;

/**
 * String Converter
 *
 * @author Alan Yeh
 * @since 2022/07/11
 */
public class StringConverter implements Converter<String> {
    @Override
    public boolean support(Class<?> source) {
        return true;
    }

    @Override
    public String convert(Object source) {
        return Objects.toString(source);
    }
}
