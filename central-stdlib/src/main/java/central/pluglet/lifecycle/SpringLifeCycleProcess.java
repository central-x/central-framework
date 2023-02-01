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

package central.pluglet.lifecycle;

import central.lang.reflect.InstanceRef;
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
    public void afterCreated(InstanceRef<?> instance) {
        if (instance.getInstance() instanceof ApplicationContextAware aware){
            aware.setApplicationContext(this.applicationContext);
        }
        if (instance.getInstance() instanceof EnvironmentAware aware){
            aware.setEnvironment(this.applicationContext.getEnvironment());
        }
    }

    @Override
    @SneakyThrows
    public void afterPropertySet(InstanceRef<?> instance) {
        if (instance.getInstance() instanceof InitializingBean bean){
            bean.afterPropertiesSet();
        }
    }

    @Override
    @SneakyThrows
    public void beforeDestroy(InstanceRef<?> instance) {
        if (instance.getInstance() instanceof DisposableBean bean){
            bean.destroy();
        }
    }
}
