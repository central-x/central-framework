package central.net.http;

import central.net.http.executor.java.JavaExecutor;

/**
 * Java Test Cases
 *
 * @author Alan Yeh
 * @since 2022/07/18
 */
public class TestJava extends TestHttp {

    @Override
    protected HttpExecutor getExecutor() {
        return JavaExecutor.Default();
    }
}
