package central.net.http.body;

import javax.annotation.Nullable;

/**
 * 将对象转换为 String
 *
 * @author Alan Yeh
 * @since 2022/07/14
 */
public interface HttpConverter<T> {
    /**
     * 是否支持转换
     *
     * @param source 源
     */
    boolean support(@Nullable Object source);

    /**
     * 将值转换成字符串
     *
     * @param source 源
     */
    String convert(T source);
}
