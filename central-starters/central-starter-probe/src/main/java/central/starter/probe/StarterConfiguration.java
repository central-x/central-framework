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

package central.starter.probe;

import central.starter.probe.core.EndpointRegistrar;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 配置
 *
 * @author Alan Yeh
 * @since 2023/12/27
 */
@Configuration
@Import(EndpointRegistrar.class)
@EnableConfigurationProperties(ProbeProperties.class)
@ConditionalOnProperty(name = "central.probe.enabled", havingValue = "true", matchIfMissing = true)
public class StarterConfiguration {

//    @Bean
//    public Map<String, Endpoint> endpoints(ProbeProperties properties) {
//        if (!properties.isEnabled()) {
//            return Collections.emptyMap();
//        }
//
//        if (Listx.isNullOrEmpty(properties.getPoints())) {
//            return Collections.emptyMap();
//        }
//
//        var points = new HashMap<String, Endpoint>();
//        for (var point : properties.getPoints()) {
//
//        }
//        return points;
//    }
}