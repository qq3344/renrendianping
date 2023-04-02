package com.sky.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.dto.Result;
import com.sky.pojo.Shop;
import com.sky.mapper.ShopMapper;
import com.sky.service.IShopService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.utils.CacheClient;
import com.sky.utils.RedisConstants;
import com.sky.utils.RedisData;
import com.sky.utils.SystemConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.domain.geo.GeoReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements IShopService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private static final ExecutorService CACHE_REBUILD_EXECUTOR = Executors.newFixedThreadPool(10);

    /**
     * 缓存工具类
     */
    @Resource
    private CacheClient cacheClient;

    /**
     * 根据id返回商铺信息
     * @param id
     * @return
     */
    @Override
    public Result queryById(Long id) {
        Shop shop = null;
        // 缓存穿透
//        shop = queryWithPassThrough(id);
        // 互斥锁解决缓存击穿
        shop = queryWithMutex(id);

        // 逻辑过期时间解决缓存击穿
        // shop = queryWithLogicalExpire(id);

//        shop = cacheClient.queryWithPassThrough(RedisConstants.CACHE_SHOP_KEY,id,Shop.class, this::getById,
//                RedisConstants.CACHE_SHOP_TTL,TimeUnit.MINUTES);
//        shop = cacheClient.queryWithLogicalExpire(RedisConstants.CACHE_SHOP_KEY,id,Shop.class,this::getById,
//                20L,TimeUnit.SECONDS);
        if (shop == null) {
            return Result.fail("店铺不存在！");
        }
        return Result.ok(shop);
    }

    /**
     * 缓存穿透
     * @param id
     * @return
     */
    public Shop queryWithPassThrough(Long id){
        // 1.先从redis中查询
        String shopJson = stringRedisTemplate.opsForValue().get(RedisConstants.CACHE_SHOP_KEY + id);
        // 2.判断是否存在
        if(StrUtil.isNotBlank(shopJson)){
            // 存在shop直接返回
            return JSONUtil.toBean(shopJson,Shop.class);
        }
        // 防止缓存穿透，数据库没查到的数据都要设为 ""
        if (shopJson != null){
            return null;
        }
        // 3.不存在，判断数据库中是否存在。
        Shop shop = this.getById(id);
        // 数据库中不存在,先将""缓存到redis，防止缓存穿透，直接返回
        if (Objects.isNull(shop)){
            stringRedisTemplate.opsForValue().set(RedisConstants.CACHE_SHOP_KEY+id,""
                    ,RedisConstants.CACHE_NULL_TTL, TimeUnit.MINUTES);
            return null;
        }
        // 4.数据库中有数据，先将数据保存到redis中，再返回
        stringRedisTemplate.opsForValue().set(RedisConstants.CACHE_SHOP_KEY+id,JSONUtil.toJsonStr(shop)
                ,RedisConstants.CACHE_SHOP_TTL+ RandomUtil.randomInt(1,10), TimeUnit.MINUTES);
        return shop;
    }

    /**
     * 互斥锁解决缓存击穿
     * @param id
     * @return
     */
    public Shop queryWithMutex(Long id){
        // 1.先从redis中查询
        String shopJson = stringRedisTemplate.opsForValue().get(RedisConstants.CACHE_SHOP_KEY + id);
        // 2.判断是否存在
        if(StrUtil.isNotBlank(shopJson)){
            // 存在shop直接返回
            return JSONUtil.toBean(shopJson,Shop.class);
        }
        // 防止缓存穿透，数据库没查到的数据都要设为 ""
        if (shopJson != null){
            return null;
        }
        // 获取互斥锁
        Shop shop = null;
        try {
            // 缓存重建
            // 是否获取到锁
            boolean isLock = tryLock(RedisConstants.LOCK_SHOP_KEY + id);
            if (!isLock){
                // 获取失败
                Thread.sleep(50);
                queryWithMutex(id);
            }
            // 获取锁成功，需要在判断key是否存在，如果存在不需要重建缓存，
            shopJson = stringRedisTemplate.opsForValue().get(RedisConstants.CACHE_SHOP_KEY + id);
            if (shopJson != null){
                // 不需要重建缓存，直接释放锁
                delLock(RedisConstants.LOCK_SHOP_KEY + id);
                return BeanUtil.toBean(shopJson,Shop.class);
            }
            // 3.缓存不存在，判断数据库中是否存在。
            shop = this.getById(id);
            // 模拟重建的延迟
            Thread.sleep(200);
            // 数据库中不存在,先将""缓存到redis，防止缓存穿透，直接返回
            if (Objects.isNull(shop)){
                stringRedisTemplate.opsForValue().set(RedisConstants.CACHE_SHOP_KEY+id,""
                        ,RedisConstants.CACHE_NULL_TTL, TimeUnit.MINUTES);
                return null;
            }
            // 4.数据库中有数据，先将数据保存到redis中，再返回
            stringRedisTemplate.opsForValue().set(RedisConstants.CACHE_SHOP_KEY+id,JSONUtil.toJsonStr(shop)
                    ,RedisConstants.CACHE_SHOP_TTL+ RandomUtil.randomInt(1,10), TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            delLock(RedisConstants.LOCK_SHOP_KEY + id);
        }
        return shop;
    }

    /**
     * 逻辑过期解决缓存击穿
     * @param id
     * @return
     */
    public Shop queryWithLogicalExpire(Long id){
        // 1.先从redis中查询
        String shopJson = stringRedisTemplate.opsForValue().get(RedisConstants.CACHE_SHOP_KEY + id);
        // 2.判断是否存在
        if(StrUtil.isBlank(shopJson)){
            // 未命中
            return null;
        }
        // 3.命中,需要判断过期时间
        RedisData redisData = JSONUtil.toBean(shopJson, RedisData.class);
        Shop shop = JSONUtil.toBean((JSONObject) redisData.getData(), Shop.class);
        LocalDateTime expireTime = redisData.getExpireTime();
        // 判断是否过期
        if (expireTime.isAfter(LocalDateTime.now())){
            // 未过期
            return shop;
        }
        // 过期，尝试获取互斥锁,重建缓存
        boolean isLock = tryLock(RedisConstants.LOCK_SHOP_KEY + id);
        // 判断是否获取互斥锁
        if (isLock){
            // 获取到锁，应该再次检测redis中是否已经存在key
            shopJson = stringRedisTemplate.opsForValue().get(RedisConstants.CACHE_SHOP_KEY + id);
            // 如果已经有数据,再判断时间是否过期
            if (StrUtil.isNotBlank(shopJson)){
                redisData = JSONUtil.toBean(shopJson,RedisData.class);
                // 未过期，释放锁，直接返回，不需要重建锁（一定要释放锁，不然会死锁）
                if (redisData.getExpireTime().isAfter(LocalDateTime.now())){
                    delLock(RedisConstants.LOCK_SHOP_KEY + id);
                    return BeanUtil.toBean(redisData.getData(),Shop.class);
                }
            }
            // 获取成功,开启新的线程，从数据库中重建缓存
            CACHE_REBUILD_EXECUTOR.submit(()->{
                try {
                    this.saveShopToRedis(id,20L);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    delLock(RedisConstants.LOCK_SHOP_KEY+id);
                }
            });
            return shop;
        }
        // 获取失败，返回过期的redis数据
        return shop;
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

    /**
     * 将缓存，逻辑过期时间写入redis
     * @param id
     * @param expireSeconds
     */
    public void saveShopToRedis(Long id,Long expireSeconds){
        Shop shop = this.getById(id);
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        RedisData redisData = new RedisData();
        redisData.setData(shop);
        // 封装逻辑过期时间
        redisData.setExpireTime(LocalDateTime.now().plusSeconds(expireSeconds));
        // 写入redis
        stringRedisTemplate.opsForValue().set(RedisConstants.CACHE_SHOP_KEY+id,JSONUtil.toJsonStr(redisData));
    }

    /**
     * 更新商铺信息
     * 先操作数据库再删除缓存
     * @param shop
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result updateShop(Shop shop) {
        if (shop.getId() == null){
            return Result.fail("店铺id不能为空");
        }
        // 更新数据库
        updateById(shop);
        // 删除缓存
        stringRedisTemplate.delete(RedisConstants.CACHE_SHOP_KEY+shop.getId());
        return Result.ok();
    }

    /**
     * 根据商铺类型分页查询商铺信息
     * @param typeId
     * @param current
     * @param sortBy
     * @param x
     * @param y
     * @return
     */
    @Override
    public Result queryShopByType(Integer typeId, Integer current,String sortBy, Double x, Double y) {
        // 按人气（评论）排序
        if ("comments".equals(sortBy)){
            // 不需要坐标查询，按数据库查询
            Page<Shop> page = query()
                    .eq("type_id", typeId)
                    .orderByDesc("comments")
                    .page(new Page<>(current, SystemConstants.DEFAULT_PAGE_SIZE));
            // 返回数据
            return Result.ok(page.getRecords());
        }
        // 按评分排序
        if ("score".equals(sortBy)){
            // 不需要坐标查询，按数据库查询
            Page<Shop> page = query()
                    .eq("type_id", typeId)
                    .orderByDesc("score")
                    .page(new Page<>(current, SystemConstants.DEFAULT_PAGE_SIZE));
            // 返回数据
            return Result.ok(page.getRecords());
        }
        // 判断是否需要根据坐标来查询
        if (x == null || y == null) {
            // 不需要坐标查询，按数据库查询
            Page<Shop> page = query()
                    .eq("type_id", typeId)
                    .page(new Page<>(current, SystemConstants.DEFAULT_PAGE_SIZE));
            // 返回数据
            return Result.ok(page.getRecords());
        }
        // 计算分页参数
        int from = (current - 1) * SystemConstants.DEFAULT_PAGE_SIZE;
        int end = current * SystemConstants.DEFAULT_PAGE_SIZE;
        // 查询redis，按照距离排序，分页结果，结果shopId ， distance GEOSEARCH key BYLONLAT x y BYRADIUS 10 WITHDISTANCE
        GeoResults<RedisGeoCommands.GeoLocation<String>> results = stringRedisTemplate.opsForGeo()
                .search(
                        RedisConstants.SHOP_GEO_KEY+typeId,
                        GeoReference.fromCoordinate(x, y),
                        new Distance(99999, Metrics.KILOMETERS),
                        RedisGeoCommands.GeoSearchCommandArgs.newGeoSearchArgs().includeDistance().limit(end)
                );
        // 根据 id 根据id 查询shopList
        if (results == null){
            return Result.ok(Collections.emptyList());
        }
        List<GeoResult<RedisGeoCommands.GeoLocation<String>>> list = results.getContent();
        // 说明后面没有数据了
        if (list.size() <= from) {
            // 没有下一页了，结束
            return Result.ok(Collections.emptyList());
        }
        // 截取自己想要的内容,收集ids
        List<Long> ids = new ArrayList<>(list.size());
        Map<String, Distance> distanceMap = new HashMap<>(list.size());
        list.stream().skip(from).forEach(result -> {
            // 获取店铺id
            String shopIdStr = result.getContent().getName();
            ids.add(Long.valueOf(shopIdStr));
            // 获取距离
            Distance distance = result.getDistance();
            distanceMap.put(shopIdStr, distance);
        });
        // 根据id查询Shop
        String idStr = StrUtil.join(",", ids);
        // 根据id查询店铺，保证有序
        List<Shop> shops = query().in("id", ids).last("ORDER BY FIELD(id," + idStr + ")").list();
        for (Shop shop : shops) {
            shop.setDistance(distanceMap.get(shop.getId().toString()).getValue());
        }
        return Result.ok(shops);
    }
}
