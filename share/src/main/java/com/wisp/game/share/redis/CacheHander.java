package com.wisp.game.share.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPubSub;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 缓存服务
 * @author Fe 2016年3月3日
 */
public interface CacheHander {
    Logger LOG = LoggerFactory.getLogger(CacheHander.class);
    String Y = "Y";
    byte[] NULL = "(nil)".getBytes();
    byte[] YES = Y.getBytes();

    /**
     * 对执行的任务进行加锁（最多两小时）
     * @param key 锁的KEY
     * @param task 任务
     */
    public <T> T lock(String key, LockTask<T> task);

    /**
     * 对执行的任务进行加锁。
     * @param key 锁的KEY
     * @param expireTime 超时时间（当超过此时间后，任务将不再加锁）
     * @param task 任务
     */
    public <T> T lock(String key, LockTask<T> task, Integer expireTime);

    /**
     * 缓存数据。数据缓存两小时后强制重新查询
     * @param key 缓存的key
     * @param data 数据来源
     * @return
     */
    public <T> T cache(String key, CacheData data);

    /**
     * 缓存数据。数据缓存expireTime秒后强制重新查询
     * @param key 缓存的key
     * @param data 数据来源
     * @param expireTime 超时时间（秒）
     * @return
     */
    public <T> T cache(String key, CacheData data, Integer expireTime);

    /**
     * 检查给定的key是否存在
     * @param key 缓存的key
     * @return
     */
    public Boolean exists(String key);

    /**
     * 获取一个string值
     * @param key
     * @return
     */
    public String getString(String key);

    /**
     * 从缓存中获取一个数据
     * @param key 缓存的key
     * @return
     */
    public <T> T get(String key);

    /**
     * 设置缓存数据。数据将缓存两小时
     * @param key 缓存的key
     * @param value 缓存的value
     * @return
     */
    public Boolean set(String key, Object value);

    /**
     * 设置缓存数据
     * @param key 缓存的key
     * @param value 缓存的value
     * @param expireTime 超时时间（秒）
     * @return
     */
    public Boolean set(String key, Object value, Integer expireTime);

    /**
     * 删除数据
     * @param key 缓存的key
     * @param keys 缓存的key（多个）
     * @return
     */
    public Boolean delete(String key, String... keys);

    /**
     * 返回库里面存在的key的集合，左模糊
     * @param key
     * @return
     */
    public Set<String> keys(String key);

    /**
     * 原子性增量实现(+1)
     * @param key
     * @return
     */
    public long incr(String key);

    /**
     * 原子性增量实现(+increment)
     * @param key
     * @param increment 增量
     * @return
     */
    public long incrBy(String key, long increment);

    /**
     * 原子性增量实现(+increment)
     * @param key
     * @param increment 增量
     * @param expireTime
     * @return
     */
    public long incrBy(String key, long increment, Integer expireTime);

    /**
     * 原子性增量实现(当前值)
     * @param key
     * @return
     */
    public long incrCurrent(String key);

    /**
     * 原子性增量实现
     * @param key
     * @param expireTime
     * @return
     */
    public long incr(String key, Integer expireTime);

    /**
     * 原子性增量实现-float
     * @param key
     * @param value
     * @param expireTime
     * @return
     */
    public double incrByFloat(String key, double value, Integer expireTime);

    /**
     * 获取集合的size
     * @param key
     * @return
     */
    public long scard(String key);

    /**
     * 从集合中随机取出一个值，并将值从集合中删除
     * @param key
     * @return
     */
    public String spop(String key);

    /**
     * 向集合中添加一个值
     * @param key
     * @param value
     * @return
     */
    public Boolean sadd(String key, String value);

    /**
     * 取集合中所有值
     * @param key
     * @return
     */
    public Set<String> smembers(String key);

    /**
     * 设置超时
     * @param key
     * @param expireTime
     * @return
     */
    public Boolean expire(String key, Integer expireTime);

    /**
     * 不存在时设置
     * @param key
     * @param expireTime
     * @return
     */
    public Boolean setNX(String key, Integer expireTime);

    /**
     * 不存在时设置
     * @param key
     * @param value
     * @param expireTime
     * @return
     */
    public Boolean setNX(String key, Object value, Integer expireTime);

    /**
     * 将数据放入list中
     * @param key
     * @param objects
     * @param expireTime
     * @return
     */
    public Long lpush(String key, Integer expireTime, Object... objects);

    /**
     * 移除并返回列表 key 的尾元素。
     * @param key
     * @return
     */
    public <T> T rpop(String key);

    /**
     * 将数据放入list中
     * @param key
     * @param str
     * @param expireTime
     * @return
     */
    public Long lpushString(String key, Integer expireTime, String str);

    /**
     * 获取list的总长度
     * @param key
     * @return
     */
    public Long llen(String key);

    /**
     * 获取list的结果
     * @param key
     * @param start
     * @param end
     * @return
     */
    public <T> List<T> lrange(String key, long start, long end);

    /**
     * 从list中删除部分数据
     * @param key
     * @param start
     * @param end
     * @return
     */
    public void ltrim(String key, long start, long end);

    /**
     * 排行榜功能添加积分
     * @param key
     * @param score
     * @param target
     */
    public void zincrby(String key, double score, String target);

    /**
     * 排行榜功能添加积分
     * @param key
     * @param score
     * @param target
     * @param expireTime
     */
    public void zincrby(String key, double score, String target, Integer expireTime);

    /**
     * 排行榜功能添加积分
     * @param key
     * @param score
     * @param target
     */
    long zadd(String key, double score, String target);

    /**
     * 排行榜功能添加积分
     * @param key
     * @param score
     * @param target
     * @param expireTime
     */
    long zadd(String key, double score, String target, Integer expireTime);

    /**
     * 排行榜功能-获取排行榜
     * @param key
     * @param start
     * @param end
     * @return
     */
    List<RankingData> zrevrangeWithScores(String key, long start, long end);

    /**
     * 清楚排行榜中的某用户积分
     * @param key
     * @param member
     * @return
     */
    long zrem(String key, String... member);

    /**
     * 获取指定成员的排名
     * @param key
     * @param member
     * @return
     */
    Long zrevrank(String key, String member);

    /**
     * 获取指定成员的分值
     * @param key
     * @param member
     * @return
     */
    Double zscore(String key, String member);

    /**
     * 获取当前服务器时间：毫秒
     * @return
     */
    long time();

    /**
     * 订阅一个或多个频道，调用此方法后线程会阻塞
     * @param jedisPubSub 订阅频道处理
     * @param channel 频道
     */
    void subscribe(JedisPubSub jedisPubSub, String... channel);

    <T> String hmset(String key, Map<String, T> hash, Integer expireTime);

    <T> List<T> hmget(String key, String... fields);

    Long hincrBy(String key, String field, long value, Integer expireTime);

    Set<String> hkeys(String key);

    <T> Map<String, T> hgetAll(String key);

    /**
     * 给指定的频道发布一个消息
     * @param channel 频道
     * @param message 消息
     */
    void publish(String channel, String message);
}
