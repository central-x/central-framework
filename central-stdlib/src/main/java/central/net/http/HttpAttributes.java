package central.net.http;

import central.lang.Attribute;
import central.net.http.proxy.HttpProxy;

import java.lang.reflect.Method;

/**
 * HttpRequest 常用属性
 *
 * @author Alan Yeh
 * @since 2022/07/19
 */
public interface HttpAttributes {
    /**
     * 代理方法
     */
    Attribute<Method> PROXY_METHOD = new Attribute<>(HttpProxy.class.getName() + ".method");
}
