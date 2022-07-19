package central.net.http.body;

import central.net.http.body.converter.*;
import central.util.LazyValue;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 值转字符串
 *
 * @author Alan Yeh
 * @since 2022/07/14
 */
public class HttpConverters implements HttpConverter<Object> {
    private final List<HttpConverter<?>> instances;

    public HttpConverters() {
        this.instances = List.of(
                new NullConverter(),
                new StringConverter(),
                new BooleanConverter(),
                new NumberConverter(),
                new TimestampConverter(),
                new OptionalEnumConverter()
        );
    }

    private static final LazyValue<HttpConverters> instance = new LazyValue<>(HttpConverters::new);

    public static HttpConverters Default() {
        return instance.get();
    }

    @Override
    public boolean support(@Nullable Object source) {
        return this.instances.stream().anyMatch(it -> it.support(source));
    }

    @Override
    @SuppressWarnings("rawtypes")
    public String convert(Object source) {
        for (HttpConverter instance : this.instances) {
            if (instance.support(source)) {
                return instance.convert(source);
            }
        }
        throw new IllegalArgumentException("不支持的数据类型: " + source.getClass().getName());
    }
}
