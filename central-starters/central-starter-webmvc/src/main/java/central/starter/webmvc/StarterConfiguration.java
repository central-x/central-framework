package central.starter.webmvc;

import central.starter.web.converter.LongToTimestampConverter;
import central.starter.webmvc.exception.JsonExceptionResolver;
import central.starter.webmvc.interceptor.ActionReportInterceptor;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
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
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter(){
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();

        SimpleModule module = new SimpleModule();

        converter.getObjectMapper().registerModule(module);
        converter.getObjectMapper().enable(JsonParser.Feature.ALLOW_COMMENTS);

        return converter;
    }
}
