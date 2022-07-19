package central.net.http.proxy;

import central.net.http.HttpRequest;

import java.lang.reflect.Method;

/**
 * 代理契约
 * 用于将本地方法调转化为网络调用
 *
 * @author Alan Yeh
 * @since 2022/07/14
 */
public interface Contract {
    /**
     * 将本地调用转化为 HttpRequest
     *
     * @param instance 被调用实例
     * @param method   被调用方法
     * @param args     调用参数
     * @return Http Request
     */
    HttpRequest parse(Object instance, Method method, Object[] args);
}
