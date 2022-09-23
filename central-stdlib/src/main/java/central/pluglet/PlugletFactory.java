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

package central.pluglet;

import central.bean.InitializeException;
import central.lang.reflect.TypeReference;
import central.pluglet.binder.ControlBinder;
import central.pluglet.control.ControlResolver;
import central.pluglet.control.PlugletControl;
import central.lang.Arrayx;
import central.lang.Assertx;
import central.lang.Stringx;

import java.util.*;

/**
 * Pluglet Factory
 * Pluglet 用于通过可视化的方式动态构建插件。开发人员通过声明 Pluglet 之后，PlugletFactory 可以将这些插件需要的内容
 *
 * @author Alan Yeh
 * @since 2022/07/11
 */
public class PlugletFactory {

    /**
     * 字段绑定
     */
    private final List<FieldBinder> binders = new ArrayList<>();

    public void registerBinder(FieldBinder binder) {
        this.binders.add(binder);
    }

    /**
     * 实例生命周期处理
     */
    private final List<LifeCycleProcessor> processors = new ArrayList<>();

    public void registerLifeCycleProcessor(LifeCycleProcessor processor) {
        this.processors.add(processor);
    }

    /**
     * 控件解析
     */
    private final List<ControlResolver> resolvers = new ArrayList<>();

    public PlugletFactory() {
        var properties = new Properties();
        try {
            var resources = Thread.currentThread().getContextClassLoader().getResources("MATA-INF/pluglet.properties");
            while (resources.hasMoreElements()) {
                var resource = resources.nextElement();
                properties.load(resource.openStream());
            }
        } catch (Exception cause) {
            throw new InitializeException(PlugletFactory.class, "Cannot load config in 'MATA-INF/pluglet.properties'", cause);
        }

        // 注册控件解析
        Arrayx.asStream(properties.getProperty("resolvers").split("[,]"))
                // 将 properties 里面指定的 ControlResolver 类加载出来
                .map(TypeReference::of)
                // 判断这些类是否都继承了 ControlResolver
                .peek(type -> Assertx.mustAssignableFrom(ControlResolver.class, type.getRawClass(), () -> new InitializeException(PlugletFactory.class, Stringx.format("'{}' must assignable to {}", type.getName(), ControlResolver.class.getName()))))
                // 实例化这些 ControlResolver
                .map(TypeReference::newInstance)
                // 注册
                .forEach(it -> this.resolvers.add((ControlResolver) it.getInstance()));

        // 注册字段绑定
        Arrayx.asStream(properties.getProperty("binders").split("[,]"))
                // 将 properties 里面指定的 FieldBinder 类加载出来
                .map(TypeReference::of)
                // 判断这些类是否都继承了 FieldBinder
                .peek(type -> Assertx.mustAssignableFrom(FieldBinder.class, type.getRawClass(), () -> new InitializeException(PlugletFactory.class, Stringx.format("'{}' must assignable to {}", type.getName(), ControlBinder.class.getName()))))
                // 实例化这些 FieldBinder
                .map(TypeReference::newInstance)
                // 注册
                .forEach(it -> this.binders.add((FieldBinder) it.getInstance()));

        // 生命周期处理
        Arrayx.asStream(properties.getProperty("processes").split("[,]"))
                // 将 properties 里面指定的 LifeCycleProcessor 类加载出来
                .map(TypeReference::of)
                // 判断这些类是否都继承了 LifeCycleProcessor
                .peek(type -> Assertx.mustAssignableFrom(LifeCycleProcessor.class, type.getRawClass(), () -> new InitializeException(PlugletFactory.class, Stringx.format("'{}' must assignable to {}", type.getName(), LifeCycleProcessor.class.getName()))))
                // 实例化这些 LifeCycleProcessor
                .map(TypeReference::newInstance)
                // 注册
                .forEach(it -> this.processors.add((LifeCycleProcessor) it.getInstance()));
    }

    /**
     * 获取插件声明的控件列表
     *
     * @param pluglet 插件
     */
    public List<PlugletControl> getControls(Class<?> pluglet) {
        var reference = TypeReference.of(pluglet);
        var fields = reference.getFields();

        var controls = new ArrayList<PlugletControl>();

        field_loop:
        for (var field : fields) {
            for (var resolver : this.resolvers) {
                if (resolver.support(field)) {
                    controls.add(resolver.resolve(field));
                    continue field_loop;
                }
            }
        }

        return controls;
    }

    /**
     * 创建插件
     *
     * @param pluglet 插件类型
     * @param params  初始化参数
     * @param <T>     插件类型
     * @return 插件实例
     */
    public <T> T create(Class<T> pluglet, Map<String, Object> params) {
        var reference = TypeReference.of(pluglet);
        var instance = reference.newInstance();

        // 处理生命周期
        for (var processor : this.processors) {
            processor.afterCreated(instance);
        }

        // 绑定字段
        field_loop:
        for (var field : reference.getFields()) {
            for (var binder : this.binders) {
                if (binder.support(field)) {
                    binder.bind(instance, field, params);
                    continue field_loop;
                }
            }
        }

        // 处理生命周期
        for (var processor : this.processors) {
            processor.afterPropertySet(instance);
        }

        return instance.getInstance();
    }
}
