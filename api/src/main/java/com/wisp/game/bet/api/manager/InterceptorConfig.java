package com.wisp.game.bet.api.manager;

import com.wisp.game.bet.api.interceptor.MyInterceptor;
import com.wisp.game.bet.api.interceptor.ResponseResultInterceptor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig extends WebMvcConfigurationSupport
{
    @Autowired
    private MyInterceptor myInterceptor;

    @Autowired
    private ResponseResultInterceptor responseResultInterceptor;

    @Override
    protected void addInterceptors(InterceptorRegistry registry)
    {
        super.addInterceptors(registry);
        registry.addInterceptor(myInterceptor).addPathPatterns("/**");
        registry.addInterceptor(responseResultInterceptor).addPathPatterns("/**");
    }


}
