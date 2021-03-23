package com.wisp.game.bet.api.beanConfig;

//import com.wisp.core.cache.ehcache.EhcacheManager;
//import org.springframework.cache.ehcache.EhCacheFactoryBean;
//import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
//import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.ClassPathResource;

@Configuration
public class EcacheConfigBeans
{
    /**
    @Bean
    public EhCacheFactoryBean ehCache()
    {
        EhCacheFactoryBean ehCacheFactoryBean = new EhCacheFactoryBean();
        ehCacheFactoryBean.setCacheName("DEFAULT_CACHE");
        ehCacheFactoryBean.setCacheManager(defaultCacheManager());
        return ehCacheFactoryBean;
    }

    @Bean
    public EhCacheManagerFactoryBean defaultCacheManager()
    {
        EhCacheManagerFactoryBean ehCacheManagerFactoryBean = new EhCacheManagerFactoryBean();
        ehCacheManagerFactoryBean.setShared(true);
        ehCacheManagerFactoryBean.setConfigLocation(new ClassPathResource("classpath:wisp/ehcache.xml"));

        return ehCacheManagerFactoryBean;
    }

    public EhcacheManager ehcacheManager()
    {
        EhcacheManager ehcacheManager = new EhcacheManager();
        ehcacheManager.setEhCache(ehCache());
        return ehcacheManager;
    }
    **/
}
