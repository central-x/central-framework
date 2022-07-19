package central.net.http.proxy.contract.spring;

import central.net.http.HttpRequest;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * Spring 注解
 *
 * @author Alan Yeh
 * @since 2022/07/18
 */
public interface SpringResolver {

    /**
     * 是否支持解析该参数
     *
     * @param parameter 字段信息
     */
    boolean support(Parameter parameter);

    /**
     * 解析参数
     *
     * @param request   请求
     * @param method    方法信息
     * @param parameter 字段信息
     * @param arg       参数值
     * @return 是否已解析，如果返回 false，会交给下一个解析器去处理
     */
    boolean resolve(HttpRequest request, Method method, Parameter parameter, Object arg);
}
