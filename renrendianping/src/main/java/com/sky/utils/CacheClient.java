package com.sky.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;


@Component
@Slf4j
public class CacheClient {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /** 线程池 */
    private static final ExecutorService CACHE_REBUILD_EXECUTOR = Executors.newFixedThreadPool(10);

    /**
     * 可以将任意类型的Java对象存储到redis中
     * @param key  键
     * @param value 值
     * @param time  过期时间
     * @param unit  时间单位
     */
    public void set(String key, Object value, Long time, TimeUnit unit){
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(value),time,unit);
    }

    /**
     * 逻辑过期时间对象存储到redis中
     * @param key  redis的key
     * @param value redis的值
     * @param time  逻辑过期时间
     * @param unit  时间单位
     */
    public void setLogicalExpire(String key, Object value, Long time, TimeUnit unit){
        RedisData redisData = new RedisData();
        redisData.setExpireTime(LocalDateTime.now().plusSeconds(unit.toSeconds(time)));
        redisData.setData(value);
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(redisData));
    }

    /**
     * 解决缓存穿透
     * @param keyPrefix  key的前缀
     * @param id    key的后缀，
     * @param type  返回的类型
     * @param dbFallback 逻辑语句
     * @param time  过期时间
     * @param unit  时间单位
     * @param <R>   泛型，返回的类型
     * @param <ID>  参数的类型
     * @return
     */
    public <R,ID> R queryWithPassThrough(String keyPrefix, ID id, Class<R> type, Function<ID,R> dbFallback,
                                         Long time,TimeUnit unit){
        String key = keyPrefix + id;
        // 1.先从redis中查询
        String json = stringRedisTemplate.opsForValue().get(key);
        // 2.判断是否存在
        if(StrUtil.isNotBlank(json)){
            // 存在shop,直接返回
            return JSONUtil.toBean(json,type);
        }
        // 防止缓存穿透，数据库没查到的数据都要设为 ""
        if (json != null){
            return null;
        }
        // 3.不存在，判断数据库中是否存在。
        R r = dbFallback.apply(id);
        // 数据库中不存在,先将""缓存到redis，防止缓存穿透，直接返回
        if (Objects.isNull(r)){
            stringRedisTemplate.opsForValue().set(RedisConstants.CACHE_SHOP_KEY+id,""
                    ,RedisConstants.CACHE_NULL_TTL, TimeUnit.MINUTES);
            return null;
        }
        // 4.数据库中有数据，先将数据保存到redis中，再返回
        this.set(key,r,time,unit);
        return r;
    }


    public <R,ID> R queryWithLogicalExpire(String keyPrefix, ID id, Class<R> type, Function<ID,R> dbFallback,
                                           Long time,TimeUnit unit){
        String key = keyPrefix+id;
        // 1.先从redis中查询
        String json = stringRedisTemplate.opsForValue().get(key);
        // 2.判断是否存在
        if(StrUtil.isBlank(json)){
            // 未命中
            return null;
        }
        // 3.命中,需要判断过期时间
        RedisData redisData = JSONUtil.toBean(json, RedisData.class);
        R r = JSONUtil.toBean((JSONObject) redisData.getData(), type);
        LocalDateTime expireTime = redisData.getExpireTime();
        // 判断是否过期
        if (expireTime.isAfter(LocalDateTime.now())){
            // 未过期
            return r;
        }
        // 过期，尝试获取互斥锁,重建缓存
        boolean isLock = tryLock(RedisConstants.LOCK_SHOP_KEY + id);
        // 判断是否获取互斥锁
        if (isLock){
            // 获取到锁，应该再次检测redis中是否已经存在key
            json = stringRedisTemplate.opsForValue().get(key);
            // 如果已经有数据,再判断时间是否过期
            if (StrUtil.isNotBlank(json)){
                redisData = JSONUtil.toBean(json,RedisData.class);
                // 未过期，释放锁，直接返回，不需要重建锁（一定要释放锁，不然会死锁）
                if (redisData.getExpireTime().isAfter(LocalDateTime.now())){
                    delLock(RedisConstants.LOCK_SHOP_KEY + id);
                    return BeanUtil.toBean(redisData.getData(),type);
                }
            }
            // 获取成功,开启新的线程，从数据库中重建缓存
            CACHE_REBUILD_EXECUTOR.submit(()->{
                try {
                    // 查询数据库
                    R apply = dbFallback.apply(id);
                    // 写缓存
                    this.setLogicalExpire(key,apply,time,unit);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    delLock(RedisConstants.LOCK_SHOP_KEY+id);
                }
            });
            return r;
        }
        // 获取失败，返回过期的redis数据
        return r;
    }


    /**
     * 获取互斥锁
     * @param key
     * @return
     */
    public boolean tryLock(String key){
        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", RedisConstants.LOCK_SHOP_TTL, TimeUnit.SECONDS);
        return BooleanUtil.isTrue(flag);
    }

    /**
     * 释放锁
     * @param key
     */
    public void delLock(String key){
        stringRedisTemplate.delete(key);
    }

}
