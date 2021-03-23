package com.wisp.core.config.bean;

import com.wisp.core.cache.redis.redisson.CacheRedissonClient;
import com.wisp.core.email.EmailHanderImpl;
import com.wisp.core.event.init.EventHander;
import com.wisp.core.random.RandomHander;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class ConfigBeans
{
    @Bean
    public RandomHander randomHander()
    {
        return new RandomHander();
    }

    @Bean
    public EventHander eventHander()
    {
        return new EventHander();
    }

    @Bean
    public EmailHanderImpl emailHander()
    {
        return new EmailHanderImpl();
    }




}
