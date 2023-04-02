package com.sky.service.impl;

import cn.hutool.json.JSONUtil;
import com.sky.dto.Result;
import com.sky.pojo.ShopType;
import com.sky.mapper.ShopTypeMapper;
import com.sky.service.IShopTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.utils.RedisConstants;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 返回商铺类型
     * 一开始用的是set数据类型，发现他是无序的，只能用list或者zset，zset通过score来控制sort
     * @return
     */
    @Override
    public Result queryList() {
        List<String> typeStrList;
        List<ShopType> typeList = null;
        // 1.先从redis中查询是否存在
        typeStrList = stringRedisTemplate.opsForList().range(RedisConstants.CACHE_SHOP_TYPE_KEY, 0, -1);
        if (typeStrList != null && typeStrList.size() > 0){
            // 因为取出来的数据都是String类型的，需要转换为对象
            typeList = typeStrList
                    .stream()
                    .map(s -> JSONUtil.toBean(s,ShopType.class)).collect(Collectors.toList());
        }
        // 2.存在直接返回
        if (typeList != null && typeList.size() > 0){
            return Result.ok(typeList);
        }
        // 3.不存在，再进行数据库查询
        typeList = this.query().orderByAsc("sort").list();
        // 4.如果数据库中没数据，直接返回无数据
        if (typeList.size() == 0){
            return Result.fail("当前无数据");
        }
        // 5.数据库中有数据，将数据添加到redis中，再返回
        stringRedisTemplate.opsForList()
                .rightPushAll(RedisConstants.CACHE_SHOP_TYPE_KEY, typeList.stream().map(JSONUtil::toJsonStr).collect(Collectors.toList()));
        return Result.ok(typeList);
    }
}
