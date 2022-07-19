package central.net.http;

import central.net.http.executor.okhttp.OkHttpExecutor;

/**
 * OkHttp Test Cases
 *
 * @author Alan Yeh
 * @since 2022/07/15
 */
public class TestOkHttp extends TestHttp {

    @Override
    protected HttpExecutor getExecutor() {
        return OkHttpExecutor.Default();
    }
}
