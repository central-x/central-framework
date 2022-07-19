package central.net.http.body;

import java.io.IOException;

/**
 * Body 处理器
 * 将 Body 处理成指定格式
 *
 * @author Alan Yeh
 * @since 2022/07/17
 */
public interface BodyExtractor<T> {
    T extract(Body body) throws IOException;
}
