package central.starter.context;

import lombok.Getter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 全局 ApplicationContext
 *
 * @author Alan Yeh
 * @since 2022/07/16
 */
public class GlobalApplicationContext implements ApplicationContextAware {

    public GlobalApplicationContext() {
    }

    @Getter
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        GlobalApplicationContext.applicationContext = applicationContext;
    }

    /**
     * 判断 GlobalApplicationContext 是否已经初始化了
     */
    public static boolean isInitialized() {
        return applicationContext != null;
    }

    /**
     * 获取 Bean
     */
    public static <T> T getBean(Class<T> requiredType) throws BeansException {
        return applicationContext.getBean(requiredType);
    }

    /**
     * 获取 Bean
     */
    public static <T> T getBean(Class<T> requiredType, Object... args) throws BeansException {
        return applicationContext.getBean(requiredType, args);
    }

    /**
     * 获取 Bean
     */
    public static Object getBean(String name) throws BeansException {
        return applicationContext.getBean(name);
    }

    /**
     * 获取 Bean
     */
    public static <T> T getBean(String name, Class<T> requiredType) throws BeansException {
        return applicationContext.getBean(name, requiredType);
    }

    /**
     * 获取 Bean
     */
    public static Object getBean(String name, Object... args) throws BeansException {
        return applicationContext.getBean(name, args);
    }

    /**
     * 判断是否包含 Bean
     */
    public static boolean containsBean(String name) {
        return applicationContext.containsBean(name);
    }

    /**
     * 获取指定 Bean 的类型
     */
    public static Class<?> getType(String name) throws NoSuchBeanDefinitionException {
        return applicationContext.getType(name);
    }
}
