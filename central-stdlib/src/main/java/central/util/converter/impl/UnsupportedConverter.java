package central.util.converter.impl;

import central.util.converter.Converter;

/**
 * Unsupported Converter
 * 不支持的转换器
 *
 * @author Alan Yeh
 * @since 2022/07/11
 */
public class UnsupportedConverter implements Converter<Void> {

    private static final UnsupportedConverter INSTANCE = new UnsupportedConverter();

    public static UnsupportedConverter getInstance() {
        return INSTANCE;
    }

    private UnsupportedConverter() {
    }

    @Override
    public boolean support(Class<?> source) {
        return false;
    }

    @Override
    public Void convert(Object source) {
        return null;
    }
}
