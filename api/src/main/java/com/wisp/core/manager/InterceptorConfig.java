package com.wisp.core.manager;


import com.wisp.core.interceptor.AllowOriginInterceptor;
import com.wisp.core.interceptor.ResponseResultInterceptor;
import com.wisp.core.interceptor.TimeCostInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class InterceptorConfig extends WebMvcConfigurationSupport
{
    @Autowired
    private AllowOriginInterceptor allowOriginInterceptor;

    @Autowired
    private TimeCostInterceptor timeCostInterceptor;

    @Autowired
    private ResponseResultInterceptor responseResultInterceptor;

    @Override
    protected void addInterceptors(InterceptorRegistry registry)
    {
        super.addInterceptors(registry);
        registry.addInterceptor(allowOriginInterceptor).addPathPatterns("/**");
        registry.addInterceptor(timeCostInterceptor).addPathPatterns("/**");
        registry.addInterceptor(responseResultInterceptor).addPathPatterns("/**");
    }

    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters)
    {
        super.extendMessageConverters(converters);

        /**
        //1.需要定义一个convert转换消息的对象;
        FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();
        //2.添加fastJson的配置信息，比如：是否要格式化返回的json数据;
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setSerializerFeatures(
                SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteNullNumberAsZero,
                SerializerFeature.QuoteFieldNames,
                SerializerFeature.WriteNullStringAsEmpty,
                SerializerFeature.DisableCircularReferenceDetect,
                SerializerFeature.WriteNullListAsEmpty,
                SerializerFeature.WriteDateUseDateFormat);
        //3处理中文乱码问题
        List<MediaType> fastMediaTypes = new ArrayList<MediaType>();
        fastMediaTypes.add(MediaType.APPLICATION_JSON);
        //4.在convert中添加配置信息.
        fastJsonHttpMessageConverter.setSupportedMediaTypes(fastMediaTypes);
        fastJsonHttpMessageConverter.setFastJsonConfig(fastJsonConfig);
        //5.将convert添加到converters当中.
        converters.add(0, fastJsonHttpMessageConverter);
        **/

        GsonHttpMessageConverter gsonHttpMessageConverter = new GsonHttpMessageConverter();

        converters.add(0,gsonHttpMessageConverter);

    }
}
