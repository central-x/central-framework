/*
 * MIT License
 *
 * Copyright (c) 2022-present Alan Yeh <alan@yeh.cn>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package central.starter.probe.core;

import central.starter.probe.ProbeProperties;
import central.util.Listx;
import central.validation.Validatex;
import lombok.Setter;
import org.springframework.beans.InvalidPropertyException;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

/**
 * 探测端口注册器
 *
 * @author Alan Yeh
 * @since 2023/12/31
 */
public class EndpointRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware {

    @Setter
    private Environment environment;

    private ProbeProperties properties = new ProbeProperties();

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry, BeanNameGenerator importBeanNameGenerator) {
        var bindResult = Binder.get(environment).bind("central.probe", ProbeProperties.class);
        if (bindResult.isBound()) {
            this.properties = bindResult.get();
        }

        Validatex.Default().validate(this.properties, new Class[0], violation -> new InvalidPropertyException(this.properties.getClass(), violation.getPropertyPath().toString(), violation.getMessage()));

        if (!properties.isEnabled()) {
            return;
        }

        // 注册 Controller
        var controller = new GenericBeanDefinition();
        controller.setBeanClass(ProbeController.class);
        controller.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE | AbstractBeanDefinition.AUTOWIRE_BY_NAME);
        registry.registerBeanDefinition(importBeanNameGenerator.generateBeanName(controller, registry), controller);

        if (Listx.isNullOrEmpty(properties.getPoints())) {
            return;
        }

        // 注册探测端
        for (var point : properties.getPoints()) {
            var definition = new GenericBeanDefinition();
            definition.setBeanClass(point.getType().getValue());
            definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE | AbstractBeanDefinition.AUTOWIRE_BY_NAME);
            definition.getPropertyValues().addPropertyValues(point.getParams());
            registry.registerBeanDefinition(point.getName(), definition);
        }
    }
}
