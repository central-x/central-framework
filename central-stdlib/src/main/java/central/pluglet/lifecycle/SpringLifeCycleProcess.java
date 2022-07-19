package central.pluglet.lifecycle;

import central.lang.reflect.InstanceReference;
import central.pluglet.LifeCycleProcessor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;

/**
 * Spring LifeCycle
 *
 * @author Alan Yeh
 * @since 2022/07/13
 */
public class SpringLifeCycleProcess implements LifeCycleProcessor {
    private final ApplicationContext applicationContext;

    public SpringLifeCycleProcess(ApplicationContext applicationContext){
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterCreated(InstanceReference<?> instance) {
        if (instance.getInstance() instanceof ApplicationContextAware aware){
            aware.setApplicationContext(this.applicationContext);
        }
        if (instance.getInstance() instanceof EnvironmentAware aware){
            aware.setEnvironment(this.applicationContext.getEnvironment());
        }
    }

    @Override
    @SneakyThrows
    public void afterPropertySet(InstanceReference<?> instance) {
        if (instance.getInstance() instanceof InitializingBean bean){
            bean.afterPropertiesSet();
        }
    }

    @Override
    @SneakyThrows
    public void beforeDestroy(InstanceReference<?> instance) {
        if (instance.getInstance() instanceof DisposableBean bean){
            bean.destroy();
        }
    }
}
