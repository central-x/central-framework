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

package central.starter.orm.core;

import central.lang.Arrayx;
import central.lang.Stringx;
import central.sql.SqlExecutor;
import central.sql.proxy.Mapper;
import central.starter.orm.EnableOrm;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 * Mapper 自动注入
 *
 * @author Alan Yeh
 * @since 2022/09/22
 */
@Slf4j
public class MapperRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {

    @Setter
    private ResourceLoader resourceLoader;

    @Override
    public void registerBeanDefinitions(@Nonnull AnnotationMetadata importingClassMetadata, @Nonnull BeanDefinitionRegistry registry) {
        ClassPathBeanDefinitionScanner scanner = getScanner(registry);
        scanner.addIncludeFilter(new AssignableTypeFilter(Mapper.class));
        scanner.addExcludeFilter((reader, factory) -> {
            // 如果不包含 Repository 注解的话，排除
            return !reader.getAnnotationMetadata().hasAnnotation(Repository.class.getName());
        });
        scanner.setResourceLoader(this.resourceLoader);

        // 根据注解上标注的包名，来获取待扫描的包
        MergedAnnotation<EnableOrm> annotation = importingClassMetadata.getAnnotations().get(EnableOrm.class);
        // 待扫描包名
        String[] packages = annotation.getStringArray("packages");
        if (Arrayx.isNullOrEmpty(packages)) {
            if (Stringx.isNotBlank(importingClassMetadata.getClassName())) {
                packages = new String[]{importingClassMetadata.getClassName().substring(0, importingClassMetadata.getClassName().lastIndexOf("."))};
            } else {
                packages = new String[]{"central"};
            }
        }

        for (String pkg : packages) {
            log.info("[central-starter-orm] 扫描包 '{}'", pkg);
            scanner.scan(pkg);
        }
    }

    private ClassPathBeanDefinitionScanner getScanner(BeanDefinitionRegistry registry) {
        return new ClassPathBeanDefinitionScanner(registry, false) {
            @Override
            protected void registerBeanDefinition(@Nonnull BeanDefinitionHolder definitionHolder, @Nonnull BeanDefinitionRegistry registry) {
                log.info("[central-starter-orm] 已注册 '{}'", definitionHolder.getBeanDefinition().getBeanClassName());
                GenericBeanDefinition genericBean = (GenericBeanDefinition) definitionHolder.getBeanDefinition();

                genericBean.getConstructorArgumentValues().addGenericArgumentValue(genericBean.getBeanClassName());
                genericBean.getPropertyValues().add("name", definitionHolder.getBeanName());
                genericBean.getPropertyValues().add("executor", new RuntimeBeanReference(SqlExecutor.class));

                // 替换 Mapper 的实现类
                genericBean.setBeanClass(MapperFactoryBean.class);
                genericBean.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);

                super.registerBeanDefinition(definitionHolder, registry);
            }

            @Override
            protected @Nonnull Set<BeanDefinitionHolder> doScan(@Nonnull String... basePackages) {
                Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);
                if (beanDefinitions.isEmpty()) {
                    log.warn("[central-starter-orm] 在 '{}' 包下没有找到 Mapper", Arrayx.getFirst(basePackages));
                }
                return beanDefinitions;
            }

            @Override
            protected boolean isCandidateComponent(@Nonnull AnnotatedBeanDefinition beanDefinition) {
                return beanDefinition.getMetadata().isInterface() && !beanDefinition.getMetadata().isAnnotation();
            }
        };
    }
}
