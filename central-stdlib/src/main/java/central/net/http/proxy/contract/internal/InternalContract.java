package central.net.http.proxy.contract.internal;

import central.net.http.HttpRequest;
import central.net.http.proxy.Contract;

import java.lang.reflect.Method;

/**
 * 本框架自带的契约
 *
 * @author Alan Yeh
 * @since 2022/07/18
 */
public class InternalContract implements Contract {
    @Override
    public HttpRequest parse(Object instance, Method method, Object[] args) {
        return null;
    }
}
