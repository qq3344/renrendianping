package com.sky.service;

import com.sky.dto.Result;
import com.sky.pojo.Shop;
import com.baomidou.mybatisplus.extension.service.IService;

public interface IShopService extends IService<Shop> {

    /**
     * 根据id返回商铺信息
     * @param id
     * @return
     */
    Result queryById(Long id);

    /**
     * 更新商铺信息
     * @param shop
     * @return
     */
    Result updateShop(Shop shop);

    /**
     * 根据商铺类型分页查询商铺信息
     * @param typeId
     * @param current
     * @param sortBy
     * @param x
     * @param y
     * @return
     */
    Result queryShopByType(Integer typeId, Integer current,String sortBy, Double x, Double y);
}
