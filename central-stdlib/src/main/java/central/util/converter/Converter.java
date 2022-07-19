package central.util.converter;

/**
 * 类型转换器
 *
 * @author Alan Yeh
 * @since 2022/07/10
 */
public interface Converter<T> {

    /**
     * 判断是否支持转换数据类型
     *
     * @param source 源类型
     */
    boolean support(Class<?> source);

    /**
     * 转换数据类型
     *
     * @param source 源数据
     */
    T convert(Object source);
}
