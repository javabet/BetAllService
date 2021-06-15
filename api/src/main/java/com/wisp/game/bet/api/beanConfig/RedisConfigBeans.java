package com.wisp.game.bet.api.beanConfig;

import com.wisp.core.cache.redis.RedisCacheHanderImpl;
import com.wisp.core.cache.redis.redisson.CacheRedissonClient;
import com.wisp.core.idg.IDGeneratorHanderImpl;
import com.wisp.core.idg.redis.RedisIdGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class RedisConfigBeans
{
    @Value("${redis.cache.host}")
    private String cacheHost;
    @Value("${redis.cache.port}")
    private String cachePort;

    @Value("${redis.cache.maxIdle}")
    private int cacheMaxIdle;
    @Value("${redis.cache.maxTotal}")
    private int cacheMaxTotal;
    @Value("${redis.cache.testOnBorrow}")
    private boolean cacheTestOnBorrow;

    @Bean
    public CacheRedissonClient cacheRedissonClient()
    {
        CacheRedissonClient cacheRedissonClient =  new CacheRedissonClient();
        cacheRedissonClient.setHost(cacheHost);
        cacheRedissonClient.setPort(cachePort);
        return cacheRedissonClient;
    }

    @Bean
    public JedisPoolConfig jedisPoolConfig()
    {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(cacheMaxIdle);
        jedisPoolConfig.setMaxTotal(cacheMaxTotal);
        jedisPoolConfig.setTestOnBorrow(cacheTestOnBorrow);

        return jedisPoolConfig;
    }

    @Bean
    public RedisCacheHanderImpl redisCacheHander()
    {
        RedisCacheHanderImpl redisCacheHanderImpl = new RedisCacheHanderImpl();
        redisCacheHanderImpl.setJedisPoolConfig( jedisPoolConfig() );
        redisCacheHanderImpl.setHost(cacheHost);
        redisCacheHanderImpl.setPort(Integer.valueOf(cachePort));
        redisCacheHanderImpl.setCacheRedissonClient(cacheRedissonClient());
        return redisCacheHanderImpl;
    }

    @Bean
    public RedisIdGenerator redisIdGenerator()
    {
        RedisIdGenerator redisIdGenerator = new RedisIdGenerator();
        redisIdGenerator.setCacheHander( redisCacheHander()  );
        return redisIdGenerator;
    }

    @Bean
    public IDGeneratorHanderImpl idGeneratorHander()
    {
        IDGeneratorHanderImpl idGeneratorHander = new IDGeneratorHanderImpl();
        idGeneratorHander.setIdGenerator( redisIdGenerator() );

        return idGeneratorHander;
    }

}
