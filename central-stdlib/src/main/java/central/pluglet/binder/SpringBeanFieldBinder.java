package central.pluglet.binder;

import central.bean.InitializeException;
import central.lang.reflect.FieldReference;
import central.lang.reflect.InstanceReference;
import central.pluglet.FieldBinder;
import central.util.Stringx;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import java.util.Map;

/**
 * Spring Bean
 *
 * @author Alan Yeh
 * @since 2022/07/11
 */
public class SpringBeanFieldBinder implements FieldBinder {
    private final ApplicationContext applicationContext;

    public SpringBeanFieldBinder(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public boolean support(FieldReference field) {
        if (ApplicationContext.class == field.getType().getRawClass()) {
            return true;
        } else if (Environment.class == field.getType().getRawClass()) {
            return true;
        } else {
            return field.getAnnotation(Autowired.class) != null || field.getAnnotation(Qualifier.class) != null;
        }
    }

    @Override
    public void bind(InstanceReference<?> target, FieldReference field, Map<String, Object> params) {
        var autowired = field.getAnnotation(Autowired.class);
        var qualifier = field.getAnnotation(Qualifier.class);

        String name = null;
        if (qualifier != null && Stringx.isNotBlank(qualifier.value())) {
            name = qualifier.value();
        }

        Object bean = null;
        try {
            if (ApplicationContext.class == field.getType().getRawClass()) {
                bean = this.applicationContext;
            } else if (Environment.class == field.getType().getRawClass()) {
                bean = this.applicationContext.getEnvironment();
            } else if (Stringx.isNotBlank(name)) {
                bean = this.applicationContext.getBean(name);
            } else {
                bean = this.applicationContext.getBean(field.getType().getRawClass());
            }
        } catch (BeansException ignored) {
        }

        if (bean == null) {
            if (autowired == null || autowired.required()) {
                if (Stringx.isNullOrBlank(name)) {
                    throw new InitializeException(target.getType().getRawClass(), Stringx.format("Cannot find specific bean(name='{}')", name));
                } else {
                    throw new InitializeException(target.getType().getRawClass(), Stringx.format("Cannot find specific bean(type='{}')", field.getType()));
                }
            }
        } else {
            field.setValue(target, bean);
        }
    }
}
