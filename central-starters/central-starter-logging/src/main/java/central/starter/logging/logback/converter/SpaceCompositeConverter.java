package central.starter.logging.logback.converter;

import central.util.Stringx;
import ch.qos.logback.core.pattern.CompositeConverter;

/**
 * 去空格后，在尾部加空格
 *
 * @author Alan Yeh
 * @since 2022/07/19
 */
public class SpaceCompositeConverter<E> extends CompositeConverter<E> {
    @Override
    protected String transform(E event, String in) {
        if (Stringx.isNullOrBlank(in)) {
            return "";
        } else {
            return in.trim() + " ";
        }
    }
}
