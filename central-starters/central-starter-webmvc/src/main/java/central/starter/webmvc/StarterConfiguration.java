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

package central.starter.webmvc;

import central.starter.web.converter.LongToTimestampConverter;
import central.starter.webmvc.exception.JsonExceptionResolver;
import central.starter.webmvc.filter.UnderlineParameterNameFilter;
import central.starter.webmvc.filter.WebMvcWrapFilter;
import central.starter.webmvc.interceptor.ActionReportInterceptor;
import central.validation.MessageInterpolator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import jakarta.validation.Validation;
import lombok.Setter;
import org.hibernate.validator.HibernateValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Starter Configuration
 *
 * @author Alan Yeh
 * @since 2022/07/15
 */
@Configuration
@ComponentScan("central.starter.webmvc.exception")
@EnableConfigurationProperties(WebMvcProperties.class)
public class StarterConfiguration implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ActionReportInterceptor());
    }

    @Setter(onMethod_ = @Autowired)
    private JsonExceptionResolver resolver;

    /**
     * 添加全局异常捕捉
     */
    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
        resolvers.add(resolver);
    }

    /**
     * 添加日期转换
     * 参数使用 java.sql.TimeStamp 作为保存日期的类型，前后端使用 time millis 作为传输格式
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new LongToTimestampConverter());
    }


    /**
     * 配置 Jackson
     */
    @Bean
    @ConditionalOnClass(ObjectMapper.class)
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        var converter = new MappingJackson2HttpMessageConverter();

        var module = new SimpleModule();

        converter.getObjectMapper().registerModule(module);
        converter.getObjectMapper().enable(JsonParser.Feature.ALLOW_COMMENTS);

        return converter;
    }

    /**
     * 自定义参数校验器
     * 支持 @Label 注解
     */
    @Override
    public Validator getValidator() {
        return new SpringValidatorAdapter(Validation.byProvider(HibernateValidator.class)
                .configure()
                .messageInterpolator(new MessageInterpolator())
                .buildValidatorFactory()
                .getValidator());
    }

    /**
     * 支持下划线命名的入参
     */
    @Bean
    public FilterRegistrationBean<UnderlineParameterNameFilter> underlineParameterNameFilterRegistrationBean() {
        var bean = new FilterRegistrationBean<UnderlineParameterNameFilter>();
        bean.setFilter(new UnderlineParameterNameFilter());
        bean.addUrlPatterns("/*");
        bean.setName("underlineParameterNameFilter");
        bean.setOrder(0);
        return bean;
    }

    /**
     * 将所有 HttpServletRequest 封装为 WebMvcRequest
     */
    @Bean
    public FilterRegistrationBean<WebMvcWrapFilter> webMvcWrapFilterRegistrationBean() {
        var bean = new FilterRegistrationBean<WebMvcWrapFilter>();
        bean.setFilter(new WebMvcWrapFilter());
        bean.addUrlPatterns("/*");
        bean.setName("webMvcWrapFilter");
        bean.setOrder(0);
        return bean;
    }
}
